package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistActivity
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistLoader
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Public ACTION_VIEW boundary. It rebuilds, rather than forwards, the incoming Intent. */
class ExternalViewerActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && !isSelfReferrer()) {
            val request = AndroidVideoIntentPolicy.resolveExternal(intent)
            if (request == null) {
                Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
            } else {
                scope.launch {
                    val playbackIntent = withContext(Dispatchers.IO) {
                        val source = PlaylistLoader.source(this@ExternalViewerActivity, request.targetUri, intent.type)
                        if (PlaylistParser.format(source.displayName, source.mimeType) != null) {
                            PlaylistActivity.intent(this@ExternalViewerActivity, source)
                        } else {
                            val metadata = ContentPlaybackMetadataResolver.resolve(this@ExternalViewerActivity, request.targetUri)
                            val spec = PlaybackForwardingPolicy.fromExternal(
                                ValidatedExternalRequest(request.targetUri.toString(), request.mimeType),
                                metadata.displayName,
                                metadata.declaredSize,
                            )
                            VideoIntentFactory.createInternal(this@ExternalViewerActivity, spec)
                        }
                    }
                    runCatching { startActivity(playbackIntent) }.onFailure {
                        Toast.makeText(this@ExternalViewerActivity, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
                return
            }
        }
        finish()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun isSelfReferrer(): Boolean = runCatching {
        referrer?.scheme == "android-app" && referrer?.host == packageName
    }.getOrDefault(false)
}
