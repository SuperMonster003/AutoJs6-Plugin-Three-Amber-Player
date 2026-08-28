package io.github.supermonster003.autojs6.plugin.videoplayer

import android.content.Context

internal data class PlayerGestureSettings(
    val sensitivity: GestureSensitivity,
    val doubleTapSeekMs: Long,
)

internal class PlayerSettingsStore(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun read(): PlayerGestureSettings = PlayerGestureSettings(
        sensitivity = runCatching {
            GestureSensitivity.valueOf(
                preferences.getString(KEY_GESTURE_SENSITIVITY, null).orEmpty(),
            )
        }.getOrDefault(GestureSensitivity.NORMAL),
        doubleTapSeekMs = PlayerGesturePolicy.normalizedDoubleTapSeekMs(
            preferences.getLong(KEY_DOUBLE_TAP_SEEK_MS, PlayerGesturePolicy.DOUBLE_TAP_SEEK_MS),
        ),
    )

    fun writeSensitivity(sensitivity: GestureSensitivity) {
        preferences.edit().putString(KEY_GESTURE_SENSITIVITY, sensitivity.name).apply()
    }

    fun writeDoubleTapSeekMs(value: Long) {
        preferences.edit()
            .putLong(KEY_DOUBLE_TAP_SEEK_MS, PlayerGesturePolicy.normalizedDoubleTapSeekMs(value))
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "player_settings"
        const val KEY_GESTURE_SENSITIVITY = "gesture_sensitivity"
        const val KEY_DOUBLE_TAP_SEEK_MS = "double_tap_seek_ms"
    }
}
