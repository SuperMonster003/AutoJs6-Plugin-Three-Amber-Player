package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrecisionPlaybackPolicyTest {

    @Test
    fun frameDuration_usesRateAndSafeFallback() {
        assertEquals(42L, FrameStepPolicy.frameDurationMs(23.976f))
        assertEquals(33L, FrameStepPolicy.frameDurationMs(30f))
        assertEquals(FrameStepPolicy.FALLBACK_FRAME_DURATION_MS, FrameStepPolicy.frameDurationMs(null))
        assertEquals(FrameStepPolicy.FALLBACK_FRAME_DURATION_MS, FrameStepPolicy.frameDurationMs(Float.NaN))
        assertEquals(FrameStepPolicy.FALLBACK_FRAME_DURATION_MS, FrameStepPolicy.frameDurationMs(0f))
    }

    @Test
    fun frameTarget_clampsAtMediaBounds() {
        assertEquals(0L, FrameStepPolicy.targetPositionMs(10L, 1_000L, 25f, -1))
        assertEquals(1_000L, FrameStepPolicy.targetPositionMs(990L, 1_000L, 25f, 1))
        assertEquals(540L, FrameStepPolicy.targetPositionMs(500L, 1_000L, 25f, 1))
    }

    @Test
    fun abStateMachine_setsAThenBThenClears() {
        val withA = AbLoopPolicy.toggle(AbLoopState(), 2_000L, 10_000L)
        assertTrue(withA.awaitingPointB)
        assertFalse(withA.active)

        val active = AbLoopPolicy.toggle(withA, 5_000L, 10_000L)
        assertEquals(2_000L, active.pointAMs)
        assertEquals(5_000L, active.pointBMs)
        assertTrue(active.active)
        assertNull(AbLoopPolicy.loopTargetMs(active, 4_999L))
        assertEquals(2_000L, AbLoopPolicy.loopTargetMs(active, 5_000L))

        assertEquals(AbLoopState(), AbLoopPolicy.toggle(active, 7_000L, 10_000L))
    }

    @Test
    fun abStateMachine_normalizesAAfterBAndEqualBoundary() {
        val reverse = AbLoopPolicy.toggle(
            AbLoopState(pointAMs = 8_000L),
            positionMs = 3_000L,
            durationMs = 10_000L,
        )
        assertEquals(3_000L, reverse.pointAMs)
        assertEquals(8_000L, reverse.pointBMs)

        val atEnd = AbLoopPolicy.toggle(
            AbLoopState(pointAMs = 10_000L),
            positionMs = 10_000L,
            durationMs = 10_000L,
        )
        assertEquals(9_900L, atEnd.pointAMs)
        assertEquals(10_000L, atEnd.pointBMs)
    }
}
