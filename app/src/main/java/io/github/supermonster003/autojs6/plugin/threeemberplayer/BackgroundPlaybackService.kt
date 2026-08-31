package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppPreferenceStore

/** Foreground mediaPlayback service used only while the explicit background-audio option is on. */
@OptIn(UnstableApi::class)
class BackgroundPlaybackService : MediaSessionService() {

    internal val sessionId: String?
        get() = handoff?.sessionId

    private val mainHandler = Handler(Looper.getMainLooper())
    private var handoff: BackgroundPlaybackHandoff? = null
    private var mediaSession: MediaSession? = null
    private var preparedTitle: CharSequence? = null
    private var foregroundStarted = false
    private var serviceState = BackgroundServiceState.IDLE
    private var transferredToUi = false
    private var trackedIndex = 0
    private var trackedPositionMs = 0L
    private var trackedDurationMs = 0L
    private var lastProgressPersistElapsedRealtimeMs = 0L

    private val permissionRunnable = object : Runnable {
        override fun run() {
            val settings = PlayerSettingsStore(this@BackgroundPlaybackService)
            if (!settings.continueAudioInBackground ||
                !BackgroundPlaybackPermissionPolicy.canPostControls(this@BackgroundPlaybackService)
            ) {
                settings.setContinueAudioInBackground(false)
                if (handoff == null) {
                    stopPrepared()
                } else {
                    terminate(BackgroundServiceEvent.SETTING_DISABLED, completed = false)
                }
                return
            }
            mainHandler.postDelayed(this, PERMISSION_CHECK_INTERVAL_MS)
        }
    }

