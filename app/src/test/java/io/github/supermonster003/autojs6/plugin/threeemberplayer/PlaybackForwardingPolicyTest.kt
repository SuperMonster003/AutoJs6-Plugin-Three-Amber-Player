package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionValues

class PlaybackForwardingPolicyTest {

    @Test
    fun explorerForwarding_keepsOnlyTargetReadGrantAndSafeMetadata() {
        val spec = PlaybackForwardingPolicy.fromExplorer(
            ValidatedExplorerRequest(
                targetId = "target-id",
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
        assertEquals(99L, spec.declaredSize)
        assertEquals(
            mapOf(
                VideoRequestPolicy.DISPLAY_NAME_EXTRA to "sample.mp4",
                VideoRequestPolicy.DECLARED_SIZE_EXTRA to 99L,
            ),
            spec.extras,
        )
    }

    @Test
    fun externalForwarding_sanitizesNameAndDoesNotCarryInboundState() {
        val spec = PlaybackForwardingPolicy.fromExternal(
            ValidatedExternalRequest(TARGET_URI, "video/x-matroska"),
            "unsafe/\u202Ename.mkv",
            123L,
        )

        assertEquals("unsafename.mkv", spec.displayName)
        assertEquals("video/x-matroska", spec.mimeType)
        assertEquals(123L, spec.declaredSize)
        assertEquals(
            setOf(
                VideoRequestPolicy.DISPLAY_NAME_EXTRA,
                VideoRequestPolicy.DECLARED_SIZE_EXTRA,
            ),
            spec.extras.keys,
        )
        assertEquals(listOf(TARGET_URI), spec.clipUris)
        assertTrue(spec.grantRead)
        assertFalse(spec.grantWrite || spec.grantPersistable || spec.grantPrefix)

        val unknownSize = PlaybackForwardingPolicy.fromExternal(
            ValidatedExternalRequest(TARGET_URI, "video/x-matroska"),
            "sample.mkv",
            -99L,
        )
        assertEquals(VideoRequestPolicy.UNKNOWN_DECLARED_SIZE, unknownSize.declaredSize)
    }

    @Test
    fun catalog_acceptsHostVideoMimeAndKeepsLegacyExtensionsWithoutDuplicates() {
        val expected = listOf(
            "mp4", "mpeg4", "mpg4", "avi", "mkv", "mov", "flv", "webm", "m4v", "3gp",
            "mpeg", "3g2", "3gp2", "3gpp", "f4v", "m2t", "m2ts", "mts", "ts", "mpg",
            "mpe", "vob", "qt",
        )

        assertEquals(expected, ThreeEmberPlayerPlugin.EXTENSIONS.asList())
        assertEquals(expected.size, ThreeEmberPlayerPlugin.EXTENSIONS.toSet().size)
        assertEquals(listOf("video/*"), ThreeEmberPlayerPlugin.MIME_TYPES.asList())
        assertEquals(12, ExplorerActionProtocol.VERSION)
        assertEquals(2, ExplorerActionValues.PLACEMENT_PRIMARY)
        assertEquals(12, ThreeEmberPlayerPlugin.PROTOCOL_VERSION)
        assertEquals(5276L, ThreeEmberPlayerPlugin.REQUIRED_HOST_VERSION)
    }

    private companion object {
        const val TARGET_URI = "content://authority/root/videos/sample.mp4"
    }
}
