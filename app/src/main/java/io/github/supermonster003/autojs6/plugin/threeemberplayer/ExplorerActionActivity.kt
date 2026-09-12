package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistActivity
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistError
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistException
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistLoader
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistParser
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistSource
import org.autojs.plugin.explorer.api.IExplorerActionHostSession
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/** Signature-protected bridge from Explorer Action v12 to the private player. */
class ExplorerActionActivity : Activity() {

    private val worker = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val completed = AtomicBoolean(false)
    private var hostSession: IExplorerActionHostSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            finish()
            return
        }
        val request = AndroidVideoIntentPolicy.resolveExplorer(intent)
        if (request == null) {
            showInvalidRequestAndFinish()
            return
        }
        hostSession = request.hostSession
        if (request.targets.any { PlaylistParser.format(it.displayName, it.mimeType) != null }) {
            launchPlaylist(request)
            return
        }
        if (request.isSelection && request.hostSession != null) {
            launchSelection(request)
            return
        }
        if (request.hostSession == null) {
            launchSingleFile(request)
            return
        }

        mainHandler.postDelayed(
            {
                if (completed.compareAndSet(false, true)) {
                    Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
                    closeHostSession()
                    finish()
                }
            },
            DISCOVERY_TIMEOUT_MS,
        )
        worker.execute {
            val queue = runCatching { ExplorerHostMediaClient.discover(request) }
                .getOrElse { selectedOnlyQueue(request) }
            mainHandler.post {
                if (!completed.compareAndSet(false, true)) return@post
                launchHostQueue(request, queue)
            }
        }
    }

    override fun onDestroy() {
        completed.set(true)
        mainHandler.removeCallbacksAndMessages(null)
        worker.shutdownNow()
        closeHostSession()
        super.onDestroy()
    }

    private fun launchSingleFile(request: AndroidExplorerRequest) {
        val target = request.primaryTarget
        val spec = PlaybackForwardingPolicy.fromExplorer(
            ValidatedExplorerRequest(
                targetId = target.targetId,
                targetUri = target.targetUri.toString(),
                parentUri = request.parentUri.toString(),
                displayName = target.displayName,
                declaredSize = target.declaredSize,
                mimeType = target.mimeType,
            ),
        )
        runCatching { startActivity(VideoIntentFactory.createInternal(this, spec)) }
            .onFailure {
                Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
            }
        finish()
    }

    private fun launchPlaylist(request: AndroidExplorerRequest) {
        mainHandler.postDelayed({
            if (completed.compareAndSet(false, true)) {
                PlaylistActivity.showError(this, PlaylistException(PlaylistError.INVALID))
                closeHostSession()
                finish()
            }
        }, DISCOVERY_TIMEOUT_MS)
        worker.execute {
            val loaded = runCatching {
                if (request.targets.size != 1 || request.hostSession == null) throw PlaylistException(PlaylistError.INVALID)
                val target = request.primaryTarget
                PlaylistLoader.host(this, PlaylistSource(target.targetUri, target.displayName, target.mimeType),
                    request.hostSession, target.targetId, request.parentDisplayPath, PlaylistPlayback::mediaMime,
                ).also { if (it.items.isEmpty()) throw PlaylistException(PlaylistError.EMPTY) }
            }
            mainHandler.post {
                if (!completed.compareAndSet(false, true)) return@post
                loaded.onSuccess { queue ->
                    runCatching { startActivity(PlaylistPlayback.hostIntent(this, request, queue)) }
                        .onSuccess { hostSession = null; PlaylistActivity.showSkipped(this, queue) }
                        .onFailure { PlaylistActivity.showError(this, it) }
                }.onFailure { PlaylistActivity.showError(this, it) }
                finish()
            }
        }
    }

    private fun launchSelection(request: AndroidExplorerRequest) {
        runCatching {
            startActivity(VideoIntentFactory.createInternalSelection(this, request))
        }.onSuccess {
            // The player owns the bounded per-target Host Session until the queue closes.
            hostSession = null
        }.onFailure {
            Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun launchHostQueue(request: AndroidExplorerRequest, queue: DiscoveredVideoQueue) {
        runCatching {
            startActivity(VideoIntentFactory.createInternalHost(this, request, queue))
        }.onSuccess {
            // VideoPlayerActivity now owns the request-scoped capability.
            hostSession = null
        }.onFailure {
            Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun selectedOnlyQueue(request: AndroidExplorerRequest) = DiscoveredVideoQueue(
        items = listOf(
            DiscoveredVideo(
                relativePath = "",
                displayName = request.primaryTarget.displayName,
                mimeType = request.primaryTarget.mimeType,
                size = request.primaryTarget.declaredSize,
                subtitles = emptyList(),
            ),
        ),
        startIndex = 0,
    )

    private fun showInvalidRequestAndFinish() {
        Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun closeHostSession() {
        hostSession?.let { session -> runCatching { session.close() } }
        hostSession = null
    }

    private companion object {
        const val DISCOVERY_TIMEOUT_MS = 15_000L
    }
}
