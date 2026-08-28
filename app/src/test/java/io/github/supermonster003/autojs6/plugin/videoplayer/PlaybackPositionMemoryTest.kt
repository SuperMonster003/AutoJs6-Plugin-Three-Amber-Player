package io.github.supermonster003.autojs6.plugin.videoplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackPositionMemoryTest {

    @Test
    fun contentKey_isStableLowercaseSha256Hex() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            PlaybackPositionMemory.contentKey("abc"),
        )
        val key = PlaybackPositionMemory.contentKey("content://authority/root/videos/sample.mp4")
        assertEquals(64, key.length)
        assertTrue(key.all { it in '0'..'9' || it in 'a'..'f' })
        assertNotEquals(key, PlaybackPositionMemory.contentKey("content://authority/root/videos/other.mp4"))
    }

    @Test
    fun shouldRemember_requiresLongEnoughMediaAndMeaningfulPosition() {
        assertTrue(PlaybackPositionMemory.shouldRemember(10_000L, 60_000L))
        assertFalse(PlaybackPositionMemory.shouldRemember(10_000L, 29_000L))
        assertFalse(PlaybackPositionMemory.shouldRemember(4_999L, 60_000L))
        assertFalse(PlaybackPositionMemory.shouldRemember(57_000L, 60_000L))
    }

    @Test
    fun isCompleted_marksFinalFivePercent() {
        assertFalse(PlaybackPositionMemory.isCompleted(56_999L, 60_000L))
        assertTrue(PlaybackPositionMemory.isCompleted(57_000L, 60_000L))
        assertTrue(PlaybackPositionMemory.isCompleted(60_000L, 60_000L))
        assertFalse(PlaybackPositionMemory.isCompleted(10_000L, 0L))
    }

    @Test
    fun updated_addsRefreshesAndRemovesEntries() {
        val key = PlaybackPositionMemory.contentKey("content://a/v/sample.mp4")

        val added = PlaybackPositionMemory.updated(emptyMap(), key, 10_000L, 60_000L, 1L)
        assertEquals(RememberedPosition(10_000L, 60_000L, 1L), added[key])

        val refreshed = PlaybackPositionMemory.updated(added, key, 20_000L, 60_000L, 2L)
        assertEquals(RememberedPosition(20_000L, 60_000L, 2L), refreshed[key])

        val completed = PlaybackPositionMemory.updated(refreshed, key, 59_000L, 60_000L, 3L)
        assertNull(completed[key])
    }

    @Test
    fun updated_keepsExistingBookmarkForTooEarlyPositions() {
        val key = PlaybackPositionMemory.contentKey("content://a/v/sample.mp4")
        val existing = mapOf(key to RememberedPosition(40_000L, 60_000L, 1L))

        val untouched = PlaybackPositionMemory.updated(existing, key, 2_000L, 60_000L, 2L)
        assertEquals(existing, untouched)
    }

    @Test
    fun prune_dropsOldestEntriesBeyondCapacity() {
        val entries = (0 until PlaybackPositionMemory.MAX_ENTRIES + 10).associate { index ->
            "key$index" to RememberedPosition(10_000L, 60_000L, index.toLong())
        }
        val pruned = PlaybackPositionMemory.prune(entries)

        assertEquals(PlaybackPositionMemory.MAX_ENTRIES, pruned.size)
        assertNull(pruned["key0"])
        assertNull(pruned["key9"])
        assertEquals(entries["key10"], pruned["key10"])
        assertEquals(entries["key${PlaybackPositionMemory.MAX_ENTRIES + 9}"], pruned["key${PlaybackPositionMemory.MAX_ENTRIES + 9}"])
    }

    @Test
    fun codec_roundTripsEntries() {
        val entries = mapOf(
            PlaybackPositionMemory.contentKey("content://a/v/one.mp4") to RememberedPosition(10_000L, 60_000L, 1L),
            PlaybackPositionMemory.contentKey("content://a/v/two.mkv") to RememberedPosition(20_000L, 90_000L, 2L),
        )
        assertEquals(entries, PlaybackPositionMemory.decode(PlaybackPositionMemory.encode(entries)))
        assertEquals("", PlaybackPositionMemory.encode(emptyMap()))
    }

    @Test
    fun decode_toleratesMissingAndMalformedInput() {
        assertEquals(emptyMap<String, RememberedPosition>(), PlaybackPositionMemory.decode(null))
        assertEquals(emptyMap<String, RememberedPosition>(), PlaybackPositionMemory.decode(""))
        assertEquals(emptyMap<String, RememberedPosition>(), PlaybackPositionMemory.decode("garbage"))

        val valid = "abc123|10000|60000|1"
        val decoded = PlaybackPositionMemory.decode(
            listOf(
                valid,
                "missing|fields",
                "|10000|60000|1",
                "key|-1|60000|1",
                "key|10000|0|1",
                "key|10000|60000|x",
            ).joinToString("\n"),
        )
        assertEquals(mapOf("abc123" to RememberedPosition(10_000L, 60_000L, 1L)), decoded)
    }
}
