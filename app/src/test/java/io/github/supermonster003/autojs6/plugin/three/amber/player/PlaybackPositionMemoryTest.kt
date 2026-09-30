package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackPositionMemoryTest {

    private val now = 1_787_932_800_000L

    @Test
    fun derivesStableOpaqueKeysWithoutPersistingUris() {
        val uri = "content://com.example.provider/video/episode01.mkv"
        val key = PlaybackPositionMemory.contentKey(uri)

        assertEquals(64, key.length)
        assertTrue(key.all { it in "0123456789abcdef" })
        assertEquals(key, PlaybackPositionMemory.contentKey(uri))
        assertFalse(key == PlaybackPositionMemory.contentKey(uri + "x"))
        assertFalse(key.contains("content"))
    }

    @Test
    fun remembersOnlyMeaningfulUnfinishedProgress() {
        assertFalse(PlaybackPositionMemory.shouldRemember(0L, 300_000L))
        assertFalse(
            PlaybackPositionMemory.shouldRemember(
                PlaybackPositionMemory.MIN_POSITION_MS - 1,
                300_000L,
            ),
        )
        assertTrue(
            PlaybackPositionMemory.shouldRemember(
                PlaybackPositionMemory.MIN_POSITION_MS,
                300_000L,
            ),
        )
        assertTrue(PlaybackPositionMemory.shouldRemember(150_000L, 300_000L))
        assertFalse(PlaybackPositionMemory.shouldRemember(285_000L, 300_000L))
        assertFalse(PlaybackPositionMemory.shouldRemember(299_999L, 300_000L))
        assertFalse(PlaybackPositionMemory.shouldRemember(10_000L, 0L))
    }

    @Test
    fun resumesOnlyTheSameRecentTarget() {
        val firstKey = PlaybackPositionMemory.contentKey("content://example/video/a.mp4")
        val secondKey = PlaybackPositionMemory.contentKey("content://example/video/b.mp4")
        val record = RememberedPosition(firstKey, 90_000L, 300_000L, now)

        assertEquals(90_000L, PlaybackPositionMemory.resumePosition(record, firstKey))
        assertNull(PlaybackPositionMemory.resumePosition(record, secondKey))
        assertNull(PlaybackPositionMemory.resumePosition(null, firstKey))
    }

    @Test
    fun updatingAThenBRetainsOnlyBAndCompletionClearsIt() {
        val firstKey = PlaybackPositionMemory.contentKey("content://example/video/a.mp4")
        val secondKey = PlaybackPositionMemory.contentKey("content://example/video/b.mp4")
        val first = PlaybackPositionMemory.updated(firstKey, 60_000L, 300_000L, now)
        val second = PlaybackPositionMemory.updated(secondKey, 90_000L, 300_000L, now + 1)

        assertEquals(firstKey, first?.targetKey)
        assertEquals(secondKey, second?.targetKey)
        assertNull(PlaybackPositionMemory.resumePosition(second, firstKey))
        assertEquals(90_000L, PlaybackPositionMemory.resumePosition(second, secondKey))
        assertNull(PlaybackPositionMemory.updated(secondKey, 300_000L, 300_000L, now + 2))
    }

    @Test
    fun codecRoundTripsAndRejectsMalformedOrCompletedRecords() {
        val record = RememberedPosition(
            PlaybackPositionMemory.contentKey("content://example/video/a.webm"),
            123_456L,
            654_321L,
            now,
        )

        assertEquals(record, PlaybackPositionMemory.decode(PlaybackPositionMemory.encode(record)))
        assertNull(PlaybackPositionMemory.decode(null))
        assertNull(PlaybackPositionMemory.decode(""))
        assertNull(PlaybackPositionMemory.decode("1|bad|1|2|3"))
        assertNull(PlaybackPositionMemory.decode("2|bad-key|1|2|3"))
        assertNull(
            PlaybackPositionMemory.decode(
                "2|${record.targetKey}|654320|654321|$now",
            ),
        )
    }
}
