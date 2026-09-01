package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

/** Public ACTION_VIEW boundary. It rebuilds, rather than forwards, the incoming Intent. */
class ExternalViewerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && !isSelfReferrer()) {
            val request = AndroidVideoIntentPolicy.resolveExternal(intent)
            if (request == null) {
                Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
            } else {
                val metadata = ContentPlaybackMetadataResolver.resolve(this, request.targetUri)
                val spec = PlaybackForwardingPolicy.fromExternal(
                    ValidatedExternalRequest(request.targetUri.toString(), request.mimeType),
                    metadata.displayName,
                    metadata.declaredSize,
                )
                runCatching { startActivity(VideoIntentFactory.createInternal(this, spec)) }
                    .onFailure {
                        Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
                    }
            }
        }
        finish()
    }

    private fun isSelfReferrer(): Boolean = runCatching {
        referrer?.scheme == "android-app" && referrer?.host == packageName
    }.getOrDefault(false)
}
