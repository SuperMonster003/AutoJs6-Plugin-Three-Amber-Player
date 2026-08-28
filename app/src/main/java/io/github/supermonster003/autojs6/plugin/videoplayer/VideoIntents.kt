package io.github.supermonster003.autojs6.plugin.videoplayer

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.core.net.toUri
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionTargetKeys
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal data class AndroidExplorerRequest(
    val targetId: String,
    val targetUri: Uri,
    val parentUri: Uri,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
    val hostSession: IExplorerActionHostSession?,
)

internal data class AndroidExternalRequest(
    val targetUri: Uri,
    val mimeType: String,
)

internal data class AndroidPlaybackRequest(
    val items: List<AndroidPlaybackItem>,
    val startIndex: Int,
    val hostSession: IExplorerActionHostSession? = null,
    val hostTargetId: String? = null,
) {
    init {
        require(items.size in 1..HostMediaDiscoveryPolicy.MAX_QUEUE_ENTRIES && startIndex in items.indices)
        require((hostSession == null) == (hostTargetId == null))
        if (hostSession == null) {
            require(items.size == 1 && items.single().relativePath == null && items.single().subtitles.isEmpty())
        } else {
            require(items.all { it.relativePath != null })
        }
    }

    val initialItem: AndroidPlaybackItem
        get() = items[startIndex]

    val targetUri: Uri
        get() = initialItem.sourceUri

    val displayName: String
        get() = initialItem.displayName

    val declaredSize: Long
        get() = initialItem.declaredSize

    val mimeType: String
        get() = initialItem.mimeType
}

internal data class AndroidPlaybackItem(
    val sourceUri: Uri,
    val externalUri: Uri?,
    val relativePath: String?,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
    val subtitles: List<AndroidPlaybackSubtitle> = emptyList(),
)

internal data class AndroidPlaybackSubtitle(
    val sourceUri: Uri,
    val relativePath: String,
    val displayName: String,
    val mimeType: String,
    val languageTag: String?,
)

internal data class ContentPlaybackMetadata(
    val displayName: String,
    val declaredSize: Long,
)

internal object AndroidVideoIntentPolicy {

