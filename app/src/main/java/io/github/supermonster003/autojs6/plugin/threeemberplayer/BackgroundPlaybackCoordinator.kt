package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.content.Context
import android.content.Intent
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.exoplayer.ExoPlayer
import java.lang.ref.WeakReference

internal interface BackgroundPlaybackClient {
    fun onBackgroundPlaybackTerminated()
}

internal data class BackgroundPlaybackHandoff(
    val sessionId: String,
    val player: ExoPlayer,
    val request: AndroidPlaybackRequest,
    val subtitleSourceRegistry: SubtitleSourceRegistry,
    val loudnessEnhancer: PlayerLoudnessEnhancer,
    val hostProgressClient: HostPlaybackProgressClient?,
    val foregroundTrackSelectionParameters: TrackSelectionParameters,
    var activeMediaItemIndex: Int,
    var playbackMode: VideoPlaybackMode,
    var abLoopState: AbLoopState,
    var sleepTimerMode: SleepTimerMode,
    var sleepTimerDeadlineElapsedRealtimeMs: Long,
    var volumeBoostLevel: VolumeBoostLevel,
)

/**
 * Same-process handoff bridge. The service and activity share the existing ExoPlayer so leaving
 * the screen does not reopen the URI, lose a Host Session, or create an audible playback gap.
 */
internal object BackgroundPlaybackCoordinator {

    private var pending: BackgroundPlaybackHandoff? = null
    private var service = WeakReference<BackgroundPlaybackService>(null)
    private var preparingService = false
    private var stopPreparedWhenAttached = false
    private val clients = HashMap<String, WeakReference<BackgroundPlaybackClient>>()

    @Synchronized
    fun registerClient(sessionId: String, client: BackgroundPlaybackClient) {
        clients[sessionId] = WeakReference(client)
    }

    @Synchronized
    fun unregisterClient(sessionId: String, client: BackgroundPlaybackClient) {
        if (clients[sessionId]?.get() === client) clients.remove(sessionId)
    }

    fun stopIfDifferent(sessionId: String) {
        val abandoned = synchronized(this) {
            pending?.takeIf { it.sessionId != sessionId }?.also { pending = null }
        }
        abandoned?.let {
            releaseAbandoned(it)
            notifyTerminated(it.sessionId)
        }
        val active = synchronized(this) { service.get()?.takeIf { it.sessionId != sessionId } }
        active?.stopForReplacement()
    }

    fun prepare(context: Context, displayName: String) {
        synchronized(this) {
            if (service.get() != null || preparingService) return
            preparingService = true
            stopPreparedWhenAttached = false
        }
        val prepareIntent = Intent(context, BackgroundPlaybackService::class.java)
            .setAction(BackgroundPlaybackService.ACTION_PREPARE)
            .putExtra(BackgroundPlaybackService.EXTRA_PREPARED_TITLE, displayName)
        runCatching { context.applicationContext.startService(prepareIntent) }
            .onFailure { synchronized(this) { preparingService = false } }
    }

    fun handoff(
        context: Context,
        sourceIntent: Intent,
        value: BackgroundPlaybackHandoff,
    ): Boolean {
        if (!retainReadGrantForService(context, sourceIntent)) return false
        val preparedService = synchronized(this) {
            val active = service.get()
            if (pending != null || active?.sessionId != null) return false
            if (active == null) {
                if (!preparingService) return false
                pending = value
                return true
            }
            active
        }
        return preparedService.acceptHandoff(value)
    }

    fun playbackRequest(sessionId: String): AndroidPlaybackRequest? {
        synchronized(this) {
            pending?.takeIf { it.sessionId == sessionId }?.let { return it.request }
        }
        return synchronized(this) { service.get() }?.playbackRequest(sessionId)
    }

    @Synchronized
    fun takePendingForPreparedService(): BackgroundPlaybackHandoff? = pending.also { pending = null }

    @Synchronized
    fun attachService(value: BackgroundPlaybackService) {
        preparingService = false
        service = WeakReference(value)
    }

    @Synchronized
    fun detachService(value: BackgroundPlaybackService) {
        if (service.get() === value) service.clear()
        preparingService = false
    }

    fun reclaim(sessionId: String): BackgroundPlaybackHandoff? {
        synchronized(this) {
            pending?.takeIf { it.sessionId == sessionId }?.let { value ->
                pending = null
                return value
            }
        }
        return synchronized(this) { service.get()?.takeIf { it.sessionId == sessionId } }
            ?.reclaimForUi()
    }

    fun stopForDisabledSetting() {
        val waiting = synchronized(this) { pending.also { pending = null } }
        waiting?.let {
            releaseAbandoned(it)
            notifyTerminated(it.sessionId)
        }
        val active = synchronized(this) { service.get() }
        if (active != null) {
            active.stopForDisabledSetting()
            return
        }
    }

    fun stopPrepared() {
        val prepared = synchronized(this) {
            if (pending != null) return
            service.get()?.takeIf { it.sessionId == null }.also {
                if (it == null && preparingService) stopPreparedWhenAttached = true
            }
        }
        prepared?.stopPrepared()
    }

    @Synchronized
    fun consumeStopPreparedRequest(): Boolean = stopPreparedWhenAttached.also {
        stopPreparedWhenAttached = false
    }

    fun notifyTerminated(sessionId: String) {
        val client = synchronized(this) { clients[sessionId]?.get() }
        client?.onBackgroundPlaybackTerminated()
    }

    fun handoffFailed(value: BackgroundPlaybackHandoff) {
        releaseAbandoned(value)
        notifyTerminated(value.sessionId)
    }

    private fun releaseAbandoned(value: BackgroundPlaybackHandoff) {
        value.player.release()
        value.loudnessEnhancer.close()
        value.subtitleSourceRegistry.clear()
        value.request.hostSession?.let { session -> runCatching { session.close() } }
    }

    private fun retainReadGrantForService(context: Context, sourceIntent: Intent): Boolean {
        if (sourceIntent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0) return true
        val grantIntent = Intent(context, BackgroundPlaybackService::class.java).apply {
            action = BackgroundPlaybackService.ACTION_RETAIN_URI_GRANTS
            setDataAndType(sourceIntent.data, sourceIntent.type)
            clipData = sourceIntent.clipData
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return runCatching {
            context.applicationContext.startService(grantIntent)
        }.isSuccess
    }
}