    private val progressRunnable = object : Runnable {
        override fun run() {
            val value = handoff ?: return
            val player = value.player
            trackedIndex = player.currentMediaItemIndex
            trackedPositionMs = player.currentPosition.coerceAtLeast(0L)
            trackedDurationMs = player.duration.takeIf { it > 0L && it != C.TIME_UNSET } ?: 0L

            if (player.isPlaying) {
                AbLoopPolicy.loopTargetMs(value.abLoopState, player.currentPosition)?.let(player::seekTo)
            }
            val deadline = value.sleepTimerDeadlineElapsedRealtimeMs
            if (value.sleepTimerMode.durationMinutes != null && deadline > 0L &&
                SystemClock.elapsedRealtime() >= deadline
            ) {
                player.pause()
                value.sleepTimerMode = SleepTimerMode.OFF
                value.sleepTimerDeadlineElapsedRealtimeMs = 0L
            }
            if (SystemClock.elapsedRealtime() - lastProgressPersistElapsedRealtimeMs >=
                PROGRESS_PERSIST_INTERVAL_MS
            ) {
                persistCurrent(completed = false)
                lastProgressPersistElapsedRealtimeMs = SystemClock.elapsedRealtime()
            }
            mainHandler.postDelayed(this, PROGRESS_INTERVAL_MS)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            serviceState = BackgroundPlaybackPolicy.transition(
                serviceState,
                if (isPlaying) {
                    BackgroundServiceEvent.PLAYER_PLAYING
                } else {
                    BackgroundServiceEvent.PLAYER_PAUSED
                },
            ).state
            updateForegroundNotification()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val value = handoff ?: return
            val nextIndex = mediaItem?.mediaId?.toIntOrNull()
                ?.takeIf { it in value.request.items.indices }
                ?: value.player.currentMediaItemIndex
            if (nextIndex != value.activeMediaItemIndex) {
                persistTracked(completed = reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
                value.activeMediaItemIndex = nextIndex
                value.abLoopState = AbLoopState()
            }
            updateForegroundNotification()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                terminate(BackgroundServiceEvent.PLAYBACK_ENDED, completed = true)
            } else {
                updateForegroundNotification()
            }
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            handoff?.loudnessEnhancer?.onAudioSessionIdChanged(audioSessionId)
        }

        override fun onPlayerError(error: PlaybackException) {
            terminate(BackgroundServiceEvent.PLAYER_ERROR, completed = false)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action == ACTION_PREPARE || intent?.action == ACTION_RETAIN_URI_GRANTS) {
            BackgroundPlaybackCoordinator.attachService(this)
            if (BackgroundPlaybackCoordinator.consumeStopPreparedRequest()) {
                stopPrepared()
                return START_NOT_STICKY
            }
            if (intent.action == ACTION_PREPARE) {
                preparedTitle = intent.getStringExtra(EXTRA_PREPARED_TITLE)
                val settings = PlayerSettingsStore(this)
                if (!settings.continueAudioInBackground ||
                    !BackgroundPlaybackPermissionPolicy.canPostControls(this)
                ) {
                    settings.setContinueAudioInBackground(false)
                    stopPrepared()
                    return START_NOT_STICKY
                }
            }
            if (handoff == null) {
                val pending = BackgroundPlaybackCoordinator.takePendingForPreparedService()
                if (pending != null) {
                    if (!startPlayback(pending)) {
                        BackgroundPlaybackCoordinator.handoffFailed(pending)
                        stopPrepared()
                    }
                } else if (intent.action == ACTION_PREPARE) {
                    if (!promoteToForeground()) stopPrepared()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onUpdateNotification(
        session: MediaSession,
        startInForegroundRequired: Boolean,
    ) {
        // Notification ownership is synchronous below. Waiting for Media3's internal controller
        // connection can outlive Android's foreground-service start window while a file buffers.
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // External ACTION_VIEW tasks are intentionally excluded from Recents and Android trims
        // them immediately after Home. MediaSessionService's default implementation may stop a
        // session while ExoPlayer is temporarily buffering, so the explicit setting and our pure
        // lifecycle policy remain authoritative once ownership has been handed off.
        if (handoff == null) stopPrepared()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(progressRunnable)
        val value = handoff
        if (value != null && !transferredToUi) release(value)
        handoff = null
        stopForegroundNotification()
        releaseMediaSession()
        BackgroundPlaybackCoordinator.detachService(this)
        super.onDestroy()
    }

    internal fun reclaimForUi(): BackgroundPlaybackHandoff? {
        val value = handoff ?: return null
        val transition = BackgroundPlaybackPolicy.transition(
            serviceState,
            BackgroundServiceEvent.UI_RECLAIMED,
        )
        serviceState = transition.state
        persistCurrent(completed = false)
        mainHandler.removeCallbacks(progressRunnable)
        value.player.removeListener(playerListener)
        value.player.setWakeMode(C.WAKE_MODE_NONE)
        value.player.trackSelectionParameters = value.foregroundTrackSelectionParameters
        releaseMediaSession()
        handoff = null
        transferredToUi = true
        return value
    }

    internal fun stopForDisabledSetting() {
        if (handoff == null) stopPrepared()
        else terminate(BackgroundServiceEvent.SETTING_DISABLED, completed = false)
    }

    internal fun stopForReplacement() {
        terminate(BackgroundServiceEvent.SETTING_DISABLED, completed = false)
    }

    internal fun acceptHandoff(value: BackgroundPlaybackHandoff): Boolean {
        if (handoff != null) return false
        return startPlayback(value)
    }

    internal fun playbackRequest(requestedSessionId: String): AndroidPlaybackRequest? =
        handoff?.takeIf { it.sessionId == requestedSessionId }?.request

    internal fun stopPrepared() {
        if (handoff != null) return
        stopForegroundNotification()
        BackgroundPlaybackCoordinator.detachService(this)
        stopSelf()
    }

    private fun startPlayback(value: BackgroundPlaybackHandoff): Boolean {
        val settings = PlayerSettingsStore(this)
        if (!settings.continueAudioInBackground ||
            !BackgroundPlaybackPermissionPolicy.canPostControls(this)
        ) {
            settings.setContinueAudioInBackground(false)
            return false
        }
        handoff = value
        transferredToUi = false
        if (!foregroundStarted && !promoteToForeground()) {
            handoff = null
            return false
        }
        return runCatching {
            trackedIndex = value.player.currentMediaItemIndex
            trackedPositionMs = value.player.currentPosition.coerceAtLeast(0L)
            trackedDurationMs = value.player.duration.takeIf {
                it > 0L && it != C.TIME_UNSET
            } ?: 0L
            value.player.trackSelectionParameters = value.foregroundTrackSelectionParameters
                .buildUpon()
                .setTrackTypeDisabled(C.TRACK_TYPE_VIDEO, true)
                .build()
            value.player.setPauseAtEndOfMediaItems(false)
            value.player.setWakeMode(C.WAKE_MODE_LOCAL)
            value.player.addListener(playerListener)
            mediaSession = MediaSession.Builder(this, value.player)
                .setId("$MEDIA_SESSION_ID_PREFIX${value.sessionId}")
                .setSessionActivity(sessionActivity(value))
                .build()
                .also(::addSession)
            preparedTitle = null
            BackgroundPlaybackCoordinator.attachService(this)
            serviceState = BackgroundPlaybackPolicy.transition(
                serviceState,
                BackgroundServiceEvent.HANDOFF_PLAYING,
            ).state
            mainHandler.post(progressRunnable)
            // PlayerNotificationManager detaches immediately after this handoff and shares the
            // same notification id. Reposting on the next main-loop turn keeps the service-owned
            // notification visible without exposing duplicate playback notifications.
            mainHandler.post(::updateForegroundNotification)
        }.fold(
            onSuccess = { true },
            onFailure = {
                mainHandler.removeCallbacks(progressRunnable)
                value.player.removeListener(playerListener)
                value.player.setWakeMode(C.WAKE_MODE_NONE)
                value.player.trackSelectionParameters = value.foregroundTrackSelectionParameters
                releaseMediaSession()
                handoff = null
                serviceState = BackgroundServiceState.IDLE
                false
            },
        )
    }

    private fun sessionActivity(value: BackgroundPlaybackHandoff): PendingIntent {
        val target = Intent(this, VideoPlayerActivity::class.java).apply {
            action = ACTION_RESUME_BACKGROUND_PLAYBACK
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(EXTRA_SESSION_ID, value.sessionId)
        }
        return PendingIntent.getActivity(
            this,
            value.sessionId.hashCode(),
            target,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun terminate(event: BackgroundServiceEvent, completed: Boolean) {
        val value = handoff ?: return
        val transition = BackgroundPlaybackPolicy.transition(serviceState, event)
        serviceState = transition.state
        if (!transition.stopService) return
        if (completed) persistCurrent(completed = true) else persistCurrent(completed = false)
        mainHandler.removeCallbacks(progressRunnable)
        value.player.removeListener(playerListener)
        stopForegroundNotification()
        releaseMediaSession()
        handoff = null
        if (transition.releasePlayer) release(value)
        BackgroundPlaybackCoordinator.detachService(this)
        BackgroundPlaybackCoordinator.notifyTerminated(value.sessionId)
        stopSelf()
    }

    private fun releaseMediaSession() {
        mediaSession?.release()
        mediaSession = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.media_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.media_notification_channel_description)
                setSound(null, null)
                enableVibration(false)
                setShowBadge(false)
            },
        )
    }

    private fun promoteToForeground(): Boolean {
        val notification = buildNotification()
        if (foregroundStarted) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
                NOTIFICATION_ID,
                notification,
            )
            return true
        }
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            foregroundStarted = true
            mainHandler.removeCallbacks(permissionRunnable)
            mainHandler.post(permissionRunnable)
        }.isSuccess
    }

