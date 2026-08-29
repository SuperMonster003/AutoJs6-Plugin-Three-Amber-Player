package io.github.supermonster003.autojs6.plugin.threeemberplayer.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoThemePaletteTest {

    @Test
    fun everyPresetAndExtremeSeedProducesOpaqueReadableRoles() {
        testSeeds.forEach { seed ->
            listOf(false, true).forEach { dark ->
                val palette = VideoThemePaletteGenerator.generate(seed, dark)
                assertEquals(seed or -0x1000000, palette.source)
                assertOpaque(palette)
                assertContrastAtLeast(palette.onAppBar, palette.appBar, 4.5)
                assertContrastAtLeast(palette.onPrimary, palette.primary, 4.5)
                assertContrastAtLeast(palette.onPrimaryContainer, palette.primaryContainer, 4.5)
                assertContrastAtLeast(palette.onSecondary, palette.secondary, 4.5)
                assertContrastAtLeast(palette.onSecondaryContainer, palette.secondaryContainer, 4.5)
                assertContrastAtLeast(palette.onBackground, palette.background, 7.0)
                assertContrastAtLeast(palette.onSurface, palette.surface, 7.0)
                assertContrastAtLeast(palette.onPlayerControl, palette.playerControl, 4.5)
                assertContrastAtLeast(palette.outline, palette.background, 3.0)
                assertContrastAtLeast(palette.onError, palette.error, 4.5)
                assertContrastAtLeast(palette.onErrorContainer, palette.errorContainer, 4.5)
            }
        }
    }

    @Test
    fun sourceIsPreservedWhileSemanticRolesAdaptToMode() {
        val source = 0xFFFFDEAD.toInt()
        val light = VideoThemePaletteGenerator.generate(source, dark = false)
        val dark = VideoThemePaletteGenerator.generate(source, dark = true)

        assertEquals(source, light.appBar)
        assertEquals(source, dark.appBar)
        assertEquals(light.appBar, dark.appBar)
        assertNotEquals(light.background, dark.background)
        assertNotEquals(light.primary, dark.primary)
        assertEquals(light.playerControl, dark.playerControl)
    }

    @Test
    fun customParserAcceptsRgbHexAndRejectsAmbiguousValues() {
        assertEquals(0xFF123ABC.toInt(), VideoThemePaletteGenerator.parseOpaqueColor("#123abc"))
        assertEquals(0xFFFFDEAD.toInt(), VideoThemePaletteGenerator.parseOpaqueColor("0xFFDEAD"))
        assertEquals(0xFF000000.toInt(), VideoThemePaletteGenerator.parseOpaqueColor("000000"))
        assertEquals(null, VideoThemePaletteGenerator.parseOpaqueColor("#123"))
        assertEquals(null, VideoThemePaletteGenerator.parseOpaqueColor("#80123ABC"))
        assertEquals(null, VideoThemePaletteGenerator.parseOpaqueColor("red"))
        assertEquals(null, VideoThemePaletteGenerator.parseOpaqueColor("#GG0000"))
    }

    private fun assertOpaque(palette: VideoThemePalette) {
        palette.javaClass.declaredFields
            .filter { field -> field.type == Int::class.javaPrimitiveType }
            .forEach { field ->
                field.isAccessible = true
                val color = field.getInt(palette)
                assertEquals("${field.name} must be opaque", 0xFF, color ushr 24)
            }
    }

    private fun assertContrastAtLeast(foreground: Int, background: Int, minimum: Double) {
        val actual = VideoThemePaletteGenerator.contrastRatio(foreground, background)
        assertTrue(
            "${VideoThemePaletteGenerator.colorHex(foreground)} on " +
                "${VideoThemePaletteGenerator.colorHex(background)}: $actual < $minimum",
            actual + 0.001 >= minimum,
        )
    }

    private companion object {
        val testSeeds = listOf(
            VideoThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE,
            *ThemePresetCatalog.colors.map(ThemePreset::color).toTypedArray(),
            0xFF000000.toInt(),
            0xFFFFFFFF.toInt(),
            0xFF010203.toInt(),
            0xFFFEFDFC.toInt(),
        )
    }
}
