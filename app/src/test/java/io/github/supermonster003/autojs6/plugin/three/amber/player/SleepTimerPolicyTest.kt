package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SleepTimerPolicyTest {

    @Test
    fun deadline_isAvailableOnlyForMinuteModes() {
        assertNull(SleepTimerPolicy.deadlineElapsedRealtimeMs(SleepTimerMode.OFF, 1_000L))
        assertNull(SleepTimerPolicy.deadlineElapsedRealtimeMs(SleepTimerMode.END_OF_VIDEO, 1_000L))
        assertEquals(
            901_000L,
            SleepTimerPolicy.deadlineElapsedRealtimeMs(SleepTimerMode.MINUTES_15, 1_000L),
        )
        assertEquals(
            3_600_000L,
            SleepTimerPolicy.deadlineElapsedRealtimeMs(SleepTimerMode.MINUTES_60, 0L),
        )
    }

    @Test
    fun deadline_saturatesInsteadOfOverflowing() {
        assertEquals(
            Long.MAX_VALUE,
            SleepTimerPolicy.deadlineElapsedRealtimeMs(
                SleepTimerMode.MINUTES_60,
                Long.MAX_VALUE - 1L,
            ),
        )
    }

    @Test
    fun remainingTime_clampsAndRoundsUp() {
        assertEquals(61_000L, SleepTimerPolicy.remainingMs(100_000L, 39_000L))
        assertEquals(2L, SleepTimerPolicy.remainingMinutes(100_000L, 39_000L))
        assertEquals(1L, SleepTimerPolicy.remainingMinutes(100_000L, 99_999L))
        assertEquals(0L, SleepTimerPolicy.remainingMinutes(100_000L, 100_000L))
        assertEquals(0L, SleepTimerPolicy.remainingMs(100_000L, 120_000L))
    }

    @Test
    fun expiration_usesElapsedRealtimeBoundary() {
        assertFalse(SleepTimerPolicy.isExpired(100_000L, 99_999L))
        assertTrue(SleepTimerPolicy.isExpired(100_000L, 100_000L))
        assertTrue(SleepTimerPolicy.isExpired(100_000L, 100_001L))
    }
}
