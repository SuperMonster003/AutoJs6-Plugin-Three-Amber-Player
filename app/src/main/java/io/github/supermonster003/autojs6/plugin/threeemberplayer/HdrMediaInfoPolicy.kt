package io.github.supermonster003.autojs6.plugin.threeemberplayer

internal enum class MediaHdrType { NONE, HDR10, HLG, UNKNOWN }

internal enum class MediaColorSpace { BT601, BT709, BT2020, UNKNOWN }

internal enum class MediaColorRange { LIMITED, FULL, UNKNOWN }

internal enum class MediaColorTransfer { SDR, ST2084, HLG, OTHER, UNKNOWN }

internal data class MediaColorMetadata(
    val colorSpace: MediaColorSpace,
    val colorRange: MediaColorRange,
    val colorTransfer: MediaColorTransfer,
    val lumaBitDepth: Int?,
    val chromaBitDepth: Int?,
)

internal data class MediaColorSummary(
    val hdrType: MediaHdrType,
    val colorSpace: MediaColorSpace,
    val colorRange: MediaColorRange,
    val lumaBitDepth: Int?,
    val chromaBitDepth: Int?,
)

/** Android-free HDR classification and display-capability policy. */
internal object HdrMediaInfoPolicy {

    fun summarize(metadata: MediaColorMetadata?): MediaColorSummary = MediaColorSummary(
        hdrType = when (metadata?.colorTransfer) {
            MediaColorTransfer.ST2084 -> MediaHdrType.HDR10
            MediaColorTransfer.HLG -> MediaHdrType.HLG
            MediaColorTransfer.SDR -> MediaHdrType.NONE
            MediaColorTransfer.OTHER,
            MediaColorTransfer.UNKNOWN,
            null,
            -> MediaHdrType.UNKNOWN
        },
        colorSpace = metadata?.colorSpace ?: MediaColorSpace.UNKNOWN,
        colorRange = metadata?.colorRange ?: MediaColorRange.UNKNOWN,
        lumaBitDepth = normalizedBitDepth(metadata?.lumaBitDepth),
        chromaBitDepth = normalizedBitDepth(metadata?.chromaBitDepth),
    )

    fun requiresToneMappingWarning(
        hdrType: MediaHdrType,
        supportsHdr10: Boolean,
        supportsHlg: Boolean,
    ): Boolean = when (hdrType) {
        MediaHdrType.HDR10 -> !supportsHdr10
        MediaHdrType.HLG -> !supportsHlg
        MediaHdrType.NONE,
        MediaHdrType.UNKNOWN,
        -> false
    }

    fun formatBitDepth(
        summary: MediaColorSummary,
        unknownValue: String,
        singleValue: (Int) -> String,
        pairValue: (luma: Int, chroma: Int) -> String,
    ): String {
        val luma = summary.lumaBitDepth
        val chroma = summary.chromaBitDepth
        return when {
            luma == null && chroma == null -> unknownValue
            luma != null && (chroma == null || chroma == luma) -> singleValue(luma)
            luma == null && chroma != null -> singleValue(chroma)
            else -> pairValue(requireNotNull(luma), requireNotNull(chroma))
        }
    }

    private fun normalizedBitDepth(value: Int?): Int? = value?.takeIf { it in 1..32 }
}
