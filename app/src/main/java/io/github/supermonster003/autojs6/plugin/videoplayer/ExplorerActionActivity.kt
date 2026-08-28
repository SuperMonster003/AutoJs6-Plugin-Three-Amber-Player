package io.github.supermonster003.autojs6.plugin.videoplayer

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
        val spec = PlaybackForwardingPolicy.fromExplorer(
            ValidatedExplorerRequest(
                targetId = request.targetId,
                targetUri = request.targetUri.toString(),
                parentUri = request.parentUri.toString(),
                displayName = request.displayName,
                declaredSize = request.declaredSize,
                mimeType = request.mimeType,
            ),
        )
        runCatching { startActivity(VideoIntentFactory.createInternal(this, spec)) }
            .onFailure {
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
                displayName = request.displayName,
                mimeType = request.mimeType,
                size = request.declaredSize,
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
