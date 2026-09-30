package io.github.supermonster003.autojs6.plugin.three.amber.player

internal enum class VolumeBoostLevel(val decibels: Int) {
    OFF(0),
    DB_3(3),
    DB_6(6),
    DB_9(9),
    DB_12(12),
    DB_15(15),
}

/** Bounded session-only LoudnessEnhancer gain mapping. */
internal object VolumeBoostPolicy {

    const val MAX_GAIN_MILLIBELS = 1_500

    fun targetGainMillibels(level: VolumeBoostLevel): Int =
        (level.decibels * 100).coerceIn(0, MAX_GAIN_MILLIBELS)

    fun fromStoredOrdinal(ordinal: Int): VolumeBoostLevel =
        VolumeBoostLevel.entries.getOrElse(ordinal) { VolumeBoostLevel.OFF }
}
