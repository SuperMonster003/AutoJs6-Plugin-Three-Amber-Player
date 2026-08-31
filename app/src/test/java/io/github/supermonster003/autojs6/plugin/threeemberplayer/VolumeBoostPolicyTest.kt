package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Test

class VolumeBoostPolicyTest {

    @Test
    fun gainLevels_mapExactlyToBoundedMillibels() {
        val expected = mapOf(
            VolumeBoostLevel.OFF to 0,
            VolumeBoostLevel.DB_3 to 300,
            VolumeBoostLevel.DB_6 to 600,
            VolumeBoostLevel.DB_9 to 900,
            VolumeBoostLevel.DB_12 to 1_200,
            VolumeBoostLevel.DB_15 to VolumeBoostPolicy.MAX_GAIN_MILLIBELS,
        )
        expected.forEach { (level, gain) ->
            assertEquals(gain, VolumeBoostPolicy.targetGainMillibels(level))
        }
    }

    @Test
    fun restoredOrdinal_rejectsUnknownValuesToSafeOff() {
        assertEquals(VolumeBoostLevel.DB_6, VolumeBoostPolicy.fromStoredOrdinal(VolumeBoostLevel.DB_6.ordinal))
        assertEquals(VolumeBoostLevel.OFF, VolumeBoostPolicy.fromStoredOrdinal(-1))
        assertEquals(VolumeBoostLevel.OFF, VolumeBoostPolicy.fromStoredOrdinal(Int.MAX_VALUE))
    }
}
