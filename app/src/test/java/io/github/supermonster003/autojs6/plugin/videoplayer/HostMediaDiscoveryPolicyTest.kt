package io.github.supermonster003.autojs6.plugin.videoplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HostMediaDiscoveryPolicyTest {

    @Test
    fun videosUseStableNaturalOrderAndStartAtSelectedItem() {
        val queue = HostMediaDiscoveryPolicy.discover(
            selectedDisplayName = "episode10.mkv",
            selectedMimeType = "video/x-matroska",
            selectedSize = 10L,
            siblings = listOf(
                file("episode10.mkv", "video/x-matroska"),
                file("episode2.mp4", "video/mp4"),
                file("episode01.mkv", "video/x-matroska"),
                file("notes.txt", "text/plain"),
            ),
        )

        assertEquals(listOf("episode01.mkv", "episode2.mp4", "episode10.mkv"), queue.items.map { it.displayName })
        assertEquals(2, queue.startIndex)
        assertEquals("", queue.items[queue.startIndex].relativePath)
    }

    @Test
    fun hostRecognizedVideoTypesAreAcceptedWithoutLegacyExtensionAllowlist() {
        val queue = HostMediaDiscoveryPolicy.discover(
            selectedDisplayName = "camera.wmv",
            selectedMimeType = "video/*",
            selectedSize = 10L,
            siblings = listOf(
                file("camera.wmv", "video/x-ms-wmv"),
                file("broadcast.mxf", "video/mxf"),
                file("notes.bin", "application/octet-stream"),
            ),
        )

        assertEquals(listOf("broadcast.mxf", "camera.wmv"), queue.items.map { it.displayName })
        val selected = queue.items[queue.startIndex]
        assertEquals("camera.wmv", selected.displayName)
        assertEquals("video/*", selected.mimeType)
        assertEquals(10L, selected.size)
    }

    @Test
    fun subtitlesMatchExactAndLanguageSuffixButRemainSeparateFromAudio() {
        val queue = HostMediaDiscoveryPolicy.discover(
            selectedDisplayName = "Turn.of.the.Tide.S01E01.mp4",
            selectedMimeType = "video/mp4",
            selectedSize = 10L,
            siblings = listOf(
                file("Turn.of.the.Tide.S01E01.mp4", "video/mp4"),
                file("Turn.of.the.Tide.S01E01.srt", "application/x-subrip"),
                file("Turn.of.the.Tide.S01E01.zh-Hant.ass", "text/x-ssa"),
                file("Turn.of.the.Tide.S01E010.srt", "application/x-subrip"),
                file("Turn.of.the.Tide.S01E01.commentary.srt", "application/x-subrip"),
                file("Turn.of.the.Tide.S01E01.m4a", "audio/mp4"),
            ),
        )

        val subtitles = queue.items.single().subtitles
        assertEquals(
            listOf("Turn.of.the.Tide.S01E01.srt", "Turn.of.the.Tide.S01E01.zh-Hant.ass"),
            subtitles.map { it.displayName },
        )
        assertNull(subtitles.first().languageTag)
        assertEquals("zh-Hant", subtitles.last().languageTag)
        assertFalse(subtitles.any { it.displayName.endsWith(".m4a") })
    }

    @Test
    fun unreadableDirectoriesAndSymbolicLinksAreExcluded() {
        val queue = HostMediaDiscoveryPolicy.discover(
            selectedDisplayName = "selected.mkv",
            selectedMimeType = "video/x-matroska",
            selectedSize = 10L,
            siblings = listOf(
                file("selected.mkv", "video/x-matroska"),
                file("unreadable.mp4", "video/mp4").copy(readable = false),
                file("linked.mp4", "video/mp4").copy(symbolicLink = true),
                file("folder.mkv", "inode/directory").copy(kind = 2),
                file("nested/escape.mp4", "video/mp4"),
            ),
        )

        assertEquals(listOf("selected.mkv"), queue.items.map { it.displayName })
    }

    @Test
    fun naturalComparatorHandlesNumericRunsAndStableCase() {
        assertTrue(HostMediaDiscoveryPolicy.compareNaturally("video2.mkv", "video10.mkv") < 0)
        assertTrue(HostMediaDiscoveryPolicy.compareNaturally("video1.mkv", "video01.mkv") < 0)
        assertTrue(HostMediaDiscoveryPolicy.compareNaturally("Video.mkv", "video.mkv") < 0)
        assertTrue(HostMediaDiscoveryPolicy.compareNaturally("video2.mkv", "Video10.mkv") < 0)
        assertEquals(0, HostMediaDiscoveryPolicy.compareNaturally("same", "same"))
    }

    @Test
    fun queueAndSubtitlePayloadsRemainBoundedAroundSelectedItem() {
        val videoFiles = (0 until 200).map { index ->
            file("episode$index.mkv", "video/x-matroska")
        }
        val subtitleFiles = (0 until 20).map { index ->
            file("episode100.en-a$index.srt", "application/x-subrip")
        }

        val queue = HostMediaDiscoveryPolicy.discover(
            selectedDisplayName = "episode100.mkv",
            selectedMimeType = "video/x-matroska",
            selectedSize = 10L,
            siblings = videoFiles + subtitleFiles,
        )

        assertEquals(HostMediaDiscoveryPolicy.MAX_QUEUE_ENTRIES, queue.items.size)
        assertEquals("episode100.mkv", queue.items[queue.startIndex].displayName)
        assertTrue(queue.items.sumOf { it.subtitles.size } <= HostMediaDiscoveryPolicy.MAX_TOTAL_SUBTITLE_ATTACHMENTS)
        assertTrue(queue.items.all { it.subtitles.size <= HostMediaDiscoveryPolicy.MAX_SUBTITLES_PER_VIDEO })
    }

    private fun file(name: String, mimeType: String) = ExplorerSiblingItem(
        relativePath = name,
        displayName = name,
        kind = 1,
        mimeType = mimeType,
        size = 10L,
        lastModified = 1L,
        readable = true,
        symbolicLink = false,
    )
}
