package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoTransformPolicyTest {

    @Test
    fun zoomMagnitude_composesWithEachScreenRelativeMirrorAxis() {
        assertTransform(VideoMirrorMode.NONE, 2f, 2f)
        assertTransform(VideoMirrorMode.HORIZONTAL, -2f, 2f)
        assertTransform(VideoMirrorMode.VERTICAL, 2f, -2f)
        assertTransform(VideoMirrorMode.BOTH, -2f, -2f)
    }

    @Test
    fun invalidScaleAndPivots_areNormalizedWithoutChangingMirrorChoice() {
        val transform = VideoTransformPolicy.resolve(
            Float.NaN,
            -1f,
            Float.POSITIVE_INFINITY,
            VideoMirrorMode.HORIZONTAL,
        )
        assertEquals(-1f, transform.scaleX, 0f)
        assertEquals(1f, transform.scaleY, 0f)
        assertEquals(0f, transform.pivotXFraction, 0f)
        assertEquals(0.5f, transform.pivotYFraction, 0f)
    }

    @Test
    fun transform_isLayoutIndependentSoRotationCanReapplyTheSameSessionState() {
        val before = VideoTransformPolicy.resolve(1.5f, 0.2f, 0.8f, VideoMirrorMode.VERTICAL)
        val after = VideoTransformPolicy.resolve(1.5f, 0.2f, 0.8f, VideoMirrorMode.VERTICAL)
        assertEquals(before, after)
    }

    private fun assertTransform(mode: VideoMirrorMode, expectedX: Float, expectedY: Float) {
        val transform = VideoTransformPolicy.resolve(2f, 0.25f, 0.75f, mode)
        assertEquals(expectedX, transform.scaleX, 0f)
        assertEquals(expectedY, transform.scaleY, 0f)
        assertEquals(0.25f, transform.pivotXFraction, 0f)
        assertEquals(0.75f, transform.pivotYFraction, 0f)
    }
}
