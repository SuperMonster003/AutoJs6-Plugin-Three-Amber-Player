package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class VideoPlaybackModePolicyTest {

    @Test
    fun rememberedMode_defaultsOffAndRejectsUnknownStoredValues() {
        assertEquals(
            VideoPlaybackMode.SEQUENCE,
            VideoPlaybackModePolicy.restoreRemembered(false, VideoPlaybackMode.SHUFFLE.name),
        )
        assertEquals(
            VideoPlaybackMode.SHUFFLE,
            VideoPlaybackModePolicy.restoreRemembered(true, VideoPlaybackMode.SHUFFLE.name),
        )
        assertEquals(
            VideoPlaybackMode.SEQUENCE,
            VideoPlaybackModePolicy.restoreRemembered(true, "future-mode"),
        )
    }

    @Test
    fun singleItem_normalizesRememberedShuffleButKeepsRepeatOne() {
        assertEquals(
            VideoPlaybackMode.SEQUENCE,
            VideoPlaybackModePolicy.normalizeForItemCount(VideoPlaybackMode.SHUFFLE, 1),
        )
        assertEquals(
            VideoPlaybackMode.REPEAT_ONE,
            VideoPlaybackModePolicy.normalizeForItemCount(VideoPlaybackMode.REPEAT_ONE, 1),
        )
        assertEquals(
            VideoPlaybackMode.SHUFFLE,
            VideoPlaybackModePolicy.normalizeForItemCount(VideoPlaybackMode.SHUFFLE, 2),
        )
    }

    @Test
    fun queueCyclesSequenceShuffleRepeatOne() {
        assertEquals(
            VideoPlaybackMode.SHUFFLE,
            VideoPlaybackModePolicy.next(VideoPlaybackMode.SEQUENCE, 3),
        )
        assertEquals(
            VideoPlaybackMode.REPEAT_ONE,
            VideoPlaybackModePolicy.next(VideoPlaybackMode.SHUFFLE, 3),
        )
        assertEquals(
            VideoPlaybackMode.SEQUENCE,
            VideoPlaybackModePolicy.next(VideoPlaybackMode.REPEAT_ONE, 3),
        )
    }

    @Test
    fun singleItemTogglesSequenceAndRepeatOne() {
        assertEquals(
            VideoPlaybackMode.REPEAT_ONE,
            VideoPlaybackModePolicy.next(VideoPlaybackMode.SEQUENCE, 1),
        )
        assertEquals(
            VideoPlaybackMode.SEQUENCE,
            VideoPlaybackModePolicy.next(VideoPlaybackMode.REPEAT_ONE, 1),
        )
        assertEquals(
            VideoPlaybackMode.REPEAT_ONE,
            VideoPlaybackModePolicy.next(VideoPlaybackMode.SHUFFLE, 1),
        )
    }

    @Test
    fun emptyQueueIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            VideoPlaybackModePolicy.next(VideoPlaybackMode.SEQUENCE, 0)
        }
    }
}
