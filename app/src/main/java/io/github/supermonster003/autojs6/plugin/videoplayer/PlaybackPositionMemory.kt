package io.github.supermonster003.autojs6.plugin.videoplayer

import java.security.MessageDigest

internal data class RememberedPosition(
    val positionMs: Long,
    val durationMs: Long,
    val savedAtMs: Long,
)

/** Pure resume-position rules and codec, kept Android-free so it is locally testable. */
internal object PlaybackPositionMemory {

    const val MAX_ENTRIES = 200
    private const val MIN_DURATION_MS = 30_000L
    private const val MIN_POSITION_MS = 5_000L
    private const val COMPLETED_FRACTION = 0.95
    private const val FIELD_SEPARATOR = "|"
    private const val ENTRY_SEPARATOR = "\n"

    /** Only a digest of the content URI is ever persisted, never the URI itself. */
    fun contentKey(uri: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(uri.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }

    fun shouldRemember(positionMs: Long, durationMs: Long): Boolean =
        durationMs >= MIN_DURATION_MS &&
            positionMs >= MIN_POSITION_MS &&
            !isCompleted(positionMs, durationMs)

    fun isCompleted(positionMs: Long, durationMs: Long): Boolean =
        durationMs > 0L && positionMs >= (durationMs * COMPLETED_FRACTION).toLong()

    /**
     * Completed playback clears the bookmark; positions too early to remember leave any
     * existing bookmark untouched, so a brief re-open never destroys real progress.
     */
    fun updated(
        entries: Map<String, RememberedPosition>,
        key: String,
        positionMs: Long,
        durationMs: Long,
        nowMs: Long,
    ): Map<String, RememberedPosition> {
        val next = entries.toMutableMap()
        when {
            shouldRemember(positionMs, durationMs) ->
                next[key] = RememberedPosition(positionMs, durationMs, nowMs)
            isCompleted(positionMs, durationMs) -> next.remove(key)
            else -> Unit
        }
        return prune(next)
    }

    fun prune(entries: Map<String, RememberedPosition>): Map<String, RememberedPosition> {
        if (entries.size <= MAX_ENTRIES) return entries
        return entries.entries
            .sortedByDescending { entry -> entry.value.savedAtMs }
            .take(MAX_ENTRIES)
            .associate { entry -> entry.key to entry.value }
    }

    fun encode(entries: Map<String, RememberedPosition>): String =
        entries.entries.joinToString(ENTRY_SEPARATOR) { (key, value) ->
            listOf(key, value.positionMs, value.durationMs, value.savedAtMs)
                .joinToString(FIELD_SEPARATOR)
        }

    fun decode(value: String?): Map<String, RememberedPosition> {
        val raw = value?.takeIf(String::isNotBlank) ?: return emptyMap()
        return raw.split(ENTRY_SEPARATOR)
            .mapNotNull(::decodeEntry)
            .toMap()
    }

    private fun decodeEntry(line: String): Pair<String, RememberedPosition>? {
        val fields = line.split(FIELD_SEPARATOR)
        if (fields.size != 4) return null
        val key = fields[0].takeIf(String::isNotBlank) ?: return null
        val positionMs = fields[1].toLongOrNull()?.takeIf { it >= 0L } ?: return null
        val durationMs = fields[2].toLongOrNull()?.takeIf { it > 0L } ?: return null
        val savedAtMs = fields[3].toLongOrNull()?.takeIf { it >= 0L } ?: return null
        return key to RememberedPosition(positionMs, durationMs, savedAtMs)
    }
}
