package io.github.supermonster003.autojs6.plugin.threeemberplayer.theme

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.TonalPalette
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Semantic light/dark colors generated from one opaque source color.
 *
 * The source color is intentionally preserved for the app bar so that following AutoJs6 remains
 * visually recognizable. The remaining roles use Material Color Utilities' HCT tonal palettes:
 * unlike fixed RGB lightening, HCT tones provide predictable perceptual contrast across saturated,
 * pale, and achromatic input colors.
 */
internal data class VideoThemePalette(
    val source: Int,
    val isDark: Boolean,
    val appBar: Int,
    val onAppBar: Int,
    val primary: Int,
    val onPrimary: Int,
    val primaryContainer: Int,
    val onPrimaryContainer: Int,
    val secondary: Int,
    val onSecondary: Int,
    val secondaryContainer: Int,
    val onSecondaryContainer: Int,
    val background: Int,
    val onBackground: Int,
    val surface: Int,
    val surfaceContainerLowest: Int,
    val surfaceContainerLow: Int,
    val surfaceContainer: Int,
    val surfaceContainerHigh: Int,
    val surfaceContainerHighest: Int,
    val onSurface: Int,
    val onSurfaceVariant: Int,
    val outline: Int,
    val outlineVariant: Int,
    val playerControl: Int,
    val onPlayerControl: Int,
    val error: Int,
    val onError: Int,
    val errorContainer: Int,
    val onErrorContainer: Int,
)

internal object VideoThemePaletteGenerator {

    /** AutoJs6's default non-INRT theme color (`ThemeColorManager.defaultThemeColor`). */
    const val AUTOJS6_FALLBACK_SOURCE: Int = -8_531 // #FFDEAD

    // Material Components embeds the upstream Material Color Utilities implementation but marks
    // this API library-group-only. All access stays here so dependency changes have one audit point.
    @SuppressLint("RestrictedApi")
    fun generate(sourceColor: Int, dark: Boolean): VideoThemePalette {
        val source = opaque(sourceColor)
        val sourceHct = Hct.fromInt(source)
        val isAchromatic = sourceHct.chroma < ACHROMATIC_CHROMA_THRESHOLD
        val primaryChroma = if (isAchromatic) {
            0.0
        } else {
            max(sourceHct.chroma, MIN_PRIMARY_CHROMA).coerceAtMost(MAX_PRIMARY_CHROMA)
        }
        val secondaryChroma = if (isAchromatic) {
            0.0
        } else {
            max(MIN_SECONDARY_CHROMA, min(primaryChroma / 3.0, MAX_SECONDARY_CHROMA))
        }
        val neutralChroma = if (isAchromatic) 0.0 else min(sourceHct.chroma / 12.0, MAX_NEUTRAL_CHROMA)
        val neutralVariantChroma = if (isAchromatic) 0.0 else max(
            MIN_NEUTRAL_VARIANT_CHROMA,
            min(sourceHct.chroma / 6.0, MAX_NEUTRAL_VARIANT_CHROMA),
        )

        val primary = TonalPalette.fromHueAndChroma(sourceHct.hue, primaryChroma)
        val secondary = TonalPalette.fromHueAndChroma(sourceHct.hue, secondaryChroma)
        val neutral = TonalPalette.fromHueAndChroma(sourceHct.hue, neutralChroma)
        val neutralVariant = TonalPalette.fromHueAndChroma(sourceHct.hue, neutralVariantChroma)
        // Error remains semantic red instead of inheriting a source hue that could mislead users.
        val error = TonalPalette.fromHueAndChroma(ERROR_HUE, ERROR_CHROMA)

        val appBar = source
        val onAppBar = bestMonochromeForeground(appBar)
        val background = neutral.tone(if (dark) 6 else 98)
        val onSurface = neutral.tone(if (dark) 90 else 10)
        val playerControl = primary.tone(80)

        return VideoThemePalette(
            source = source,
            isDark = dark,
            appBar = appBar,
            onAppBar = onAppBar,
            primary = primary.tone(if (dark) 80 else 40),
            onPrimary = primary.tone(if (dark) 20 else 100),
            primaryContainer = primary.tone(if (dark) 30 else 90),
            onPrimaryContainer = primary.tone(if (dark) 90 else 10),
            secondary = secondary.tone(if (dark) 80 else 40),
            onSecondary = secondary.tone(if (dark) 20 else 100),
            secondaryContainer = secondary.tone(if (dark) 30 else 90),
            onSecondaryContainer = secondary.tone(if (dark) 90 else 10),
            background = background,
            onBackground = onSurface,
            surface = background,
            surfaceContainerLowest = neutral.tone(if (dark) 4 else 100),
            surfaceContainerLow = neutral.tone(if (dark) 10 else 96),
            surfaceContainer = neutral.tone(if (dark) 12 else 94),
            surfaceContainerHigh = neutral.tone(if (dark) 17 else 92),
            surfaceContainerHighest = neutral.tone(if (dark) 22 else 90),
            onSurface = onSurface,
            onSurfaceVariant = neutralVariant.tone(if (dark) 80 else 30),
            outline = neutralVariant.tone(if (dark) 60 else 50),
            outlineVariant = neutralVariant.tone(if (dark) 30 else 80),
            // Playback overlays always sit over black/video, independently of app night mode.
            playerControl = playerControl,
            onPlayerControl = ensureReadable(primary.tone(20), playerControl, MIN_TEXT_CONTRAST),
            error = error.tone(if (dark) 80 else 40),
            onError = error.tone(if (dark) 20 else 100),
            errorContainer = error.tone(if (dark) 30 else 90),
            onErrorContainer = error.tone(if (dark) 90 else 10),
        )
    }

