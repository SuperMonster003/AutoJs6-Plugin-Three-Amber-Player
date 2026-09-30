package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Test

class SubtitleStylePolicyTest {

    @Test
    fun defaults_matchMedia3CurrentVisualBaseline() {
        val spec = SubtitleStylePolicy.resolve(SubtitleStyleSettings())

        assertEquals(0.0533f, spec.fractionalTextSize, 0.00001f)
        assertEquals(0xFFFFFFFF.toInt(), spec.foregroundArgb)
        assertEquals(0xFF000000.toInt(), spec.backgroundArgb)
        assertEquals(0.08f, spec.bottomPaddingFraction, 0.00001f)
    }

    @Test
    fun everyChoice_mapsToBoundedPrimitiveValues() {
        SubtitleTextScale.entries.forEach { textScale ->
            SubtitleForegroundColor.entries.forEach { foreground ->
                SubtitleBackgroundStyle.entries.forEach { background ->
                    SubtitleBottomMargin.entries.forEach { margin ->
                        val spec = SubtitleStylePolicy.resolve(
                            SubtitleStyleSettings(textScale, foreground, background, margin),
                        )
                        assertEquals(0.0533f * textScale.multiplier, spec.fractionalTextSize, 0.00001f)
                        assertEquals(foreground.argb, spec.foregroundArgb)
                        assertEquals(background.argb, spec.backgroundArgb)
                        assertEquals(margin.fraction, spec.bottomPaddingFraction, 0.00001f)
                    }
                }
            }
        }
    }

    @Test
    fun invalidPersistedEnum_fallsBackWithoutThrowing() {
        assertEquals(
            SubtitleTextScale.PERCENT_100,
            SubtitleStylePolicy.enumOrDefault("future-value", SubtitleTextScale.PERCENT_100),
        )
        assertEquals(
            SubtitleTextScale.PERCENT_125,
            SubtitleStylePolicy.enumOrDefault("PERCENT_125", SubtitleTextScale.PERCENT_100),
        )
    }
}
