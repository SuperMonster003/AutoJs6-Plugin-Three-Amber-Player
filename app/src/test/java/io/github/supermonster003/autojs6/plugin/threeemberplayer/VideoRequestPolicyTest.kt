package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class VideoRequestPolicyTest {

    @Test
    fun explorerRequest_acceptsExactProtocolV12Envelope() {
        val result = VideoRequestPolicy.validateExplorer(validExplorerEnvelope())

        assertNotNull(result)
        assertEquals(TARGET_URI, result?.targetUri)
        assertEquals(PARENT_URI, result?.parentUri)
        assertEquals("sample.mp4", result?.displayName)
        assertEquals(42L, result?.declaredSize)
        assertEquals("video/mp4", result?.mimeType)
    }

    @Test
    fun explorerRequest_acceptsAnySafeNameWhenTrustedHostDeclaresVideo() {
        val target = "content://authority/root/videos/camera.wmv"
        val result = VideoRequestPolicy.validateExplorer(
            validExplorerEnvelope().copy(
                targetUri = target,
                displayName = "camera.wmv",
                clipItems = listOf(ClipItemEnvelope(target)),
                mimeType = "video/*",
            ),
        )

        assertNotNull(result)
        assertEquals("camera.wmv", result?.displayName)
        assertEquals("video/*", result?.mimeType)
    }

    @Test
    fun explorerRequest_rejectsWrongIdentitySourceOrHostBuild() {
        listOf(
            validExplorerEnvelope().copy(action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION),
            validExplorerEnvelope().copy(actionId = "other-action"),
            validExplorerEnvelope().copy(protocolVersion = 11),
            validExplorerEnvelope().copy(sourceSurface = "dialog"),
            validExplorerEnvelope().copy(hostVersionCode = ThreeEmberPlayerPlugin.REQUIRED_HOST_VERSION - 1),
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
    fun explorerRequest_requiresOneExactTargetClipItem() {
        listOf(
            emptyList(),
            listOf(ClipItemEnvelope(TARGET_URI), ClipItemEnvelope(PARENT_URI)),
            listOf(ClipItemEnvelope(PARENT_URI)),
            listOf(ClipItemEnvelope(TARGET_URI, hasText = true)),
            listOf(ClipItemEnvelope(TARGET_URI, hasIntent = true)),
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
            validExplorerEnvelope().copy(lastModified = -2L),
            validExplorerEnvelope().copy(targetKind = 2),
            validExplorerEnvelope().copy(targetCount = 2),
            validExplorerEnvelope().copy(targetId = "bad id"),
            validExplorerEnvelope().copy(mimeType = "application/octet-stream"),
            validExplorerEnvelope().copy(mimeType = "Video/mp4"),
            validExplorerEnvelope().copy(mimeType = "video/mp4; charset=utf-8"),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExplorer(envelope))
        }

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
            declaredSize = 42L,
            mimeType = "video/mp4",
            extraKeys = setOf(
                VideoRequestPolicy.DISPLAY_NAME_EXTRA,
                VideoRequestPolicy.DECLARED_SIZE_EXTRA,
            ),
        )
        val validated = VideoRequestPolicy.validateInternal(valid)
        assertNotNull(validated)
        assertEquals(42L, validated?.declaredSize)

        listOf(
            valid.copy(action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION),
            valid.copy(clipItems = emptyList()),
            valid.copy(clipItems = listOf(ClipItemEnvelope(TARGET_URI, hasHtmlText = true))),
            valid.copy(grants = EXPLORER_GRANTS),
            valid.copy(extraKeys = emptySet()),
            valid.copy(extraKeys = valid.extraKeys + "untrusted"),
            valid.copy(displayName = "bad/name.mp4"),
            valid.copy(declaredSize = -2L),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateInternal(envelope))
        }

        assertEquals(
            VideoRequestPolicy.UNKNOWN_DECLARED_SIZE,
            VideoRequestPolicy.validateInternal(
                valid.copy(declaredSize = VideoRequestPolicy.UNKNOWN_DECLARED_SIZE),
            )?.declaredSize,
        )
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
        actionId = ThreeEmberPlayerPlugin.ACTION_ID,
        protocolVersion = VideoRequestPolicy.PROTOCOL_VERSION,
        sourceSurface = VideoRequestPolicy.SOURCE_SURFACE_MAIN,
        hostVersionCode = ThreeEmberPlayerPlugin.REQUIRED_HOST_VERSION,
        requestId = "123e4567-e89b-12d3-a456-426614174000",
        targetUri = TARGET_URI,
        parentUri = PARENT_URI,
        parentDisplayPath = "/storage/emulated/0/videos",
        clipItems = listOf(ClipItemEnvelope(TARGET_URI)),
        grants = EXPLORER_GRANTS,
        targetCount = 1,
        targetId = "target-id",
        targetKind = 1,
        displayName = "sample.mp4",
        declaredSize = 42L,
        lastModified = 1L,
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
