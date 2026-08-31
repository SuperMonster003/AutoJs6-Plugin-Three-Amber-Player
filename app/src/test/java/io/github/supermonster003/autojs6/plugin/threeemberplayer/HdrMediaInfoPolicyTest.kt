package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HdrMediaInfoPolicyTest {

    @Test
    fun hdrClassification_distinguishesHdr10HlgSdrAndMissingMetadata() {
        assertEquals(MediaHdrType.HDR10, summary(MediaColorTransfer.ST2084).hdrType)
        assertEquals(MediaHdrType.HLG, summary(MediaColorTransfer.HLG).hdrType)
        assertEquals(MediaHdrType.NONE, summary(MediaColorTransfer.SDR).hdrType)
        assertEquals(MediaHdrType.UNKNOWN, HdrMediaInfoPolicy.summarize(null).hdrType)
        assertEquals(MediaHdrType.UNKNOWN, summary(MediaColorTransfer.OTHER).hdrType)
    }

    @Test
    fun colorMetadata_preservesKnownFieldsAndRejectsImpossibleBitDepths() {
        val result = HdrMediaInfoPolicy.summarize(
            MediaColorMetadata(
                colorSpace = MediaColorSpace.BT2020,
                colorRange = MediaColorRange.LIMITED,
                colorTransfer = MediaColorTransfer.ST2084,
                lumaBitDepth = 10,
                chromaBitDepth = 12,
            ),
        )
        assertEquals(MediaColorSpace.BT2020, result.colorSpace)
        assertEquals(MediaColorRange.LIMITED, result.colorRange)
        assertEquals(10, result.lumaBitDepth)
        assertEquals(12, result.chromaBitDepth)

        val invalid = HdrMediaInfoPolicy.summarize(
            MediaColorMetadata(
                MediaColorSpace.UNKNOWN,
                MediaColorRange.UNKNOWN,
                MediaColorTransfer.UNKNOWN,
                0,
                64,
            ),
        )
        assertNull(invalid.lumaBitDepth)
        assertNull(invalid.chromaBitDepth)
    }

    @Test
    fun warning_isOnlyRequiredForKnownUnsupportedHdrTransfer() {
        assertTrue(HdrMediaInfoPolicy.requiresToneMappingWarning(MediaHdrType.HDR10, false, true))
        assertTrue(HdrMediaInfoPolicy.requiresToneMappingWarning(MediaHdrType.HLG, true, false))
        assertFalse(HdrMediaInfoPolicy.requiresToneMappingWarning(MediaHdrType.HDR10, true, false))
        assertFalse(HdrMediaInfoPolicy.requiresToneMappingWarning(MediaHdrType.NONE, false, false))
        assertFalse(HdrMediaInfoPolicy.requiresToneMappingWarning(MediaHdrType.UNKNOWN, false, false))
    }

    @Test
    fun bitDepthFormatting_handlesUnknownSingleEqualAndSplitChannels() {
        assertEquals("unknown", formatDepth(null, null))
        assertEquals("8-bit", formatDepth(8, null))
        assertEquals("10-bit", formatDepth(10, 10))
        assertEquals("12-bit", formatDepth(null, 12))
        assertEquals("10-bit luma / 12-bit chroma", formatDepth(10, 12))
    }

    private fun summary(transfer: MediaColorTransfer): MediaColorSummary =
        HdrMediaInfoPolicy.summarize(
            MediaColorMetadata(
                MediaColorSpace.BT709,
                MediaColorRange.LIMITED,
                transfer,
                8,
                8,
            ),
        )

    private fun formatDepth(luma: Int?, chroma: Int?): String =
        HdrMediaInfoPolicy.formatBitDepth(
            summary = MediaColorSummary(
                MediaHdrType.UNKNOWN,
                MediaColorSpace.UNKNOWN,
                MediaColorRange.UNKNOWN,
                luma,
                chroma,
            ),
            unknownValue = "unknown",
            singleValue = { "$it-bit" },
            pairValue = { lumaBits, chromaBits ->
                "$lumaBits-bit luma / $chromaBits-bit chroma"
            },
        )
}
