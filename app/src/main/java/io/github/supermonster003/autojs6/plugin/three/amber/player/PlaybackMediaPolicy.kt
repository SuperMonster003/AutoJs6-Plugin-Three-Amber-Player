package io.github.supermonster003.autojs6.plugin.three.amber.player

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

internal enum class VideoOrientationSuggestion { NONE, LANDSCAPE, PORTRAIT }

internal data class PipAspectRatio(
    val numerator: Int,
    val denominator: Int,
) {
    init {
        require(numerator > 0 && denominator > 0)
    }

    val value: Double
        get() = numerator.toDouble() / denominator
}

/** Pure media-metadata and display policy, kept Android-free for JVM verification. */
internal object PlaybackMediaPolicy {

    // Android's platform PiP validation accepts ratios in [1 / 2.39, 2.39].
    const val PIP_MAX_ASPECT_RATIO = 2.39
    const val PIP_MIN_ASPECT_RATIO = 1.0 / PIP_MAX_ASPECT_RATIO

    private const val SQUARE_TOLERANCE = 0.05
    private const val RATIO_SCALE = 1_000

    fun suggestedOrientation(
        width: Int,
        height: Int,
        rotationDegrees: Int = 0,
        pixelWidthHeightRatio: Float = 1f,
    ): VideoOrientationSuggestion {
        val ratio = displayAspectRatio(width, height, rotationDegrees, pixelWidthHeightRatio)
            ?: return VideoOrientationSuggestion.NONE
        return when {
            ratio > 1.0 + SQUARE_TOLERANCE -> VideoOrientationSuggestion.LANDSCAPE
            ratio < 1.0 - SQUARE_TOLERANCE -> VideoOrientationSuggestion.PORTRAIT
            else -> VideoOrientationSuggestion.NONE
        }
    }

    fun pipAspectRatio(
        width: Int,
        height: Int,
        rotationDegrees: Int = 0,
        pixelWidthHeightRatio: Float = 1f,
    ): PipAspectRatio? {
        val ratio = displayAspectRatio(width, height, rotationDegrees, pixelWidthHeightRatio)
            ?.coerceIn(PIP_MIN_ASPECT_RATIO, PIP_MAX_ASPECT_RATIO)
            ?: return null
        val numerator = (ratio * RATIO_SCALE).roundToInt().coerceAtLeast(1)
        val divisor = greatestCommonDivisor(numerator, RATIO_SCALE)
        return PipAspectRatio(numerator / divisor, RATIO_SCALE / divisor)
    }

    fun formatFrameRate(frameRate: Float): String? {
        if (!frameRate.isFinite() || frameRate <= 0f) return null
        val rounded = String.format(Locale.ROOT, "%.2f", frameRate).trimEnd('0').trimEnd('.')
        return rounded.takeIf(String::isNotEmpty)
    }

    fun formatCodec(sampleMimeType: String?, codecs: String?): String? {
        val mime = sampleMimeType?.trim()?.takeIf(String::isNotEmpty)
        val codec = codecs?.trim()?.takeIf(String::isNotEmpty)
        return when {
            mime == null -> codec
            codec == null || codec.equals(mime, ignoreCase = true) -> mime
            else -> "$codec ($mime)"
        }
    }

    private fun displayAspectRatio(
        width: Int,
        height: Int,
        rotationDegrees: Int,
        pixelWidthHeightRatio: Float,
    ): Double? {
        if (width <= 0 || height <= 0) return null
        val pixelRatio = pixelWidthHeightRatio.takeIf { it.isFinite() && it > 0f } ?: 1f
        val rotated = normalizedRotation(rotationDegrees) == 90 || normalizedRotation(rotationDegrees) == 270
        val displayWidth = if (rotated) height.toDouble() else width * pixelRatio.toDouble()
        val displayHeight = if (rotated) width * pixelRatio.toDouble() else height.toDouble()
        return (displayWidth / displayHeight).takeIf { it.isFinite() && it > 0.0 }
    }

    private fun normalizedRotation(rotationDegrees: Int): Int =
        ((rotationDegrees % 360) + 360) % 360

    private fun greatestCommonDivisor(first: Int, second: Int): Int {
        var a = abs(first)
        var b = abs(second)
        while (b != 0) {
            val remainder = a % b
            a = b
            b = remainder
        }
        return a.coerceAtLeast(1)
    }
}
