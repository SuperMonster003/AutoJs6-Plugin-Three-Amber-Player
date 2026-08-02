package io.github.supermonster003.autojs6.plugin.videoplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionValues

class PlaybackForwardingPolicyTest {

    @Test
    fun explorerForwarding_keepsOnlyTargetReadGrantAndDisplayName() {
        val spec = PlaybackForwardingPolicy.fromExplorer(
            ValidatedExplorerRequest(
                targetUri = TARGET_URI,
                parentUri = "content://authority/root/videos",
                displayName = "sample.mp4",
                declaredSize = 99L,
                mimeType = "video/mp4",
            ),
        )

        assertEquals(VideoRequestPolicy.INTERNAL_PLAY_ACTION, spec.action)
        assertEquals(TARGET_URI, spec.targetUri)
        assertEquals(listOf(TARGET_URI), spec.clipUris)
        assertTrue(spec.grantRead)
        assertFalse(spec.grantWrite)
        assertFalse(spec.grantPersistable)
        assertFalse(spec.grantPrefix)
        assertEquals(
            mapOf(VideoRequestPolicy.DISPLAY_NAME_EXTRA to "sample.mp4"),
            spec.extras,
        )
    }

    @Test
    fun externalForwarding_sanitizesNameAndDoesNotCarryInboundState() {
        val spec = PlaybackForwardingPolicy.fromExternal(
            ValidatedExternalRequest(TARGET_URI, "video/x-matroska"),
            "unsafe/\u202Ename.mkv",
        )

        assertEquals("unsafename.mkv", spec.displayName)
        assertEquals("video/x-matroska", spec.mimeType)
        assertEquals(setOf(VideoRequestPolicy.DISPLAY_NAME_EXTRA), spec.extras.keys)
        assertEquals(listOf(TARGET_URI), spec.clipUris)
        assertTrue(spec.grantRead)
        assertFalse(spec.grantWrite || spec.grantPersistable || spec.grantPrefix)
    }

    @Test
    fun catalog_coversCurrentHostVideoExtensionsWithoutDuplicates() {
        val expected = listOf(
            "mp4", "mpeg4", "mpg4", "avi", "mkv", "mov", "flv", "webm", "m4v", "3gp",
            "mpeg", "3g2", "3gp2", "3gpp", "f4v", "m2t", "m2ts", "mts", "ts", "mpg",
            "mpe", "vob", "qt",
        )

        assertEquals(expected, VideoPlayerPlugin.EXTENSIONS.asList())
        assertEquals(expected.size, VideoPlayerPlugin.EXTENSIONS.toSet().size)
        assertTrue(VideoPlayerPlugin.MIME_TYPES.isEmpty())
        assertEquals(2, ExplorerActionProtocol.VERSION)
        assertEquals(2, ExplorerActionValues.PLACEMENT_PRIMARY)
        assertEquals(5269L, VideoPlayerPlugin.REQUIRED_HOST_VERSION)
    }

    private companion object {
        const val TARGET_URI = "content://authority/root/videos/sample.mp4"
    }
}
