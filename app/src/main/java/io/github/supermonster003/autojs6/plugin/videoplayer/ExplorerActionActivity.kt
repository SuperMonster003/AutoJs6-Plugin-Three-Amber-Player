package io.github.supermonster003.autojs6.plugin.videoplayer

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

/** Signature-protected boundary for Explorer Action protocol v2. */
class ExplorerActionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val request = AndroidVideoIntentPolicy.resolveExplorer(intent)
            if (request == null) {
                Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
            } else {
                val spec = PlaybackForwardingPolicy.fromExplorer(
                    ValidatedExplorerRequest(
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
            }
        }
        finish()
    }
}
