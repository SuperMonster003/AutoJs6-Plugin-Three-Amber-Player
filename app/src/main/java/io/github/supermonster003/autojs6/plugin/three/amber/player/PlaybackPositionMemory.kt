package io.github.supermonster003.autojs6.plugin.three.amber.player

import java.security.MessageDigest

internal data class RememberedPosition(
    val targetKey: String,
    val positionMs: Long,
    val durationMs: Long,
    val savedAtMs: Long,
)

/** Pure codec and retention rules for exactly one most-recent unfinished video. */
internal object PlaybackPositionMemory {

    const val MIN_POSITION_MS = 5_000L
    const val END_MARGIN_MS = 5_000L

    private const val COMPLETED_FRACTION = 0.95
    private const val ENCODING_VERSION = "2"
    private const val FIELD_SEPARATOR = '|'
    private const val HEX_DIGITS = "0123456789abcdef"

    /** Only a digest of the content identity is persisted, never a URI or host path. */
    fun contentKey(identity: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(identity.toByteArray(Charsets.UTF_8))
        return buildString(digest.size * 2) {
            digest.forEach { byte ->
                val value = byte.toInt() and 0xFF
                append(HEX_DIGITS[value ushr 4])
                append(HEX_DIGITS[value and 0x0F])
            }
        }
    }

    fun shouldRemember(positionMs: Long, durationMs: Long): Boolean =
        durationMs > 0L &&
            positionMs >= MIN_POSITION_MS &&
            positionMs <= durationMs &&
            durationMs - positionMs >= END_MARGIN_MS &&
            !isCompleted(positionMs, durationMs)

    fun isCompleted(positionMs: Long, durationMs: Long): Boolean =
        durationMs > 0L && positionMs >= (durationMs * COMPLETED_FRACTION).toLong()

    /** Opening a different target invalidates the previous target immediately. */
    fun resumePosition(record: RememberedPosition?, targetKey: String): Long? =
        record
            ?.takeIf { it.targetKey == targetKey }
            ?.takeIf { shouldRemember(it.positionMs, it.durationMs) }
            ?.positionMs

    fun updated(
        targetKey: String,
        positionMs: Long,
        durationMs: Long,
        nowMs: Long,
    ): RememberedPosition? = if (shouldRemember(positionMs, durationMs)) {
        RememberedPosition(targetKey, positionMs, durationMs, nowMs.coerceAtLeast(0L))
    } else {
        null
    }

    fun encode(record: RememberedPosition): String = listOf(
        ENCODING_VERSION,
        record.targetKey,
        record.positionMs,
        record.durationMs,
        record.savedAtMs,
    ).joinToString(FIELD_SEPARATOR.toString())

    fun decode(value: String?): RememberedPosition? {
        val fields = value?.split(FIELD_SEPARATOR) ?: return null
        if (fields.size != 5 || fields[0] != ENCODING_VERSION) return null
        val targetKey = fields[1].takeIf(::isTargetKey) ?: return null
        val positionMs = fields[2].toLongOrNull()?.takeIf { it >= 0L } ?: return null
        val durationMs = fields[3].toLongOrNull()?.takeIf { it > 0L } ?: return null
        val savedAtMs = fields[4].toLongOrNull()?.takeIf { it >= 0L } ?: return null
        return RememberedPosition(targetKey, positionMs, durationMs, savedAtMs)
            .takeIf { shouldRemember(it.positionMs, it.durationMs) }
    }

    private fun isTargetKey(value: String): Boolean =
        value.length == 64 && value.all { it in HEX_DIGITS }
}
