package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackMediaPolicyTest {

    @Test
    fun suggestedOrientation_accountsForRotationAndSquareMedia() {
        assertEquals(
            VideoOrientationSuggestion.LANDSCAPE,
            PlaybackMediaPolicy.suggestedOrientation(1920, 1080),
        )
        assertEquals(
            VideoOrientationSuggestion.PORTRAIT,
            PlaybackMediaPolicy.suggestedOrientation(1920, 1080, rotationDegrees = 90),
        )
        assertEquals(
            VideoOrientationSuggestion.NONE,
            PlaybackMediaPolicy.suggestedOrientation(1000, 1000),
        )
        assertEquals(
            VideoOrientationSuggestion.NONE,
            PlaybackMediaPolicy.suggestedOrientation(0, 1080),
        )
    }

    @Test
    fun suggestedOrientation_accountsForNonSquarePixels() {
        assertEquals(
            VideoOrientationSuggestion.LANDSCAPE,
            PlaybackMediaPolicy.suggestedOrientation(
                width = 720,
                height = 576,
                pixelWidthHeightRatio = 16f / 15f,
            ),
        )
    }

    @Test
    fun pipAspectRatio_preservesOrdinaryMediaAndRotation() {
        val landscape = PlaybackMediaPolicy.pipAspectRatio(1920, 1080)
        val portrait = PlaybackMediaPolicy.pipAspectRatio(1920, 1080, rotationDegrees = 90)

        assertTrue(landscape != null)
        assertEquals(16.0 / 9.0, landscape!!.value, 0.002)
        assertTrue(portrait != null)
        assertEquals(9.0 / 16.0, portrait!!.value, 0.002)
    }

    @Test
    fun pipAspectRatio_clampsExtremeMediaToPlatformRange() {
        val veryWide = PlaybackMediaPolicy.pipAspectRatio(10_000, 100)
        val veryTall = PlaybackMediaPolicy.pipAspectRatio(100, 10_000)

        assertEquals(PlaybackMediaPolicy.PIP_MAX_ASPECT_RATIO, veryWide!!.value, 0.001)
        assertEquals(PlaybackMediaPolicy.PIP_MIN_ASPECT_RATIO, veryTall!!.value, 0.001)
        assertNull(PlaybackMediaPolicy.pipAspectRatio(0, 1080))
    }

    @Test
    fun metadataFormatting_handlesKnownAndMissingValues() {
        assertEquals("23.98", PlaybackMediaPolicy.formatFrameRate(23.976f))
        assertEquals("30", PlaybackMediaPolicy.formatFrameRate(30f))
        assertNull(PlaybackMediaPolicy.formatFrameRate(-1f))
        assertNull(PlaybackMediaPolicy.formatFrameRate(Float.NaN))

        assertEquals(
            "avc1.640028 (video/avc)",
            PlaybackMediaPolicy.formatCodec("video/avc", "avc1.640028"),
        )
        assertEquals("audio/ac3", PlaybackMediaPolicy.formatCodec("audio/ac3", null))
        assertEquals("vp09", PlaybackMediaPolicy.formatCodec(null, "vp09"))
        assertNull(PlaybackMediaPolicy.formatCodec(" ", null))
    }
}
