package io.github.supermonster003.autojs6.plugin.three.amber.player

import kotlin.math.ceil

internal enum class SleepTimerMode(val durationMinutes: Long?) {
    OFF(null),
    MINUTES_15(15L),
    MINUTES_30(30L),
    MINUTES_45(45L),
    MINUTES_60(60L),
    END_OF_VIDEO(null),
}

/** Pure sleep-timer arithmetic based on elapsed realtime rather than wall-clock time. */
internal object SleepTimerPolicy {

    private const val MILLIS_PER_MINUTE = 60_000L

    fun deadlineElapsedRealtimeMs(mode: SleepTimerMode, nowElapsedRealtimeMs: Long): Long? =
        mode.durationMinutes?.let { minutes ->
            val now = nowElapsedRealtimeMs.coerceAtLeast(0L)
            val duration = minutes * MILLIS_PER_MINUTE
            if (now > Long.MAX_VALUE - duration) Long.MAX_VALUE else now + duration
        }

    fun remainingMs(deadlineElapsedRealtimeMs: Long, nowElapsedRealtimeMs: Long): Long =
        (deadlineElapsedRealtimeMs - nowElapsedRealtimeMs.coerceAtLeast(0L)).coerceAtLeast(0L)

    fun remainingMinutes(deadlineElapsedRealtimeMs: Long, nowElapsedRealtimeMs: Long): Long {
        val remaining = remainingMs(deadlineElapsedRealtimeMs, nowElapsedRealtimeMs)
        return if (remaining == 0L) 0L else ceil(remaining.toDouble() / MILLIS_PER_MINUTE).toLong()
    }

    fun isExpired(deadlineElapsedRealtimeMs: Long, nowElapsedRealtimeMs: Long): Boolean =
        remainingMs(deadlineElapsedRealtimeMs, nowElapsedRealtimeMs) == 0L
}
