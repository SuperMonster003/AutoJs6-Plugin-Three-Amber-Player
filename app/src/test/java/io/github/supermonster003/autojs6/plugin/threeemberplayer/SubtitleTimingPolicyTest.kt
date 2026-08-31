package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubtitleTimingPolicyTest {

    @Test
    fun offset_isClampedAndRoundedToOneTenthSecond() {
        assertEquals(600_000L, SubtitleTimingPolicy.normalizeOffsetMs(Long.MAX_VALUE))
        assertEquals(-600_000L, SubtitleTimingPolicy.normalizeOffsetMs(Long.MIN_VALUE))
        assertEquals(100L, SubtitleTimingPolicy.normalizeOffsetMs(149L))
        assertEquals(200L, SubtitleTimingPolicy.normalizeOffsetMs(150L))
        assertEquals("+1.2s", SubtitleTimingPolicy.formatOffset(1_249L))
        assertEquals("-0.1s", SubtitleTimingPolicy.formatOffset(-100L))
    }

    @Test
    fun srtShift_movesBothBoundsAndClampsNegativeTime() {
        val input = """
            1
            00:00:01,250 --> 00:00:03,500
            First at 00:10:00,000

            2
              00:10:00.000 --> 00:10:01.000 align:start
            Last
        """.trimIndent()

        val shifted = SubtitleTimingPolicy.shift(input, "application/x-subrip", -2_000L)

        assertTrue("00:00:00,000 --> 00:00:01,500" in shifted)
        assertTrue("  00:09:58.000 --> 00:09:59.000 align:start" in shifted)
        assertTrue("First at 00:10:00,000" in shifted)
    }

    @Test
    fun assShift_onlyChangesDialogueStartAndEndFields() {
        val input = """
            [Script Info]
            Title: 0:00:01.00 must stay text
            [Events]
            Dialogue: 0,0:00:05.20,0:00:07.40,Default,,0,0,0,,Hello
        """.trimIndent()

        val shifted = SubtitleTimingPolicy.shift(input, "text/x-ssa", 1_300L)

        assertTrue("Title: 0:00:01.00 must stay text" in shifted)
        assertTrue("Dialogue: 0,0:00:06.50,0:00:08.70" in shifted)
    }

    @Test
    fun unsupportedMimeAndZeroOffset_areNoOps() {
        val text = "00:00:01,000 --> 00:00:02,000"
        assertEquals(text, SubtitleTimingPolicy.shift(text, "text/plain", 1_000L))
        assertEquals(text, SubtitleTimingPolicy.shift(text, "application/x-subrip", 0L))
    }
}
