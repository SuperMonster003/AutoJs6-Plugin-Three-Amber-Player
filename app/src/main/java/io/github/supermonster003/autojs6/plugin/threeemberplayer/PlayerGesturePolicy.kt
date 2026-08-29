package io.github.supermonster003.autojs6.plugin.threeemberplayer

import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

internal enum class VerticalDragZone { BRIGHTNESS, VOLUME }

internal enum class DoubleTapZone { REWIND, TOGGLE_PLAYBACK, FORWARD }

internal enum class GestureSensitivity(val multiplier: Float) {
    LOW(0.65f),
    NORMAL(1f),
    HIGH(1.4f),
}

internal enum class PlayerResizeMode {
    FIT, FILL, ZOOM;

    fun next(): PlayerResizeMode = entries[(ordinal + 1) % entries.size]
}

internal enum class PlayerOrientationMode {
    AUTO, LANDSCAPE, PORTRAIT;

    fun next(): PlayerOrientationMode = entries[(ordinal + 1) % entries.size]
}

/** Pure gesture and control math, kept Android-free so it is locally testable. */
internal object PlayerGesturePolicy {

    const val DOUBLE_TAP_SEEK_MS = 10_000L
    const val LONG_PRESS_BOOST_SPEED = 2f
    const val CONTROLS_AUTO_HIDE_MS = 3_500L
    const val MIN_BRIGHTNESS = 0.01f
    const val MIN_ZOOM_SCALE = 0.25f
    const val MAX_ZOOM_SCALE = 4f
    const val DEFAULT_ZOOM_SCALE = 1f

    val SPEED_OPTIONS = listOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 3f)
    val DOUBLE_TAP_SEEK_OPTIONS_MS = listOf(5_000L, DOUBLE_TAP_SEEK_MS, 30_000L)

    private const val SEEK_WINDOW_MS_MAX = 90_000L

    fun verticalDragZone(startX: Float, viewWidth: Int): VerticalDragZone =
        if (startX < viewWidth / 2f) VerticalDragZone.BRIGHTNESS else VerticalDragZone.VOLUME

    fun doubleTapZone(x: Float, viewWidth: Int): DoubleTapZone = when {
        x < viewWidth / 3f -> DoubleTapZone.REWIND
        x < viewWidth * 2f / 3f -> DoubleTapZone.TOGGLE_PLAYBACK
        else -> DoubleTapZone.FORWARD
    }

    /** A drag across the full view width moves at most [SEEK_WINDOW_MS_MAX], less for shorter media. */
    fun seekDragTarget(
        startPositionMs: Long,
        dragPx: Float,
        viewWidthPx: Int,
        durationMs: Long,
        sensitivity: GestureSensitivity = GestureSensitivity.NORMAL,
    ): Long {
        if (durationMs <= 0L || viewWidthPx <= 0) return startPositionMs.coerceAtLeast(0L)
        val windowMs = durationMs.coerceAtMost(SEEK_WINDOW_MS_MAX)
        val deltaMs = (dragPx / viewWidthPx * windowMs * sensitivity.multiplier).roundToLong()
        return (startPositionMs + deltaMs).coerceIn(0L, durationMs)
    }

    /** A drag across the full view height moves brightness or volume across its whole range. */
    fun draggedFraction(
        startFraction: Float,
        dragUpPx: Float,
        viewHeightPx: Int,
        sensitivity: GestureSensitivity = GestureSensitivity.NORMAL,
    ): Float {
        if (viewHeightPx <= 0) return startFraction.coerceIn(0f, 1f)
        return (startFraction + dragUpPx / viewHeightPx * sensitivity.multiplier).coerceIn(0f, 1f)
    }

    /** Applies sensitivity symmetrically to pinch-in and pinch-out deltas before clamping. */
    fun zoomScale(
        currentScale: Float,
        detectorScaleFactor: Float,
        sensitivity: GestureSensitivity = GestureSensitivity.NORMAL,
    ): Float {
        if (!currentScale.isFinite() || !detectorScaleFactor.isFinite() || detectorScaleFactor <= 0f) {
            return currentScale.takeIf(Float::isFinite)
                ?.coerceIn(MIN_ZOOM_SCALE, MAX_ZOOM_SCALE)
                ?: DEFAULT_ZOOM_SCALE
        }
        val adjustedFactor = detectorScaleFactor.toDouble()
            .pow(sensitivity.multiplier.toDouble())
            .toFloat()
        return (currentScale * adjustedFactor).coerceIn(MIN_ZOOM_SCALE, MAX_ZOOM_SCALE)
    }

    fun normalizedDoubleTapSeekMs(value: Long): Long =
        value.takeIf(DOUBLE_TAP_SEEK_OPTIONS_MS::contains) ?: DOUBLE_TAP_SEEK_MS

    fun percentLabel(fraction: Float): String =
        "${(fraction.coerceIn(0f, 1f) * 100).roundToInt()}%"

    fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs.coerceAtLeast(0L) / 1_000L
        val hours = totalSeconds / 3_600L
        val minutes = totalSeconds % 3_600L / 60L
        val seconds = totalSeconds % 60L
        return if (hours > 0L) {
            String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
        }
    }

    fun formatSpeed(speed: Float): String {
        val centi = (speed * 100).roundToInt()
        val whole = centi / 100
        val remainder = centi % 100
        return when {
            remainder == 0 -> "$whole.0×"
            remainder % 10 == 0 -> "$whole.${remainder / 10}×"
            else -> "$whole.${String.format(Locale.ROOT, "%02d", remainder)}×"
        }
    }
}