    fun resolveExplorer(intent: Intent?): AndroidExplorerRequest? = runCatching {
        intent ?: return null
        if (intent.selector != null) return null
        val targetUri = intent.data ?: return null
        val parentUri = intent.parcelableUriExtra(ExplorerActionIntentExtras.PARENT_URI) ?: return null
        val targetBundles = intent.parcelableBundleArrayListExtra(ExplorerActionIntentExtras.TARGETS)
            ?.takeIf { it.size == 1 }
            ?: return null
        val target = targetBundles.single()
        val bundledUri = target.parcelableUri(ExplorerActionTargetKeys.URI) ?: return null
        if (bundledUri != targetUri) return null
        val displayName = target.getString(ExplorerActionTargetKeys.DISPLAY_NAME) ?: return null
        val declaredSize = target.getLong(ExplorerActionTargetKeys.SIZE, Long.MIN_VALUE)
        val mimeType = target.getString(ExplorerActionTargetKeys.MIME_TYPE) ?: return null
        if (intent.stringExtra(ExplorerActionIntentExtras.DISPLAY_NAME) != displayName) return null
        if (intent.longExtraOrNull(ExplorerActionIntentExtras.SIZE) != declaredSize) return null
        if (intent.type != mimeType) return null
        val hostSessionBundle = intent.parcelableBundleExtra(ExplorerActionIntentExtras.HOST_SESSION)
        val hostSession = if (hostSessionBundle == null) {
            null
        } else {
            val binder = hostSessionBundle.getBinder(ExplorerActionHostSessionKeys.BINDER) ?: return null
            if (runCatching { binder.interfaceDescriptor }.getOrNull() != IExplorerActionHostSession.DESCRIPTOR) {
                return null
            }
            IExplorerActionHostSession.Stub.asInterface(binder)
        }
        val validated = VideoRequestPolicy.validateExplorer(
            ExplorerRequestEnvelope(
                action = intent.action,
                actionId = intent.stringExtra(ExplorerActionIntentExtras.ACTION_ID),
                protocolVersion = intent.intExtraOrNull(ExplorerActionIntentExtras.PROTOCOL_VERSION),
                sourceSurface = intent.stringExtra(ExplorerActionIntentExtras.SOURCE_SURFACE),
                hostVersionCode = intent.longExtraOrNull(ExplorerActionIntentExtras.HOST_VERSION_CODE),
                requestId = intent.stringExtra(ExplorerActionIntentExtras.REQUEST_ID),
                targetUri = targetUri.toString(),
                parentUri = parentUri.toString(),
                parentDisplayPath = intent.stringExtra(ExplorerActionIntentExtras.PARENT_DISPLAY_PATH),
                clipItems = intent.clipItems(),
                grants = intent.grantEnvelope(),
                targetCount = targetBundles.size,
                targetId = target.getString(ExplorerActionTargetKeys.ID),
                targetKind = target.getInt(ExplorerActionTargetKeys.KIND, Int.MIN_VALUE),
                displayName = displayName,
                declaredSize = declaredSize,
                lastModified = target.getLong(ExplorerActionTargetKeys.LAST_MODIFIED, Long.MIN_VALUE),
                mimeType = mimeType,
            ),
        ) ?: return null
        AndroidExplorerRequest(
            targetId = validated.targetId,
            targetUri = targetUri,
            parentUri = parentUri,
            displayName = validated.displayName,
            declaredSize = validated.declaredSize,
            mimeType = validated.mimeType,
            hostSession = hostSession,
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
        val hostBundle = intent.parcelableBundleExtra(VideoIntentFactory.HOST_REQUEST_EXTRA)
        val baseExtraKeys = setOf(
            VideoRequestPolicy.DISPLAY_NAME_EXTRA,
            VideoRequestPolicy.DECLARED_SIZE_EXTRA,
        )
        val expectedExtraKeys = if (hostBundle == null) {
            baseExtraKeys
        } else {
            baseExtraKeys + VideoIntentFactory.HOST_REQUEST_EXTRA
        }
        if (intent.extras?.keySet()?.toSet().orEmpty() != expectedExtraKeys) return null
        val validated = VideoRequestPolicy.validateInternal(
            InternalRequestEnvelope(
                action = intent.action,
                targetUri = targetUri.toString(),
                clipItems = intent.clipItems(),
                grants = intent.grantEnvelope(),
                displayName = intent.stringExtra(VideoRequestPolicy.DISPLAY_NAME_EXTRA),
                declaredSize = intent.longExtraOrNull(VideoRequestPolicy.DECLARED_SIZE_EXTRA),
                mimeType = intent.type,
                extraKeys = baseExtraKeys,
            ),
        ) ?: return null
        if (hostBundle == null) {
            return@runCatching AndroidPlaybackRequest(
                items = listOf(
                    AndroidPlaybackItem(
                        sourceUri = targetUri,
                        externalUri = targetUri,
                        relativePath = null,
                        displayName = validated.displayName,
                        declaredSize = validated.declaredSize,
                        mimeType = validated.mimeType,
                    ),
                ),
                startIndex = 0,
            )
        }
        VideoIntentFactory.resolveHostRequest(hostBundle, targetUri, validated)
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
    private fun Intent.parcelableBundleExtra(name: String): Bundle? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(name, Bundle::class.java)
        } else {
            getParcelableExtra(name)
        }

    @Suppress("DEPRECATION")
    private fun Intent.parcelableBundleArrayListExtra(name: String): ArrayList<Bundle>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayListExtra(name, Bundle::class.java)
        } else {
            getParcelableArrayListExtra(name)
        }

    @Suppress("DEPRECATION")
    private fun Bundle.parcelableUri(name: String): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(name, Uri::class.java)
        } else {
            getParcelable(name)
        }

    @Suppress("DEPRECATION")
    private fun Intent.parcelableUriExtra(name: String): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(name, Uri::class.java)
        } else {
            getParcelableExtra(name)
        }
}

internal object VideoIntentFactory {

    const val HOST_REQUEST_EXTRA =
        "io.github.supermonster003.autojs6.plugin.videoplayer.extra.HOST_REQUEST"

