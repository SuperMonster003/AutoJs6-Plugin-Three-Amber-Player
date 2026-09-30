package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoAdvancePromptPolicyTest {

    @Test
    fun prompt_isLimitedToAQueueWithANextItemAndNoCompetingEndBehavior() {
        assertTrue(
            AutoAdvancePromptPolicy.shouldShowPrompt(
                itemCount = 3,
                hasNextMediaItem = true,
                playbackMode = VideoPlaybackMode.SEQUENCE,
                abLoopActive = false,
                stopAtEndOfVideo = false,
            ),
        )
        assertTrue(
            AutoAdvancePromptPolicy.shouldShowPrompt(
                itemCount = 3,
                hasNextMediaItem = true,
                playbackMode = VideoPlaybackMode.SHUFFLE,
                abLoopActive = false,
                stopAtEndOfVideo = false,
            ),
        )
        assertFalse(prompt(itemCount = 1))
        assertFalse(prompt(hasNext = false))
        assertFalse(prompt(mode = VideoPlaybackMode.REPEAT_ONE))
        assertFalse(prompt(abLoop = true))
        assertFalse(prompt(stopAtEnd = true))
    }

    @Test
    fun playerPausesAtItemEndForPromptOrStopAtEndOnly() {
        assertTrue(pauseAtEnd())
        assertTrue(pauseAtEnd(itemCount = 1, stopAtEnd = true))
        assertFalse(pauseAtEnd(itemCount = 1))
        assertFalse(pauseAtEnd(mode = VideoPlaybackMode.REPEAT_ONE))
        assertFalse(pauseAtEnd(abLoop = true))
    }

    @Test
    fun countdownRoundsUpAndStopsAtZero() {
        assertEquals(3, AutoAdvancePromptPolicy.remainingSeconds(4_000L, 1_000L))
        assertEquals(3, AutoAdvancePromptPolicy.remainingSeconds(4_000L, 1_001L))
        assertEquals(1, AutoAdvancePromptPolicy.remainingSeconds(4_000L, 3_999L))
        assertEquals(0, AutoAdvancePromptPolicy.remainingSeconds(4_000L, 4_000L))
        assertEquals(0, AutoAdvancePromptPolicy.remainingSeconds(4_000L, 5_000L))
    }

    private fun prompt(
        itemCount: Int = 3,
        hasNext: Boolean = true,
        mode: VideoPlaybackMode = VideoPlaybackMode.SEQUENCE,
        abLoop: Boolean = false,
        stopAtEnd: Boolean = false,
    ): Boolean = AutoAdvancePromptPolicy.shouldShowPrompt(
        itemCount,
        hasNext,
        mode,
        abLoop,
        stopAtEnd,
    )

    private fun pauseAtEnd(
        itemCount: Int = 3,
        mode: VideoPlaybackMode = VideoPlaybackMode.SEQUENCE,
        abLoop: Boolean = false,
        stopAtEnd: Boolean = false,
    ): Boolean = AutoAdvancePromptPolicy.shouldPauseAtItemEnd(
        itemCount,
        mode,
        abLoop,
        stopAtEnd,
    )
}