    fun parseOpaqueColor(value: String): Int? {
        val normalized = value.trim().removePrefix("#").removePrefix("0x").removePrefix("0X")
        if (normalized.length != 6 || normalized.any { it.digitToIntOrNull(16) == null }) return null
        return normalized.toLong(16).toInt() or OPAQUE_ALPHA
    }

    fun colorHex(color: Int): String = "#%06X".format(color and RGB_MASK)

    fun opaque(color: Int): Int = color or OPAQUE_ALPHA

    fun withAlpha(color: Int, alpha: Int): Int =
        color and RGB_MASK or (alpha.coerceIn(0, 255) shl 24)

    fun contrastRatio(first: Int, second: Int): Double {
        val firstLuminance = luminance(first)
        val secondLuminance = luminance(second)
        val lighter = max(firstLuminance, secondLuminance)
        val darker = min(firstLuminance, secondLuminance)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun bestMonochromeForeground(background: Int): Int {
        val blackContrast = contrastRatio(OPAQUE_BLACK, background)
        val whiteContrast = contrastRatio(OPAQUE_WHITE, background)
        return if (blackContrast >= whiteContrast) OPAQUE_BLACK else OPAQUE_WHITE
    }

    private fun ensureReadable(foreground: Int, background: Int, minimumContrast: Double): Int =
        foreground.takeIf { contrastRatio(it, background) >= minimumContrast }
            ?: bestMonochromeForeground(background)

    private fun luminance(color: Int): Double {
        fun channel(shift: Int): Double {
            val component = (color shr shift and 0xFF) / 255.0
            return if (component <= 0.04045) {
                component / 12.92
            } else {
                ((component + 0.055) / 1.055).pow(2.4)
            }
        }
        return 0.2126 * channel(16) + 0.7152 * channel(8) + 0.0722 * channel(0)
    }

    private const val ACHROMATIC_CHROMA_THRESHOLD = 4.0
    private const val MIN_PRIMARY_CHROMA = 48.0
    private const val MAX_PRIMARY_CHROMA = 96.0
    private const val MIN_SECONDARY_CHROMA = 16.0
    private const val MAX_SECONDARY_CHROMA = 24.0
    private const val ERROR_HUE = 25.0
    private const val ERROR_CHROMA = 84.0
    private const val MAX_NEUTRAL_CHROMA = 6.0
    private const val MIN_NEUTRAL_VARIANT_CHROMA = 8.0
    private const val MAX_NEUTRAL_VARIANT_CHROMA = 12.0
    private const val MIN_TEXT_CONTRAST = 4.5
    private const val RGB_MASK = 0x00FFFFFF
    private const val OPAQUE_ALPHA = -0x1000000
    private const val OPAQUE_BLACK = -0x1000000
    private const val OPAQUE_WHITE = -0x1
}
