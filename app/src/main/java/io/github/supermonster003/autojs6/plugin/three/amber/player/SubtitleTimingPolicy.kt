package io.github.supermonster003.autojs6.plugin.three.amber.player

import java.util.Locale
import kotlin.math.roundToLong

/** Android-free subtitle offset clamping, formatting and SRT/ASS timestamp shifting. */
internal object SubtitleTimingPolicy {

    const val MIN_OFFSET_MS = -600_000L
    const val MAX_OFFSET_MS = 600_000L
    const val OFFSET_STEP_MS = 100L

    private const val SRT_TIMESTAMP_TOKEN = "\\d{1,3}:[0-5]\\d:[0-5]\\d[,.]\\d{3}"
    private val srtTimestamp = Regex("(\\d{1,3}):([0-5]\\d):([0-5]\\d)([,.])(\\d{3})")
    private val srtTimingLine = Regex(
        "(?m)^([ \\t]*)($SRT_TIMESTAMP_TOKEN)([ \\t]*-->[ \\t]*)($SRT_TIMESTAMP_TOKEN)([^\\r\\n]*)(\\r?)$",
    )
    private val assDialogue = Regex(
        pattern = "^(Dialogue\\s*:\\s*[^,]*,)(\\d{1,2}:[0-5]\\d:[0-5]\\d[.]\\d{1,3}),(\\d{1,2}:[0-5]\\d:[0-5]\\d[.]\\d{1,3})(,.*)$",
        options = setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE),
    )

    fun normalizeOffsetMs(offsetMs: Long): Long {
        val bounded = offsetMs.coerceIn(MIN_OFFSET_MS, MAX_OFFSET_MS)
        return (bounded.toDouble() / OFFSET_STEP_MS).roundToLong() * OFFSET_STEP_MS
    }

    fun formatOffset(offsetMs: Long): String = String.format(
        Locale.ROOT,
        "%+.1fs",
        normalizeOffsetMs(offsetMs) / 1_000.0,
    )

    fun shift(text: String, mimeType: String, offsetMs: Long): String {
        val normalized = normalizeOffsetMs(offsetMs)
        if (normalized == 0L) return text
        return when (mimeType.lowercase(Locale.ROOT)) {
            "application/x-subrip", "application/srt", "text/srt" -> shiftSrt(text, normalized)
            "text/x-ssa", "text/x-ass", "application/x-ass" -> shiftAss(text, normalized)
            else -> text
        }
    }

    private fun shiftSrt(text: String, offsetMs: Long): String = srtTimingLine.replace(text) { match ->
        match.groupValues[1] +
            shiftSrtTimestamp(match.groupValues[2], offsetMs) +
            match.groupValues[3] +
            shiftSrtTimestamp(match.groupValues[4], offsetMs) +
            match.groupValues[5] +
            match.groupValues[6]
    }

    private fun shiftSrtTimestamp(value: String, offsetMs: Long): String {
        val match = requireNotNull(srtTimestamp.matchEntire(value))
        val shifted = (parseSrtTimestamp(match) + offsetMs).coerceAtLeast(0L)
        return formatSrtTimestamp(shifted, match.groupValues[4])
    }

    private fun parseSrtTimestamp(match: MatchResult): Long =
        match.groupValues[1].toLong() * 3_600_000L +
            match.groupValues[2].toLong() * 60_000L +
            match.groupValues[3].toLong() * 1_000L +
            match.groupValues[5].toLong()

    private fun formatSrtTimestamp(valueMs: Long, separator: String): String {
        val hours = valueMs / 3_600_000L
        val minutes = valueMs / 60_000L % 60L
        val seconds = valueMs / 1_000L % 60L
        val millis = valueMs % 1_000L
        return String.format(Locale.ROOT, "%02d:%02d:%02d%s%03d", hours, minutes, seconds, separator, millis)
    }

    private fun shiftAss(text: String, offsetMs: Long): String = assDialogue.replace(text) { match ->
        val start = (parseAssTimestamp(match.groupValues[2]) + offsetMs).coerceAtLeast(0L)
        val end = (parseAssTimestamp(match.groupValues[3]) + offsetMs).coerceAtLeast(0L)
        match.groupValues[1] + formatAssTimestamp(start) + "," + formatAssTimestamp(end) + match.groupValues[4]
    }

    private fun parseAssTimestamp(value: String): Long {
        val fields = value.split(':', limit = 3)
        val seconds = fields[2].split('.', limit = 2)
        val fraction = seconds.getOrElse(1) { "0" }.take(3).padEnd(3, '0').toLong()
        return fields[0].toLong() * 3_600_000L +
            fields[1].toLong() * 60_000L +
            seconds[0].toLong() * 1_000L + fraction
    }

    private fun formatAssTimestamp(valueMs: Long): String {
        val hours = valueMs / 3_600_000L
        val minutes = valueMs / 60_000L % 60L
        val seconds = valueMs / 1_000L % 60L
        val centiseconds = valueMs % 1_000L / 10L
        return String.format(Locale.ROOT, "%d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds)
    }
}
