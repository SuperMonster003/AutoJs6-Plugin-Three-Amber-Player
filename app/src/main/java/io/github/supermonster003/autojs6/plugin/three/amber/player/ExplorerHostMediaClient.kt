package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.os.Build
import android.os.Bundle
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal object ExplorerHostMediaClient {

    fun discover(request: AndroidExplorerRequest): DiscoveredVideoQueue {
        require(!request.isSelection && request.targets.size == 1)
        val session = requireNotNull(request.hostSession)
        val selected = request.primaryTarget
        return HostMediaDiscoveryPolicy.discover(
            selectedDisplayName = selected.displayName,
            selectedMimeType = selected.mimeType,
            selectedSize = selected.declaredSize,
            siblings = listDirectSiblings(session, selected.targetId),
        )
    }

    private fun listDirectSiblings(
        session: IExplorerActionHostSession,
        targetId: String,
    ): List<ExplorerSiblingItem> {
        val result = ArrayList<ExplorerSiblingItem>()
        var offset = 0
        while (true) {
            val page = session.listChildren(
                targetId,
                "",
                offset,
                ExplorerActionProtocol.MAX_SESSION_PAGE_SIZE,
            )
            val items = page.bundleArrayList(ExplorerActionHostSessionKeys.ITEMS)
                ?: error("Explorer sibling page has no items")
            require(result.size + items.size <= MAX_DISCOVERY_ITEMS) {
                "Explorer sibling discovery exceeds its bounded item limit"
            }
            items.forEach { item -> result += parseItem(item) }
            val nextOffset = page.getInt(ExplorerActionHostSessionKeys.NEXT_OFFSET, -1)
            val complete = page.getBoolean(ExplorerActionHostSessionKeys.COMPLETE, false)
            require(nextOffset == offset + items.size && nextOffset >= offset) {
                "Explorer sibling page offset is inconsistent"
            }
            if (complete) return result
            require(items.isNotEmpty() && nextOffset > offset) {
                "Explorer sibling pagination made no progress"
            }
            offset = nextOffset
        }
    }

    private fun parseItem(bundle: Bundle): ExplorerSiblingItem {
        val relativePath = requireNotNull(
            bundle.getString(ExplorerActionHostSessionKeys.RELATIVE_PATH),
        )
        val displayName = requireNotNull(
            bundle.getString(ExplorerActionHostSessionKeys.DISPLAY_NAME),
        )
        require(relativePath == displayName && '/' !in relativePath && '\\' !in relativePath)
        return ExplorerSiblingItem(
            relativePath = relativePath,
            displayName = displayName,
            kind = bundle.getInt(ExplorerActionHostSessionKeys.KIND, Int.MIN_VALUE),
            mimeType = requireNotNull(bundle.getString(ExplorerActionHostSessionKeys.MIME_TYPE)),
            size = bundle.getLong(ExplorerActionHostSessionKeys.SIZE, Long.MIN_VALUE),
            lastModified = bundle.getLong(ExplorerActionHostSessionKeys.LAST_MODIFIED, Long.MIN_VALUE),
            readable = bundle.getBoolean(ExplorerActionHostSessionKeys.READABLE, false),
            symbolicLink = bundle.getBoolean(ExplorerActionHostSessionKeys.SYMBOLIC_LINK, true),
        )
    }

    @Suppress("DEPRECATION")
    private fun Bundle.bundleArrayList(key: String): ArrayList<Bundle>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayList(key, Bundle::class.java)
        } else {
            getParcelableArrayList(key)
        }

    private const val MAX_DISCOVERY_ITEMS = 4_096
}
