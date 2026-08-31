package io.github.supermonster003.autojs6.plugin.threeemberplayer

import java.util.Locale

internal data class ValidatedSubtitleDocument(
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

/** Pure validation for the single, temporary document-picker subtitle entry point. */
internal object SubtitleDocumentPolicy {
    const val MAX_SUBTITLE_BYTES = 4L * 1024L * 1024L

    fun validate(displayName: String?, declaredSize: Long): ValidatedSubtitleDocument? {
        val safeName = displayName?.trim()?.takeIf {
            it.isNotEmpty() && it.length <= VideoRequestPolicy.MAX_DISPLAY_NAME_LENGTH &&
                '/' !in it && '\\' !in it && it.none(Char::isISOControl)
        } ?: return null
        val mimeType = when (safeName.substringAfterLast('.', "").lowercase(Locale.ROOT)) {
            "srt" -> "application/x-subrip"
            "ass" -> "text/x-ssa"
            else -> return null
        }
        if (declaredSize < 0L || declaredSize > MAX_SUBTITLE_BYTES) return null
        return ValidatedSubtitleDocument(safeName, declaredSize, mimeType)
    }
}
