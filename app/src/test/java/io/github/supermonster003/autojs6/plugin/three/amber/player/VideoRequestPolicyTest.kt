package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoRequestPolicyTest {

    @Test
    fun explorerSingle_acceptsExactProtocolV12Envelope() {
        val result = VideoRequestPolicy.validateExplorer(validExplorerEnvelope())

        assertNotNull(result)
        assertFalse(requireNotNull(result).isSelection)
        assertEquals(TARGET_URI, result.primaryTarget.targetUri)
        assertEquals(PARENT_URI, result.parentUri)
        assertEquals("sample.mp4", result.primaryTarget.displayName)
        assertEquals(42L, result.primaryTarget.declaredSize)
        assertEquals("video/mp4", result.primaryTarget.mimeType)
    }

    @Test
    fun explorerSelection_acceptsOneThrough128AndPreservesHostOrder() {
        listOf(1, 2, ExplorerSelectionPolicy.MAX_TARGETS).forEach { count ->
            val envelope = validSelectionEnvelope(count).let { candidate ->
                if (count == 1) candidate.copy(hostSessionPresent = false) else candidate
            }
            val result = VideoRequestPolicy.validateExplorer(envelope)

            assertNotNull(result)
            assertTrue(requireNotNull(result).isSelection)
            assertEquals(
                envelope.targets.map { it.displayName },
                result.targets.map { it.displayName },
            )
        }
    }

    @Test
    fun explorerSelection_rejectsInvalidCountMissingSessionAndSingleActionGroup() {
        listOf(
            validSelectionEnvelope(1).copy(targets = emptyList(), clipItems = emptyList()),
            validSelectionEnvelope(ExplorerSelectionPolicy.MAX_TARGETS).let { envelope ->
                val extra = targetEnvelope(ExplorerSelectionPolicy.MAX_TARGETS)
                envelope.copy(
                    targets = envelope.targets + extra,
                    clipItems = envelope.clipItems + ClipItemEnvelope(extra.targetUri),
                )
            },
            validSelectionEnvelope(2).copy(hostSessionPresent = false),
            validSelectionEnvelope(2).copy(actionId = ThreeAmberPlayerPlugin.ACTION_ID),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExplorer(envelope))
        }
    }

    @Test
    fun explorerSelection_rejectsDuplicateIdentitiesAndOrderMismatch() {
        val valid = validSelectionEnvelope(3)
        val first = valid.targets.first()
        val second = valid.targets[1]
        listOf(
            valid.copy(targets = valid.targets.toMutableList().also { it[1] = second.copy(targetId = first.targetId) }),
            valid.copy(targets = valid.targets.toMutableList().also { it[1] = second.copy(targetUri = first.targetUri) }),
            valid.copy(
                clipItems = valid.clipItems.toMutableList().also {
                    val swapped = it[0]
                    it[0] = it[1]
                    it[1] = swapped
                },
            ),
            valid.copy(dataUri = valid.targets[1].targetUri),
            valid.copy(envelopeDisplayName = valid.targets[1].displayName),
            valid.copy(envelopeDeclaredSize = valid.targets[1].declaredSize),
            valid.copy(envelopeMimeType = "video/*"),
        ).forEach { envelope ->
            assertNull(VideoRequestPolicy.validateExplorer(envelope))
        }
    }

    @Test
    fun explorerRequest_acceptsAnySafeNameWhenTrustedHostDeclaresVideo() {
        val target = targetEnvelope(0).copy(
            targetUri = "$PARENT_URI/camera.wmv",
            displayName = "camera.wmv",
            mimeType = "video/*",
        )
        val result = VideoRequestPolicy.validateExplorer(
            validExplorerEnvelope().copy(
                dataUri = target.targetUri,
                clipItems = listOf(ClipItemEnvelope(target.targetUri)),
                targets = listOf(target),
                envelopeDisplayName = target.displayName,
                envelopeDeclaredSize = target.declaredSize,
                envelopeMimeType = target.mimeType,
            ),
        )

        assertNotNull(result)
        assertEquals("camera.wmv", result?.primaryTarget?.displayName)
        assertEquals("video/*", result?.primaryTarget?.mimeType)
    }

    @Test
    fun explorerRequest_rejectsWrongIdentitySourceOrHostBuild() {
        listOf(
            validExplorerEnvelope().copy(action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION),
            validExplorerEnvelope().copy(actionId = "other-action"),
            validExplorerEnvelope().copy(protocolVersion = 11),
            validExplorerEnvelope().copy(sourceSurface = "dialog"),
            validExplorerEnvelope().copy(hostVersionCode = ThreeAmberPlayerPlugin.REQUIRED_HOST_VERSION - 1),
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
    fun explorerRequest_rejectsUnsafeUnrelatedOrInconsistentTargetMetadata() {
        val valid = validExplorerEnvelope()
        val target = valid.targets.single()
        val invalidTargets = listOf(
            target.copy(targetUri = "file:///root/videos/sample.mp4"),
            target.copy(targetUri = "$TARGET_URI?mode=read"),
            target.copy(targetUri = "$TARGET_URI#fragment"),
            target.copy(targetUri = "content://authority/root/videos/%2E%2E/sample.mp4"),
            target.copy(targetUri = "content://authority/root/videos/sample%2Fmp4"),
            target.copy(displayName = "other.mp4"),
            target.copy(displayName = "sample\u202Emp4"),
            target.copy(declaredSize = -1L),
            target.copy(declaredSize = 8L * 1024L * 1024L * 1024L * 1024L + 1L),
            target.copy(lastModified = -2L),
            target.copy(targetKind = 2),
            target.copy(targetId = "bad id"),
            target.copy(mimeType = "application/octet-stream"),
            target.copy(mimeType = "Video/mp4"),
            target.copy(mimeType = "video/mp4; charset=utf-8"),
        )
        invalidTargets.forEach { invalid ->
            assertNull(
                VideoRequestPolicy.validateExplorer(
                    valid.copy(
                        dataUri = invalid.targetUri,
                        clipItems = listOf(ClipItemEnvelope(invalid.targetUri)),
                        targets = listOf(invalid),
                        envelopeDisplayName = invalid.displayName,
                        envelopeDeclaredSize = invalid.declaredSize,
                        envelopeMimeType = invalid.mimeType,
                    ),
                ),
            )
        }
        listOf(
            "content://authority/root/other",
            TARGET_URI,
            "content://other/root/videos",
        ).forEach { parent ->
            assertNull(VideoRequestPolicy.validateExplorer(valid.copy(parentUri = parent)))
        }
    }

    @Test
    fun explorerRequest_requiresExactTargetClipItems() {
        val valid = validExplorerEnvelope()
        listOf(
            emptyList(),
            listOf(ClipItemEnvelope(TARGET_URI), ClipItemEnvelope(PARENT_URI)),
            listOf(ClipItemEnvelope(PARENT_URI)),
            listOf(ClipItemEnvelope(TARGET_URI, hasText = true)),
            listOf(ClipItemEnvelope(TARGET_URI, hasIntent = true)),
        ).forEach { clipItems ->
            assertNull(VideoRequestPolicy.validateExplorer(valid.copy(clipItems = clipItems)))
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
    fun internalRequest_requiresOneExactClipAndOnlyDeclaredExtras() {
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

    private fun validExplorerEnvelope(): ExplorerRequestEnvelope {
        val target = targetEnvelope(0)
        return ExplorerRequestEnvelope(
            action = VideoRequestPolicy.EXPLORER_EXECUTE_ACTION,
            actionId = ThreeAmberPlayerPlugin.ACTION_ID,
            protocolVersion = VideoRequestPolicy.PROTOCOL_VERSION,
            sourceSurface = VideoRequestPolicy.SOURCE_SURFACE_MAIN,
            hostVersionCode = ThreeAmberPlayerPlugin.REQUIRED_HOST_VERSION,
            requestId = "123e4567-e89b-12d3-a456-426614174000",
            dataUri = target.targetUri,
            parentUri = PARENT_URI,
            parentDisplayPath = "/storage/emulated/0/videos",
            clipItems = listOf(ClipItemEnvelope(target.targetUri)),
            grants = EXPLORER_GRANTS,
            targets = listOf(target),
            envelopeDisplayName = target.displayName,
            envelopeDeclaredSize = target.declaredSize,
            envelopeMimeType = target.mimeType,
            hostSessionPresent = false,
        )
    }

    private fun validSelectionEnvelope(count: Int): ExplorerRequestEnvelope {
        require(count in 1..ExplorerSelectionPolicy.MAX_TARGETS)
        // Deliberately non-natural: validation must never sort the host's selection order.
        val targets = (0 until count).map { index -> targetEnvelope((index * 37) % count) }
        val first = targets.first()
        return validExplorerEnvelope().copy(
            actionId = ThreeAmberPlayerPlugin.ACTION_SELECTION_ID,
            dataUri = first.targetUri,
            clipItems = targets.map { ClipItemEnvelope(it.targetUri) },
            targets = targets,
            envelopeDisplayName = first.displayName,
            envelopeDeclaredSize = first.declaredSize,
            envelopeMimeType = "*/*",
            hostSessionPresent = true,
        )
    }

    private fun targetEnvelope(index: Int): ExplorerTargetEnvelope {
        val name = if (index == 0) "sample.mp4" else "selected-$index.mp4"
        return ExplorerTargetEnvelope(
            targetId = "target-$index",
            targetUri = "$PARENT_URI/$name",
            targetKind = 1,
            displayName = name,
            declaredSize = 42L + index,
            lastModified = 1L + index,
            mimeType = "video/mp4",
        )
    }

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
