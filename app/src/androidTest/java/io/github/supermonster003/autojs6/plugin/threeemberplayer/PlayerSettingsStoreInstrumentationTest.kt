package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerSettingsStoreInstrumentationTest {

    @Test
    fun playbackModeMemoryDefaultsOffAndClearsItsStoredChoiceWhenDisabled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val backup = preferences.all.toMap()
        try {
            preferences.edit().clear().commit()
            val store = PlayerSettingsStore(context)

            assertFalse(store.rememberPlaybackMode)
            assertEquals(VideoPlaybackMode.SEQUENCE, store.readPlaybackMode())
            store.writePlaybackMode(VideoPlaybackMode.SHUFFLE)
            assertEquals(VideoPlaybackMode.SEQUENCE, store.readPlaybackMode())

            store.setRememberPlaybackMode(true)
            assertTrue(store.rememberPlaybackMode)
            assertEquals(VideoPlaybackMode.SEQUENCE, store.readPlaybackMode())
            store.writePlaybackMode(VideoPlaybackMode.SHUFFLE)
            assertEquals(VideoPlaybackMode.SHUFFLE, PlayerSettingsStore(context).readPlaybackMode())

            store.setRememberPlaybackMode(false)
            assertFalse(store.rememberPlaybackMode)
            store.setRememberPlaybackMode(true)
            assertEquals(VideoPlaybackMode.SEQUENCE, store.readPlaybackMode())
        } finally {
            restore(preferences, backup)
        }
    }

    private fun restore(preferences: SharedPreferences, values: Map<String, *>) {
        preferences.edit().clear().apply {
            values.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Set<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        putStringSet(key, value as Set<String>)
                    }
                }
            }
        }.commit()
    }

    private companion object {
        const val PREFERENCES_NAME = "player_settings"
    }
}
