package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.LoadedPlaylist
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistMedia
import org.autojs.plugin.explorer.api.IExplorerActionHostSession
import org.junit.Assert.*
import org.junit.Test

class PlaylistPlaybackInstrumentationTest {
    @Test fun documentQueuePreservesTitlesAndRepeatedUris() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val item = PlaylistMedia(Uri.parse("content://test/videos/movie.mp4"), null, "Movie", "video/mp4")
        val loaded = LoadedPlaylist(listOf(item, item.copy(displayName = "Again")), 0)
        val intent = PlaylistPlayback.documentIntent(context, loaded)
        val resolved = requireNotNull(AndroidVideoIntentPolicy.resolveInternal(intent))
        assertEquals(listOf("Movie", "Again"), resolved.items.map { it.displayName })
        assertEquals(listOf(item.uri, item.uri), resolved.items.map { it.sourceUri })
        assertNull(resolved.hostSession)
        assertEquals(0, resolved.startIndex)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        assertNull(AndroidVideoIntentPolicy.resolveInternal(intent))
    }

    @Test fun hostPlaylistKeepsTheInputDocumentOutOfTheQueue() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val session = TestHostSession()
        val request = AndroidExplorerRequest(
            ThreeAmberPlayerPlugin.ACTION_ID, Uri.parse("content://test/videos"),
            listOf(AndroidExplorerTarget("playlist", Uri.parse("content://test/videos/list.m3u"), "list.m3u", 40, "audio/x-mpegurl")), session,
        )
        val loaded = LoadedPlaylist(List(2) { PlaylistMedia(null, "movie.mp4", "Same title", "video/mp4") }, 0)
        val intent = PlaylistPlayback.hostIntent(context, request, loaded)
        val resolved = requireNotNull(AndroidVideoIntentPolicy.resolveInternal(intent))
        assertEquals(session.asBinder(), resolved.hostSession?.asBinder())
        assertEquals(listOf("movie.mp4", "movie.mp4"), resolved.items.map { it.relativePath })
        assertTrue(resolved.items.all { it.externalUri == null })
        assertTrue(resolved.items.none { it.sourceUri == request.primaryTarget.targetUri })
        val routes = resolved.hostFileRoutesByUri()
        assertEquals(2, routes.size)
        assertTrue(routes.values.all { it.targetId == "playlist" && it.relativePath == "movie.mp4" })
    }

    @Test fun rejectsPlaylistRouteTraversal() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val request = AndroidExplorerRequest(
            ThreeAmberPlayerPlugin.ACTION_ID, Uri.parse("content://test/videos"),
            listOf(AndroidExplorerTarget("playlist", Uri.parse("content://test/videos/list.m3u"), "list.m3u", 40, "audio/x-mpegurl")), TestHostSession(),
        )
        val loaded = LoadedPlaylist(listOf(PlaylistMedia(null, "../secret.mp4", "Movie", "video/mp4")), 0)
        assertNull(AndroidVideoIntentPolicy.resolveInternal(PlaylistPlayback.hostIntent(context, request, loaded)))
    }
    private class TestHostSession : IExplorerActionHostSession.Stub() {
        override fun listChildren(targetId: String, relativePath: String, offset: Int, limit: Int) = Bundle.EMPTY
        override fun openFile(targetId: String, relativePath: String): ParcelFileDescriptor = unsupported()
        override fun prepareOutput(displayName: String, mimeType: String, conflictPolicy: Int) = Bundle.EMPTY
        override fun openOutput(transactionId: String): ParcelFileDescriptor = unsupported()
        override fun commitOutput(transactionId: String) = Bundle.EMPTY
        override fun abortOutput(transactionId: String) = Unit
        override fun close() = Unit
        override fun openPendingOutput(transactionId: String): ParcelFileDescriptor = unsupported()
        override fun prepareTargetReplacement(targetId: String) = Bundle.EMPTY
        override fun prepareOutputTree(displayName: String, conflictPolicy: Int) = Bundle.EMPTY
        override fun createOutputDirectory(transactionId: String, relativePath: String) = Unit
        override fun openOutputFile(transactionId: String, relativePath: String): ParcelFileDescriptor = unsupported()
        override fun queryOutput(transactionId: String) = Bundle.EMPTY
        override fun listOutputs() = Bundle.EMPTY
        override fun attachClient(clientToken: IBinder) = Unit
        override fun getPlaybackProgress(targetId: String, relativePath: String) = Bundle.EMPTY
        override fun reportPlaybackProgress(
            targetId: String,
            relativePath: String,
            positionMillis: Long,
            durationMillis: Long,
            reportState: Int,
        ) = Unit

        private fun unsupported(): Nothing = throw UnsupportedOperationException()
    }
}
