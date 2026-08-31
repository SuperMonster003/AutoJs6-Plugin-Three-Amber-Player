package io.github.supermonster003.autojs6.plugin.threeemberplayer

import kotlin.math.roundToLong

internal object FrameStepPolicy {
    const val FALLBACK_FRAME_DURATION_MS = 33L
    const val MIN_FRAME_DURATION_MS = 4L
    const val MAX_FRAME_DURATION_MS = 1_000L

    fun frameDurationMs(frameRate: Float?): Long {
        val validRate = frameRate?.takeIf { it.isFinite() && it > 0f }
            ?: return FALLBACK_FRAME_DURATION_MS
        return (1_000.0 / validRate).roundToLong().coerceIn(MIN_FRAME_DURATION_MS, MAX_FRAME_DURATION_MS)
    }

    fun targetPositionMs(positionMs: Long, durationMs: Long, frameRate: Float?, direction: Int): Long {
        require(direction == -1 || direction == 1)
        val upperBound = durationMs.takeIf { it > 0L } ?: Long.MAX_VALUE
        val step = frameDurationMs(frameRate)
        val target = if (direction < 0) positionMs - step else positionMs + step
        return target.coerceIn(0L, upperBound)
    }
}

internal data class AbLoopState(
    val pointAMs: Long? = null,
    val pointBMs: Long? = null,
) {
    val active: Boolean
        get() = pointAMs != null && pointBMs != null

    val awaitingPointB: Boolean
        get() = pointAMs != null && pointBMs == null
}

/** Three-state A -> B -> clear policy with normalized bounds and deterministic A > B handling. */
internal object AbLoopPolicy {
    const val MIN_LOOP_DURATION_MS = 100L

    fun toggle(state: AbLoopState, positionMs: Long, durationMs: Long): AbLoopState {
        val duration = durationMs.coerceAtLeast(0L)
        val position = positionMs.coerceIn(0L, duration)
        if (state.active) return AbLoopState()
        val pointA = state.pointAMs
        if (pointA == null) return AbLoopState(pointAMs = position)

        var start = minOf(pointA.coerceIn(0L, duration), position)
        var end = maxOf(pointA.coerceIn(0L, duration), position)
        if (end - start < MIN_LOOP_DURATION_MS) {
            end = (start + MIN_LOOP_DURATION_MS).coerceAtMost(duration)
            if (end - start < MIN_LOOP_DURATION_MS) {
                start = (end - MIN_LOOP_DURATION_MS).coerceAtLeast(0L)
            }
        }
        return if (end > start) AbLoopState(start, end) else AbLoopState(pointAMs = start)
    }

    fun loopTargetMs(state: AbLoopState, positionMs: Long): Long? {
        val start = state.pointAMs ?: return null
        val end = state.pointBMs ?: return null
        return start.takeIf { positionMs >= end }
    }
}
