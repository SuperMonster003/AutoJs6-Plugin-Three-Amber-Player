package io.github.supermonster003.autojs6.plugin.videoplayer

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.core.net.toUri
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras

internal data class AndroidExplorerRequest(
    val targetUri: Uri,
    val parentUri: Uri,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

internal data class AndroidExternalRequest(
    val targetUri: Uri,
    val mimeType: String,
)

internal data class AndroidPlaybackRequest(
    val targetUri: Uri,
    val displayName: String,
    val mimeType: String,
)

internal object AndroidVideoIntentPolicy {

    fun resolveExplorer(intent: Intent?): AndroidExplorerRequest? = runCatching {
        intent ?: return null
        if (intent.selector != null) return null
        val targetUri = intent.data ?: return null
        val parentUri = intent.parcelableUriExtra(ExplorerActionIntentExtras.PARENT_URI) ?: return null
        val validated = VideoRequestPolicy.validateExplorer(
            ExplorerRequestEnvelope(
                action = intent.action,
                actionId = intent.stringExtra(ExplorerActionIntentExtras.ACTION_ID),
                protocolVersion = intent.intExtraOrNull(ExplorerActionIntentExtras.PROTOCOL_VERSION),
                sourceSurface = intent.stringExtra(ExplorerActionIntentExtras.SOURCE_SURFACE),
                hostVersionCode = intent.longExtraOrNull(ExplorerActionIntentExtras.HOST_VERSION_CODE),
                targetUri = targetUri.toString(),
                parentUri = parentUri.toString(),
                clipItems = intent.clipItems(),
                grants = intent.grantEnvelope(),
                displayName = intent.stringExtra(ExplorerActionIntentExtras.DISPLAY_NAME),
                declaredSize = intent.longExtraOrNull(ExplorerActionIntentExtras.SIZE),
                mimeType = intent.type,
            ),
        ) ?: return null
        AndroidExplorerRequest(
            targetUri = targetUri,
            parentUri = parentUri,
            displayName = validated.displayName,
            declaredSize = validated.declaredSize,
            mimeType = validated.mimeType,
        )
    }.getOrNull()

    fun resolveExternal(intent: Intent?): AndroidExternalRequest? = runCatching {
        intent ?: return null
        if (intent.selector != null) return null
        val targetUri = intent.data ?: return null
        val validated = VideoRequestPolicy.validateExternal(
            ExternalRequestEnvelope(
                action = intent.action,
                targetUri = targetUri.toString(),
                grants = intent.grantEnvelope(),
                mimeType = intent.type,
            ),
        ) ?: return null
        AndroidExternalRequest(targetUri, validated.mimeType)
    }.getOrNull()

    fun resolveInternal(intent: Intent?): AndroidPlaybackRequest? = runCatching {
        intent ?: return null
        if (intent.selector != null) return null
        val targetUri = intent.data ?: return null
        val validated = VideoRequestPolicy.validateInternal(
            InternalRequestEnvelope(
                action = intent.action,
                targetUri = targetUri.toString(),
                clipItems = intent.clipItems(),
                grants = intent.grantEnvelope(),
                displayName = intent.stringExtra(VideoRequestPolicy.DISPLAY_NAME_EXTRA),
                mimeType = intent.type,
                extraKeys = intent.extras?.keySet()?.toSet().orEmpty(),
            ),
        ) ?: return null
        AndroidPlaybackRequest(targetUri, validated.displayName, validated.mimeType)
    }.getOrNull()

    private fun Intent.grantEnvelope() = GrantEnvelope(
        read = flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0,
        write = flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION != 0,
        persistable = flags and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION != 0,
        prefix = flags and Intent.FLAG_GRANT_PREFIX_URI_PERMISSION != 0,
    )

    private fun Intent.clipItems(): List<ClipItemEnvelope> {
        val clip = clipData ?: return emptyList()
        return (0 until clip.itemCount).map { index ->
            val item = clip.getItemAt(index)
            ClipItemEnvelope(
                uri = item.uri?.toString(),
                hasText = item.text != null,
                hasHtmlText = item.htmlText != null,
                hasIntent = item.intent != null,
            )
        }
    }

    private fun Intent.stringExtra(name: String): String? =
        getStringExtra(name)

    private fun Intent.intExtraOrNull(name: String): Int? =
        if (hasExtra(name)) getIntExtra(name, Int.MIN_VALUE) else null

    private fun Intent.longExtraOrNull(name: String): Long? =
        if (hasExtra(name)) getLongExtra(name, Long.MIN_VALUE) else null

    @Suppress("DEPRECATION")
    private fun Intent.parcelableUriExtra(name: String): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(name, Uri::class.java)
        } else {
            getParcelableExtra(name)
        }
}

internal object VideoIntentFactory {

    fun createInternal(context: Context, spec: ForwardedPlaybackSpec): Intent {
        check(spec.action == VideoRequestPolicy.INTERNAL_PLAY_ACTION)
        check(spec.clipUris == listOf(spec.targetUri))
        check(spec.grantRead && !spec.grantWrite && !spec.grantPersistable && !spec.grantPrefix)
        check(spec.extras.keys == setOf(VideoRequestPolicy.DISPLAY_NAME_EXTRA))

        val targetUri = spec.targetUri.toUri()
        return Intent(context, VideoPlayerActivity::class.java).apply {
            action = spec.action
            setDataAndType(targetUri, spec.mimeType)
            clipData = ClipData.newRawUri(spec.displayName, targetUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(VideoRequestPolicy.DISPLAY_NAME_EXTRA, spec.displayName)
        }
    }
}

internal object ContentDisplayNameResolver {

    fun resolve(context: Context, uri: Uri): String {
        val providerName = runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
            )?.use(::readDisplayName)
        }.getOrNull()
        return VideoRequestPolicy.sanitizeExternalDisplayName(
            providerName,
            uri.lastPathSegment,
        )
    }

    private fun readDisplayName(cursor: Cursor): String? {
        if (!cursor.moveToFirst()) return null
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        return index.takeIf { it >= 0 && !cursor.isNull(it) }?.let(cursor::getString)
    }
}
