package io.github.supermonster003.autojs6.plugin.videoplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class VideoRequestPolicyTest {

    @Test
    fun explorerRequest_acceptsExactProtocolV2Envelope() {
        val result = VideoRequestPolicy.validateExplorer(validExplorerEnvelope())

        assertNotNull(result)
        assertEquals(TARGET_URI, result?.targetUri)
        assertEquals(PARENT_URI, result?.parentUri)
        assertEquals("sample.mp4", result?.displayName)
        assertEquals(42L, result?.declaredSize)
        assertEquals("video/mp4", result?.mimeType)
    }

    @Test
    fun explorerRequest_rejectsWrongIdentitySourceOrHostBuild() {
        listOf(
            validExplorerEnvelope().copy(action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION),
            validExplorerEnvelope().copy(actionId = "other-action"),
            validExplorerEnvelope().copy(protocolVersion = 1),
            validExplorerEnvelope().copy(sourceSurface = "dialog"),
            validExplorerEnvelope().copy(hostVersionCode = VideoPlayerPlugin.REQUIRED_HOST_VERSION - 1),
            validExplorerEnvelope().copy(hostVersionCode = null),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExplorer(envelope))
        }
    }

    @Test
    fun explorerRequest_requiresReadAndPrefixButNoWriteOrPersistableGrant() {
        listOf(
            GrantEnvelope(read = false, write = false, persistable = false, prefix = true),
            GrantEnvelope(read = true, write = true, persistable = false, prefix = true),
            GrantEnvelope(read = true, write = false, persistable = true, prefix = true),
            GrantEnvelope(read = true, write = false, persistable = false, prefix = false),
        ).forEach { grants ->
            assertNull(VideoRequestPolicy.validateExplorer(validExplorerEnvelope().copy(grants = grants)))
        }
    }

    @Test
    fun explorerRequest_rejectsUnsafeOrUnrelatedUris() {
        listOf(
            validExplorerEnvelope().copy(targetUri = "file:///root/videos/sample.mp4"),
            validExplorerEnvelope().copy(targetUri = "$TARGET_URI?mode=read"),
            validExplorerEnvelope().copy(targetUri = "$TARGET_URI#fragment"),
            validExplorerEnvelope().copy(targetUri = "content://authority/root/videos/%2E%2E/sample.mp4"),
            validExplorerEnvelope().copy(targetUri = "content://authority/root/videos/sample%2Fmp4"),
            validExplorerEnvelope().copy(parentUri = "content://authority/root/other"),
            validExplorerEnvelope().copy(parentUri = TARGET_URI),
            validExplorerEnvelope().copy(parentUri = "content://other/root/videos"),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExplorer(envelope))
        }
    }

    @Test
    fun explorerRequest_requiresExactTargetAndParentClipItems() {
        listOf(
            emptyList(),
            listOf(ClipItemEnvelope(TARGET_URI)),
            listOf(ClipItemEnvelope(TARGET_URI), ClipItemEnvelope(PARENT_URI), ClipItemEnvelope(TARGET_URI)),
            listOf(ClipItemEnvelope(PARENT_URI), ClipItemEnvelope(TARGET_URI)),
            listOf(ClipItemEnvelope(TARGET_URI, hasText = true), ClipItemEnvelope(PARENT_URI)),
            listOf(ClipItemEnvelope(TARGET_URI), ClipItemEnvelope(PARENT_URI, hasIntent = true)),
        ).forEach { clipItems ->
            assertNull(
                VideoRequestPolicy.validateExplorer(
                    validExplorerEnvelope().copy(clipItems = clipItems),
                ),
            )
        }
    }

    @Test
    fun explorerRequest_rejectsUnsafeMetadataAndNonVideoTargets() {
        listOf(
            validExplorerEnvelope().copy(displayName = "other.mp4"),
            validExplorerEnvelope().copy(displayName = "sample\u202Emp4"),
            validExplorerEnvelope().copy(displayName = "sample.exe"),
            validExplorerEnvelope().copy(declaredSize = -1L),
            validExplorerEnvelope().copy(mimeType = "application/octet-stream"),
            validExplorerEnvelope().copy(mimeType = "Video/mp4"),
            validExplorerEnvelope().copy(mimeType = "video/mp4; charset=utf-8"),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExplorer(envelope))
        }

        val unsupportedTarget = "content://authority/root/videos/sample.bin"
        val unsupported = validExplorerEnvelope().copy(
            targetUri = unsupportedTarget,
            displayName = "sample.bin",
            clipItems = listOf(ClipItemEnvelope(unsupportedTarget), ClipItemEnvelope(PARENT_URI)),
        )
        assertNull(VideoRequestPolicy.validateExplorer(unsupported))
    }

    @Test
    fun externalRequest_acceptsOnlyExactReadGrantAndContentVideo() {
        val valid = ExternalRequestEnvelope(
            action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION,
            targetUri = TARGET_URI,
            grants = EXTERNAL_GRANTS,
            mimeType = "video/mp4",
        )
        assertNotNull(VideoRequestPolicy.validateExternal(valid))

        listOf(
            valid.copy(action = VideoRequestPolicy.EXPLORER_EXECUTE_ACTION),
            valid.copy(targetUri = "https://example.com/video.mp4"),
            valid.copy(mimeType = "audio/mp4"),
            valid.copy(grants = EXPLORER_GRANTS),
            valid.copy(grants = EXTERNAL_GRANTS.copy(write = true)),
            valid.copy(grants = EXTERNAL_GRANTS.copy(persistable = true)),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExternal(envelope))
        }
    }

    @Test
    fun internalRequest_requiresOneExactClipAndOnlyDisplayNameExtra() {
        val valid = InternalRequestEnvelope(
            action = VideoRequestPolicy.INTERNAL_PLAY_ACTION,
            targetUri = TARGET_URI,
            clipItems = listOf(ClipItemEnvelope(TARGET_URI)),
            grants = EXTERNAL_GRANTS,
            displayName = "sample.mp4",
            mimeType = "video/mp4",
            extraKeys = setOf(VideoRequestPolicy.DISPLAY_NAME_EXTRA),
        )
        assertNotNull(VideoRequestPolicy.validateInternal(valid))

        listOf(
            valid.copy(action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION),
            valid.copy(clipItems = emptyList()),
            valid.copy(clipItems = listOf(ClipItemEnvelope(TARGET_URI, hasHtmlText = true))),
            valid.copy(grants = EXPLORER_GRANTS),
            valid.copy(extraKeys = emptySet()),
            valid.copy(extraKeys = valid.extraKeys + "untrusted"),
            valid.copy(displayName = "bad/name.mp4"),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateInternal(envelope))
        }
    }

    @Test
    fun displayNameSanitizer_removesPathAndUnicodeControls() {
        assertEquals(
            "unsafevideo.mp4",
            VideoRequestPolicy.sanitizeExternalDisplayName("unsafe/\\\u202Evideo.mp4", null),
        )
        assertEquals("fallback.mp4", VideoRequestPolicy.sanitizeExternalDisplayName("..", "fallback.mp4"))
        assertEquals("video", VideoRequestPolicy.sanitizeExternalDisplayName(null, null))
    }

    private fun validExplorerEnvelope() = ExplorerRequestEnvelope(
        action = VideoRequestPolicy.EXPLORER_EXECUTE_ACTION,
        actionId = VideoPlayerPlugin.ACTION_ID,
        protocolVersion = VideoRequestPolicy.PROTOCOL_VERSION,
        sourceSurface = VideoRequestPolicy.SOURCE_SURFACE_MAIN,
        hostVersionCode = VideoPlayerPlugin.REQUIRED_HOST_VERSION,
        targetUri = TARGET_URI,
        parentUri = PARENT_URI,
        clipItems = listOf(ClipItemEnvelope(TARGET_URI), ClipItemEnvelope(PARENT_URI)),
        grants = EXPLORER_GRANTS,
        displayName = "sample.mp4",
        declaredSize = 42L,
        mimeType = "video/mp4",
    )

    private companion object {
        const val PARENT_URI = "content://authority/root/videos"
        const val TARGET_URI = "$PARENT_URI/sample.mp4"
        val EXPLORER_GRANTS = GrantEnvelope(
            read = true,
            write = false,
            persistable = false,
            prefix = true,
        )
        val EXTERNAL_GRANTS = GrantEnvelope(
            read = true,
            write = false,
            persistable = false,
            prefix = false,
        )
    }
}