    private fun updateForegroundNotification() {
        if (!foregroundStarted) {
            if (handoff != null) promoteToForeground()
            return
        }
        val notification = buildNotification()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
            NOTIFICATION_ID,
            notification,
        )
    }

    private fun buildNotification(): Notification {
        val value = handoff
        val player = value?.player
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }.setSmallIcon(R.drawable.ic_notification_video)
            .setContentTitle(
                player?.currentMediaItem?.mediaMetadata?.displayTitle
                    ?: player?.currentMediaItem?.mediaMetadata?.title
                    ?: value?.request?.displayName
                    ?: preparedTitle
                    ?: getString(R.string.app_name),
            )
            .setContentText(getString(R.string.app_name))
            .setContentIntent(value?.let(::sessionActivity) ?: launcherActivity())
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setShowWhen(false)

        if (value == null || player == null) return builder.build()

        val compactIndexes = ArrayList<Int>(3)
        fun addMediaAction(icon: Int, title: Int, keyCode: Int) {
            compactIndexes += compactIndexes.size
            builder.addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(this, icon),
                    getString(title),
                    mediaButtonPendingIntent(keyCode),
                ).build(),
            )
        }
        if (player.isCommandAvailable(Player.COMMAND_SEEK_TO_PREVIOUS)) {
            addMediaAction(R.drawable.ic_previous, R.string.action_previous_video, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        }
        addMediaAction(
            if (player.playWhenReady) R.drawable.ic_pause else R.drawable.ic_play_arrow,
            if (player.playWhenReady) R.string.action_pause else R.string.action_play,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        )
        if (player.isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT)) {
            addMediaAction(R.drawable.ic_next, R.string.action_next_video, KeyEvent.KEYCODE_MEDIA_NEXT)
        }
        builder.setStyle(
            Notification.MediaStyle().apply {
                mediaSession?.platformToken?.let(::setMediaSession)
                setShowActionsInCompactView(*compactIndexes.toIntArray())
            },
        )
        return builder.build()
    }

    private fun launcherActivity(): PendingIntent = PendingIntent.getActivity(
        this,
        PREPARED_NOTIFICATION_REQUEST_CODE,
        Intent(this, LauncherActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun mediaButtonPendingIntent(keyCode: Int): PendingIntent {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, this, javaClass).apply {
            putExtra(
                Intent.EXTRA_KEY_EVENT,
                KeyEvent(KeyEvent.ACTION_DOWN, keyCode),
            )
        }
        return PendingIntent.getService(
            this,
            keyCode,
            mediaButtonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun stopForegroundNotification() {
        mainHandler.removeCallbacks(permissionRunnable)
        if (foregroundStarted) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            foregroundStarted = false
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(NOTIFICATION_ID)
    }

    private fun release(value: BackgroundPlaybackHandoff) {
        value.player.setWakeMode(C.WAKE_MODE_NONE)
        value.player.release()
        value.loudnessEnhancer.close()
        value.subtitleSourceRegistry.clear()
        value.request.hostSession?.let { session -> runCatching { session.close() } }
    }

    private fun persistCurrent(completed: Boolean) {
        val value = handoff ?: return
        val player = value.player
        val duration = player.duration.takeIf { it > 0L && it != C.TIME_UNSET } ?: return
        persist(
            value = value,
            index = player.currentMediaItemIndex,
            positionMs = if (completed) duration else player.currentPosition.coerceAtLeast(0L),
            durationMs = duration,
            completed = completed,
        )
    }

    private fun persistTracked(completed: Boolean) {
        val value = handoff ?: return
        if (trackedDurationMs <= 0L) return
        persist(
            value = value,
            index = trackedIndex,
            positionMs = if (completed) trackedDurationMs else trackedPositionMs,
            durationMs = trackedDurationMs,
            completed = completed,
        )
    }

    private fun persist(
        value: BackgroundPlaybackHandoff,
        index: Int,
        positionMs: Long,
        durationMs: Long,
        completed: Boolean,
    ) {
        val item = value.request.items.getOrNull(index) ?: return
        val appPreferences = AppPreferenceStore(this)
        val positionStore = PlaybackPositionStore(this)
        if (!appPreferences.rememberPlaybackPosition) {
            positionStore.clearAll()
            return
        }
        val boundedPosition = positionMs.coerceIn(0L, durationMs)
        val handledByHost = item.relativePath?.let { relativePath ->
            item.hostTargetId?.let { targetId ->
                value.hostProgressClient?.report(
                    targetId,
                    relativePath,
                    boundedPosition,
                    durationMs,
                    completed,
                )
            }
        }
        if (handledByHost == true ||
            value.hostProgressClient?.state == HostPlaybackHistoryState.DISABLED
        ) {
            return
        }
        val identity = if (item.hostTargetId != null && item.relativePath != null) {
            "explorer-session:${item.hostTargetId}:${item.relativePath}"
        } else {
            item.externalUri?.toString() ?: item.sourceUri.toString()
        }
        if (completed) positionStore.clear(identity) else {
            positionStore.save(identity, boundedPosition, durationMs)
        }
    }

    internal companion object {
        const val ACTION_PREPARE =
            "io.github.supermonster003.autojs6.plugin.threeemberplayer.action.PREPARE_BACKGROUND_PLAYBACK"
        const val ACTION_RETAIN_URI_GRANTS =
            "io.github.supermonster003.autojs6.plugin.threeemberplayer.action.RETAIN_BACKGROUND_URI_GRANTS"
        const val ACTION_RESUME_BACKGROUND_PLAYBACK =
            "io.github.supermonster003.autojs6.plugin.threeemberplayer.action.RESUME_BACKGROUND_PLAYBACK"
        const val EXTRA_SESSION_ID = "background_playback_session_id"
        const val EXTRA_PREPARED_TITLE = "background_playback_prepared_title"
        internal const val NOTIFICATION_CHANNEL_ID = "background_audio_playback"
        internal const val NOTIFICATION_ID = 6_004
        private const val MEDIA_SESSION_ID_PREFIX = "background-audio-"
        private const val PREPARED_NOTIFICATION_REQUEST_CODE = 6_004
        private const val PROGRESS_INTERVAL_MS = 250L
        private const val PROGRESS_PERSIST_INTERVAL_MS = 10_000L
        private const val PERMISSION_CHECK_INTERVAL_MS = 1_000L
    }
}
