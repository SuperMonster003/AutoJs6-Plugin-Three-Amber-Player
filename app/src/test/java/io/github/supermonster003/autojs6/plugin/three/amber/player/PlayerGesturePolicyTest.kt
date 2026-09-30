package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerGesturePolicyTest {

    @Test
    fun verticalDragZone_splitsViewInHalves() {
        assertEquals(VerticalDragZone.BRIGHTNESS, PlayerGesturePolicy.verticalDragZone(0f, 1000))
        assertEquals(VerticalDragZone.BRIGHTNESS, PlayerGesturePolicy.verticalDragZone(499f, 1000))
        assertEquals(VerticalDragZone.VOLUME, PlayerGesturePolicy.verticalDragZone(500f, 1000))
        assertEquals(VerticalDragZone.VOLUME, PlayerGesturePolicy.verticalDragZone(999f, 1000))
    }

    @Test
    fun doubleTapZone_splitsViewInThirds() {
        assertEquals(DoubleTapZone.REWIND, PlayerGesturePolicy.doubleTapZone(0f, 900))
        assertEquals(DoubleTapZone.REWIND, PlayerGesturePolicy.doubleTapZone(299f, 900))
        assertEquals(DoubleTapZone.TOGGLE_PLAYBACK, PlayerGesturePolicy.doubleTapZone(300f, 900))
        assertEquals(DoubleTapZone.TOGGLE_PLAYBACK, PlayerGesturePolicy.doubleTapZone(599f, 900))
        assertEquals(DoubleTapZone.FORWARD, PlayerGesturePolicy.doubleTapZone(600f, 900))
        assertEquals(DoubleTapZone.FORWARD, PlayerGesturePolicy.doubleTapZone(899f, 900))
    }

    @Test
    fun seekDragTarget_mapsFullWidthToNinetySecondsOnLongMedia() {
        val target = PlayerGesturePolicy.seekDragTarget(
            startPositionMs = 100_000L,
            dragPx = 1000f,
            viewWidthPx = 1000,
            durationMs = 600_000L,
        )
        assertEquals(190_000L, target)
    }

    @Test
    fun seekDragTarget_shrinksWindowForShortMedia() {
        val target = PlayerGesturePolicy.seekDragTarget(
            startPositionMs = 0L,
            dragPx = 500f,
            viewWidthPx = 1000,
            durationMs = 30_000L,
        )
        assertEquals(15_000L, target)
    }

    @Test
    fun seekDragTarget_clampsToMediaBounds() {
        assertEquals(
            0L,
            PlayerGesturePolicy.seekDragTarget(5_000L, -1000f, 1000, 600_000L),
        )
        assertEquals(
            600_000L,
            PlayerGesturePolicy.seekDragTarget(590_000L, 1000f, 1000, 600_000L),
        )
        assertEquals(
            7_000L,
            PlayerGesturePolicy.seekDragTarget(7_000L, 1000f, 1000, 0L),
        )
        assertEquals(
            7_000L,
            PlayerGesturePolicy.seekDragTarget(7_000L, 1000f, 0, 600_000L),
        )
    }

    @Test
    fun draggedFraction_scalesByViewHeightAndClamps() {
        assertEquals(0.75f, PlayerGesturePolicy.draggedFraction(0.5f, 250f, 1000), 0.0001f)
        assertEquals(0.25f, PlayerGesturePolicy.draggedFraction(0.5f, -250f, 1000), 0.0001f)
        assertEquals(1f, PlayerGesturePolicy.draggedFraction(0.5f, 2000f, 1000), 0.0001f)
        assertEquals(0f, PlayerGesturePolicy.draggedFraction(0.5f, -2000f, 1000), 0.0001f)
        assertEquals(0.5f, PlayerGesturePolicy.draggedFraction(0.5f, 500f, 0), 0.0001f)
    }

    @Test
    fun sensitivity_scalesSeekAndVerticalGestures() {
        assertEquals(
            58_500L,
            PlayerGesturePolicy.seekDragTarget(
                startPositionMs = 0L,
                dragPx = 1000f,
                viewWidthPx = 1000,
                durationMs = 100_000L,
                sensitivity = GestureSensitivity.LOW,
            ),
        )
        assertEquals(
            1f,
            PlayerGesturePolicy.draggedFraction(
                startFraction = 0.5f,
                dragUpPx = 500f,
                viewHeightPx = 1000,
                sensitivity = GestureSensitivity.HIGH,
            ),
            0.0001f,
        )
    }

    @Test
    fun zoomScale_appliesSensitivityAndClamps() {
        assertEquals(
            2f,
            PlayerGesturePolicy.zoomScale(1f, 2f, GestureSensitivity.NORMAL),
            0.0001f,
        )
        assertTrue(
            PlayerGesturePolicy.zoomScale(1f, 2f, GestureSensitivity.HIGH) > 2f,
        )
        assertEquals(
            PlayerGesturePolicy.MAX_ZOOM_SCALE,
            PlayerGesturePolicy.zoomScale(4f, 2f),
            0.0001f,
        )
        assertEquals(
            PlayerGesturePolicy.MIN_ZOOM_SCALE,
            PlayerGesturePolicy.zoomScale(0.25f, 0.5f),
            0.0001f,
        )
        assertEquals(
            PlayerGesturePolicy.DEFAULT_ZOOM_SCALE,
            PlayerGesturePolicy.zoomScale(Float.NaN, 2f),
            0.0001f,
        )
    }

    @Test
    fun doubleTapSeekOptions_areStrictlyNormalized() {
        assertEquals(listOf(5_000L, 10_000L, 30_000L), PlayerGesturePolicy.DOUBLE_TAP_SEEK_OPTIONS_MS)
        assertEquals(5_000L, PlayerGesturePolicy.normalizedDoubleTapSeekMs(5_000L))
        assertEquals(10_000L, PlayerGesturePolicy.normalizedDoubleTapSeekMs(7_000L))
    }

    @Test
    fun percentLabel_roundsAndClamps() {
        assertEquals("0%", PlayerGesturePolicy.percentLabel(-0.5f))
        assertEquals("50%", PlayerGesturePolicy.percentLabel(0.5f))
        assertEquals("100%", PlayerGesturePolicy.percentLabel(1.5f))
    }

    @Test
    fun sliderValues_snapToValidStepsAndBounds() {
        assertEquals(
            0.41f,
            PlayerGesturePolicy.snapToSliderStep(0.413f, 0.01f, 1f, 0.01f),
            0.0001f,
        )
        assertEquals(
            0.47f,
            PlayerGesturePolicy.snapToSliderStep(7f / 15f, 0f, 1f, 0.01f),
            0.0001f,
        )
        assertEquals(
            4f,
            PlayerGesturePolicy.snapToSliderStep(5f, 0.25f, 4f, 0.05f),
            0.0001f,
        )
        assertEquals(
            0.25f,
            PlayerGesturePolicy.snapToSliderStep(Float.NaN, 0.25f, 4f, 0.05f),
            0.0001f,
        )
    }

    @Test
    fun formatTime_coversMinutesAndHours() {
        assertEquals("00:00", PlayerGesturePolicy.formatTime(-1L))
        assertEquals("00:00", PlayerGesturePolicy.formatTime(0L))
        assertEquals("00:59", PlayerGesturePolicy.formatTime(59_999L))
        assertEquals("01:01", PlayerGesturePolicy.formatTime(61_000L))
        assertEquals("1:00:00", PlayerGesturePolicy.formatTime(3_600_000L))
        assertEquals("1:01:01", PlayerGesturePolicy.formatTime(3_661_000L))
    }

    @Test
    fun formatSpeed_rendersTrimmedMultiplier() {
        assertEquals("0.25×", PlayerGesturePolicy.formatSpeed(0.25f))
        assertEquals("0.5×", PlayerGesturePolicy.formatSpeed(0.5f))
        assertEquals("1.0×", PlayerGesturePolicy.formatSpeed(1f))
        assertEquals("1.25×", PlayerGesturePolicy.formatSpeed(1.25f))
        assertEquals("1.5×", PlayerGesturePolicy.formatSpeed(1.5f))
        assertEquals("3.0×", PlayerGesturePolicy.formatSpeed(3f))
    }

    @Test
    fun speedOptions_areSortedUniqueAndIncludeNormalSpeed() {
        val options = PlayerGesturePolicy.SPEED_OPTIONS
        assertEquals(options.sorted(), options)
        assertEquals(options.size, options.toSet().size)
        assertTrue(1f in options)
        assertTrue(PlayerGesturePolicy.LONG_PRESS_BOOST_SPEED in options)
    }

    @Test
    fun resizeAndOrientationModes_cycleThroughAllStates() {
        assertEquals(PlayerResizeMode.FILL, PlayerResizeMode.FIT.next())
        assertEquals(PlayerResizeMode.ZOOM, PlayerResizeMode.FILL.next())
        assertEquals(PlayerResizeMode.FIT, PlayerResizeMode.ZOOM.next())

        assertEquals(PlayerOrientationMode.LANDSCAPE, PlayerOrientationMode.AUTO.next())
        assertEquals(PlayerOrientationMode.PORTRAIT, PlayerOrientationMode.LANDSCAPE.next())
        assertEquals(PlayerOrientationMode.AUTO, PlayerOrientationMode.PORTRAIT.next())
    }
}
