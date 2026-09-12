package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.LoadedPlaylist
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistLocationPolicy
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistParser
import org.autojs.plugin.explorer.api.IExplorerActionHostSession
import java.util.Locale

/** Separate private payload for ordered lists, whose first media item need not be the input file. */
internal object PlaylistPlayback {
    const val EXTRA = "io.github.supermonster003.autojs6.plugin.threeemberplayer.extra.PLAYLIST"

    fun mediaMime(name: String, declared: String?): String? {
        if (PlaylistParser.format(name, declared) != null) return null
        return VideoRequestPolicy.normalizeVideoMimeType(declared)
            ?: "video/*".takeIf { name.substringAfterLast('.', "").lowercase(Locale.ROOT) in ThreeEmberPlayerPlugin.EXTENSIONS }
    }

    fun documentIntent(context: Context, loaded: LoadedPlaylist): Intent = create(
        context, requireNotNull(loaded.items.first().uri), loaded.items.first().mimeType, loaded, null, null,
    )

    fun hostIntent(context: Context, request: AndroidExplorerRequest, loaded: LoadedPlaylist): Intent = create(
        context, request.primaryTarget.targetUri, request.primaryTarget.mimeType, loaded,
        requireNotNull(request.hostSession), request.primaryTarget.targetId,
    )

    private fun create(
        context: Context, source: Uri, sourceMime: String, loaded: LoadedPlaylist,
        session: IExplorerActionHostSession?, targetId: String?,
    ): Intent = Intent(context, VideoPlayerActivity::class.java).apply {
        require(loaded.items.size in 1..PlaylistParser.MAX_QUEUE_SIZE)
        action = VideoRequestPolicy.INTERNAL_PLAY_ACTION
        setDataAndType(source, sourceMime)
        clipData = ClipData.newRawUri("playlist", source).apply {
            if (session == null) loaded.items.drop(1).forEach { addItem(ClipData.Item(requireNotNull(it.uri))) }
        }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        putExtra(EXTRA, Bundle().apply {
            session?.let { putBinder("session", it.asBinder()); putString("target", requireNotNull(targetId)) }
            putParcelableArrayList("items", ArrayList(loaded.items.map { item ->
                Bundle().apply {
                    putString("location", if (session == null) requireNotNull(item.uri).toString() else requireNotNull(item.relativePath))
                    putString("title", item.displayName)
                    putString("mime", item.mimeType)
                    putLong("size", item.size)
                }
            }))
        })
    }

    fun resolve(intent: Intent): AndroidPlaybackRequest? = runCatching {
        if (intent.action != VideoRequestPolicy.INTERNAL_PLAY_ACTION || intent.selector != null ||
            intent.flags != Intent.FLAG_GRANT_READ_URI_PERMISSION || intent.extras?.keySet() != setOf(EXTRA)
        ) return null
        val data = intent.data?.takeIf { PlaylistLocationPolicy.contentUri(it.toString()) != null } ?: return null
        @Suppress("DEPRECATION")
        val bundle = intent.getBundleExtra(EXTRA) ?: return null
        val binder = bundle.getBinder("session")
        val session = binder?.let {
            if (it.interfaceDescriptor != IExplorerActionHostSession.DESCRIPTOR) return null
            IExplorerActionHostSession.Stub.asInterface(it)
        }
        val targetId = bundle.getString("target")
        if (session == null) {
            if (bundle.keySet() != setOf("items")) return null
        } else if (bundle.keySet() != setOf("items", "session", "target") || targetId == null ||
            targetId.length !in 1..128 || targetId.any { it.isWhitespace() || it.isISOControl() }
        ) return null
        @Suppress("DEPRECATION")
        val bundles = if (Build.VERSION.SDK_INT >= 33) bundle.getParcelableArrayList("items", Bundle::class.java)
            else bundle.getParcelableArrayList<Bundle>("items")
        if (bundles == null || bundles.size !in 1..PlaylistParser.MAX_QUEUE_SIZE) return null
        val items = bundles.mapIndexed { index, item ->
            if (item.keySet() != setOf("location", "title", "mime", "size")) return null
            val location = item.getString("location") ?: return null
            val title = item.getString("title")?.takeIf { it == VideoRequestPolicy.sanitizeExternalDisplayName(it, null) } ?: return null
            val mime = VideoRequestPolicy.normalizeVideoMimeType(item.getString("mime")) ?: return null
            val size = item.getLong("size", Long.MIN_VALUE).takeIf { it >= -1L } ?: return null
            val uri = if (session == null) {
                PlaylistLocationPolicy.contentUri(location)?.let(Uri::parse) ?: return null
            } else {
                if (!PlaylistLocationPolicy.isSafeName(location)) return null
                Uri.Builder().scheme(VideoIntentFactory.HOST_SOURCE_SCHEME).authority("video").appendPath(index.toString()).build()
            }
            AndroidPlaybackItem(uri, uri.takeIf { session == null }, targetId, location.takeIf { session != null }, title, size, mime)
        }
        val clip = intent.clipData ?: return null
        val expectedUris = if (session == null) items.map { it.sourceUri } else listOf(data)
        if (clip.itemCount != expectedUris.size) return null
        expectedUris.forEachIndexed { index, uri ->
            val item = clip.getItemAt(index)
            if (item.uri != uri || item.text != null || item.htmlText != null || item.intent != null) return null
        }
        if (session == null && (data != items.first().sourceUri || intent.type != items.first().mimeType)) return null
        AndroidPlaybackRequest(items, 0, session)
    }.getOrNull()
}