    private const val HOST_TARGET_ID = "targetId"
    private const val HOST_START_INDEX = "startIndex"
    private const val HOST_ITEMS = "items"
    private const val HOST_RELATIVE_PATH = "relativePath"
    private const val HOST_DISPLAY_NAME = "displayName"
    private const val HOST_MIME_TYPE = "mimeType"
    private const val HOST_SIZE = "size"
    private const val HOST_SUBTITLES = "subtitles"
    private const val HOST_LANGUAGE_TAG = "languageTag"
    private const val HOST_VIDEO_AUTHORITY = "video"
    private const val HOST_SUBTITLE_AUTHORITY = "subtitle"
    const val HOST_SOURCE_SCHEME = "autojs6-explorer"

    fun createInternal(context: Context, spec: ForwardedPlaybackSpec): Intent {
        check(spec.action == VideoRequestPolicy.INTERNAL_PLAY_ACTION)
        check(spec.clipUris == listOf(spec.targetUri))
        check(spec.grantRead && !spec.grantWrite && !spec.grantPersistable && !spec.grantPrefix)
        check(
            spec.extras.keys == setOf(
                VideoRequestPolicy.DISPLAY_NAME_EXTRA,
                VideoRequestPolicy.DECLARED_SIZE_EXTRA,
            ),
        )

        val targetUri = spec.targetUri.toUri()
        return Intent(context, VideoPlayerActivity::class.java).apply {
            action = spec.action
            setDataAndType(targetUri, spec.mimeType)
            clipData = ClipData.newRawUri(spec.displayName, targetUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(VideoRequestPolicy.DISPLAY_NAME_EXTRA, spec.displayName)
            putExtra(VideoRequestPolicy.DECLARED_SIZE_EXTRA, spec.declaredSize)
        }
    }

    fun createInternalHost(
        context: Context,
        request: AndroidExplorerRequest,
        queue: DiscoveredVideoQueue,
    ): Intent {
        val session = requireNotNull(request.hostSession)
        require(queue.items.isNotEmpty() && queue.startIndex in queue.items.indices)
        require(queue.items[queue.startIndex].displayName == request.displayName)
        val targetUri = request.targetUri
        return Intent(context, VideoPlayerActivity::class.java).apply {
            action = VideoRequestPolicy.INTERNAL_PLAY_ACTION
            setDataAndType(targetUri, request.mimeType)
            clipData = ClipData.newRawUri(request.displayName, targetUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(VideoRequestPolicy.DISPLAY_NAME_EXTRA, request.displayName)
            putExtra(VideoRequestPolicy.DECLARED_SIZE_EXTRA, request.declaredSize)
            putExtra(
                HOST_REQUEST_EXTRA,
                Bundle().apply {
                    putBinder(ExplorerActionHostSessionKeys.BINDER, session.asBinder())
                    putString(HOST_TARGET_ID, request.targetId)
                    putInt(HOST_START_INDEX, queue.startIndex)
                    putParcelableArrayList(
                        HOST_ITEMS,
                        ArrayList(queue.items.map { item -> item.toHostBundle() }),
                    )
                },
            )
        }
    }

    fun resolveHostRequest(
        bundle: Bundle,
        selectedContentUri: Uri,
        selected: ValidatedPlaybackRequest,
    ): AndroidPlaybackRequest? {
        if (bundle.keySet().toSet() != HOST_REQUEST_KEYS) return null
        val binder = bundle.getBinder(ExplorerActionHostSessionKeys.BINDER) ?: return null
        if (runCatching { binder.interfaceDescriptor }.getOrNull() != IExplorerActionHostSession.DESCRIPTOR) {
            return null
        }
        val session = IExplorerActionHostSession.Stub.asInterface(binder)
        val targetId = bundle.getString(HOST_TARGET_ID)
            ?.takeIf { value ->
                value.length in 1..128 && value.none { character ->
                    character.isWhitespace() || character.isISOControl() ||
                        Character.getType(character) == Character.FORMAT.toInt()
                }
            }
            ?: return null
        val itemBundles = bundle.bundleArrayList(HOST_ITEMS)
            ?.takeIf { it.size in 1..HostMediaDiscoveryPolicy.MAX_QUEUE_ENTRIES }
            ?: return null
        val startIndex = bundle.getInt(HOST_START_INDEX, -1).takeIf { it in itemBundles.indices }
            ?: return null
        var totalSubtitleAttachments = 0
        val items = itemBundles.mapIndexed { index, itemBundle ->
            if (itemBundle.keySet().toSet() != HOST_ITEM_KEYS) return null
            val relativePath = itemBundle.getString(HOST_RELATIVE_PATH) ?: return null
            if (relativePath.isNotEmpty() && !isSafeHostName(relativePath)) return null
            val displayName = itemBundle.getString(HOST_DISPLAY_NAME)
                ?.takeIf(::isSafeHostName)
                ?: return null
            if (index == startIndex) {
                if (relativePath.isNotEmpty()) return null
            } else if (relativePath != displayName) {
                return null
            }
            val mimeType = VideoRequestPolicy.normalizeVideoMimeType(
                itemBundle.getString(HOST_MIME_TYPE),
            ) ?: return null
            val size = itemBundle.getLong(HOST_SIZE, Long.MIN_VALUE)
                .takeIf { it >= VideoRequestPolicy.UNKNOWN_DECLARED_SIZE }
                ?: return null
            val subtitleBundles = itemBundle.bundleArrayList(HOST_SUBTITLES)
                ?.takeIf { it.size <= HostMediaDiscoveryPolicy.MAX_SUBTITLES_PER_VIDEO }
                ?: return null
            totalSubtitleAttachments += subtitleBundles.size
            if (totalSubtitleAttachments > HostMediaDiscoveryPolicy.MAX_TOTAL_SUBTITLE_ATTACHMENTS) return null
            val subtitles = subtitleBundles.mapIndexed {
                    subtitleIndex,
                    subtitleBundle,
                ->
                val subtitleKeys = subtitleBundle.keySet().toSet()
                if (subtitleKeys != HOST_SUBTITLE_KEYS && subtitleKeys != HOST_SUBTITLE_KEYS_WITH_LANGUAGE) {
                    return null
                }
                val subtitleRelativePath = subtitleBundle.getString(HOST_RELATIVE_PATH)
                    ?.takeIf(::isSafeHostName)
                    ?: return null
                val subtitleDisplayName = subtitleBundle.getString(HOST_DISPLAY_NAME)
                    ?.takeIf { it == subtitleRelativePath }
                    ?: return null
                val subtitleMimeType = subtitleBundle.getString(HOST_MIME_TYPE)
                    ?.takeIf { it == "application/x-subrip" || it == "text/x-ssa" }
                    ?: return null
                val languageTag = subtitleBundle.getString(HOST_LANGUAGE_TAG)
                if (languageTag != null && !LANGUAGE_TAG.matches(languageTag)) return null
                AndroidPlaybackSubtitle(
                    sourceUri = hostSourceUri(HOST_SUBTITLE_AUTHORITY, index, subtitleIndex),
                    relativePath = subtitleRelativePath,
                    displayName = subtitleDisplayName,
                    mimeType = subtitleMimeType,
                    languageTag = languageTag,
                )
            }
            if (subtitles.map { it.relativePath }.toSet().size != subtitles.size) return null
            if (subtitles.zipWithNext().any { (first, second) ->
                    HostMediaDiscoveryPolicy.compareNaturally(first.displayName, second.displayName) > 0
                }
            ) {
                return null
            }
            AndroidPlaybackItem(
                sourceUri = if (index == startIndex) {
                    selectedContentUri
                } else {
                    hostSourceUri(HOST_VIDEO_AUTHORITY, index)
                },
                externalUri = selectedContentUri.takeIf { index == startIndex },
                relativePath = relativePath,
                displayName = displayName,
                declaredSize = size,
                mimeType = mimeType,
                subtitles = subtitles,
            )
        }
        if (items[startIndex].relativePath != "" ||
            items[startIndex].displayName != selected.displayName ||
            items[startIndex].mimeType != selected.mimeType ||
            items[startIndex].declaredSize != selected.declaredSize
        ) {
            return null
        }
        if (items.map { it.displayName }.toSet().size != items.size) return null
        if (items.zipWithNext().any { (first, second) ->
                HostMediaDiscoveryPolicy.compareNaturally(first.displayName, second.displayName) > 0
            }
        ) {
            return null
        }
        return AndroidPlaybackRequest(items, startIndex, session, targetId)
    }

    private fun DiscoveredVideo.toHostBundle(): Bundle = Bundle().apply {
        putString(HOST_RELATIVE_PATH, relativePath)
        putString(HOST_DISPLAY_NAME, displayName)
        putString(HOST_MIME_TYPE, mimeType)
        putLong(HOST_SIZE, size)
        putParcelableArrayList(
            HOST_SUBTITLES,
            ArrayList(
                subtitles.map { subtitle ->
                    Bundle().apply {
                        putString(HOST_RELATIVE_PATH, subtitle.relativePath)
                        putString(HOST_DISPLAY_NAME, subtitle.displayName)
                        putString(HOST_MIME_TYPE, subtitle.mimeType)
                        subtitle.languageTag?.let { putString(HOST_LANGUAGE_TAG, it) }
                    }
                },
            ),
        )
    }

    private fun hostSourceUri(authority: String, vararg indices: Int): Uri = Uri.Builder()
        .scheme(HOST_SOURCE_SCHEME)
        .authority(authority)
        .apply { indices.forEach { index -> appendPath(index.toString()) } }
        .build()

    private fun isSafeHostName(value: String): Boolean =
        value.length in 1..VideoRequestPolicy.MAX_DISPLAY_NAME_LENGTH && value.isNotBlank() &&
            value != "." && value != ".." && '/' !in value && '\\' !in value &&
            value.none { it.isISOControl() || Character.getType(it) == Character.FORMAT.toInt() }

    private val HOST_REQUEST_KEYS = setOf(
        ExplorerActionHostSessionKeys.BINDER,
        HOST_TARGET_ID,
        HOST_START_INDEX,
        HOST_ITEMS,
    )
    private val HOST_ITEM_KEYS = setOf(
        HOST_RELATIVE_PATH,
        HOST_DISPLAY_NAME,
        HOST_MIME_TYPE,
        HOST_SIZE,
        HOST_SUBTITLES,
    )
    private val HOST_SUBTITLE_KEYS = setOf(
        HOST_RELATIVE_PATH,
        HOST_DISPLAY_NAME,
        HOST_MIME_TYPE,
    )
    private val HOST_SUBTITLE_KEYS_WITH_LANGUAGE = HOST_SUBTITLE_KEYS + HOST_LANGUAGE_TAG
    private val LANGUAGE_TAG = Regex("[A-Za-z]{2,3}(?:-[A-Za-z0-9]{2,8})*")

    @Suppress("DEPRECATION")
    private fun Bundle.bundleArrayList(key: String): ArrayList<Bundle>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayList(key, Bundle::class.java)
        } else {
            getParcelableArrayList(key)
        }
}

internal object ContentPlaybackMetadataResolver {

    fun resolve(context: Context, uri: Uri): ContentPlaybackMetadata {
        val providerMetadata = runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                null,
                null,
                null,
            )?.use(::readMetadata)
        }.getOrNull()
        return ContentPlaybackMetadata(
            displayName = VideoRequestPolicy.sanitizeExternalDisplayName(
                providerMetadata?.first,
                uri.lastPathSegment,
            ),
            declaredSize = providerMetadata?.second
                ?.takeIf { it >= 0L }
                ?: VideoRequestPolicy.UNKNOWN_DECLARED_SIZE,
        )
    }

    private fun readMetadata(cursor: Cursor): Pair<String?, Long?>? {
        if (!cursor.moveToFirst()) return null
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        val name = nameIndex.takeIf { it >= 0 && !cursor.isNull(it) }?.let(cursor::getString)
        val size = sizeIndex.takeIf { it >= 0 && !cursor.isNull(it) }?.let(cursor::getLong)
        return name to size
    }
}
