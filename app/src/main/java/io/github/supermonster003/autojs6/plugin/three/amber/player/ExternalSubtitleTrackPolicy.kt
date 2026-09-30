package io.github.supermonster003.autojs6.plugin.three.amber.player

internal const val EXTERNAL_SUBTITLE_ID_PREFIX = "threeamber:external:"

internal data class ExternalSubtitleTrackIdentity(
    val stableId: String,
    val displayName: String?,
)

/**
 * Media3's parse-during-extraction path can discard SubtitleConfiguration.id while retaining its
 * label. Keep a reversible marker in both fields so external tracks remain identifiable on every
 * supported Media3 path without exposing that marker in the plugin's own track picker.
 */
internal object ExternalSubtitleTrackPolicy {

    private const val LABEL_SEPARATOR = '|'

    fun encodedLabel(stableId: String, displayName: String): String {
        require(stableId.startsWith(EXTERNAL_SUBTITLE_ID_PREFIX))
        return "$stableId$LABEL_SEPARATOR$displayName"
    }

    fun identify(formatId: String?, formatLabel: String?): ExternalSubtitleTrackIdentity? {
        val labelIdentity = decodeLabel(formatLabel)
        val stableFormatId = formatId?.takeIf { it.startsWith(EXTERNAL_SUBTITLE_ID_PREFIX) }
        if (stableFormatId != null) {
            return ExternalSubtitleTrackIdentity(
                stableId = stableFormatId,
                displayName = labelIdentity
                    ?.takeIf { it.stableId == stableFormatId }
                    ?.displayName
                    ?: formatLabel?.takeUnless { it.startsWith(EXTERNAL_SUBTITLE_ID_PREFIX) },
            )
        }
        return labelIdentity
    }

    private fun decodeLabel(label: String?): ExternalSubtitleTrackIdentity? {
        val value = label?.takeIf { it.startsWith(EXTERNAL_SUBTITLE_ID_PREFIX) } ?: return null
        val separatorIndex = value.indexOf(LABEL_SEPARATOR)
        val stableId = if (separatorIndex >= 0) value.substring(0, separatorIndex) else value
        val displayName = if (separatorIndex >= 0) {
            value.substring(separatorIndex + 1).takeIf(String::isNotEmpty)
        } else {
            null
        }
        return ExternalSubtitleTrackIdentity(stableId, displayName)
    }
}
