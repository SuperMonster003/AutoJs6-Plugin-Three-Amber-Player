package io.github.supermonster003.autojs6.plugin.threeemberplayer

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

    val rememberPlaybackMode: Boolean
        get() = preferences.getBoolean(KEY_REMEMBER_PLAYBACK_MODE, false)

    fun readPlaybackMode(): VideoPlaybackMode = VideoPlaybackModePolicy.restoreRemembered(
        enabled = rememberPlaybackMode,
        storedValue = preferences.getString(KEY_PLAYBACK_MODE, null),
    )

    fun writePlaybackMode(mode: VideoPlaybackMode) {
        if (!rememberPlaybackMode) return
        preferences.edit().putString(KEY_PLAYBACK_MODE, mode.name).apply()
    }

    fun setRememberPlaybackMode(enabled: Boolean) {
        preferences.edit().apply {
            putBoolean(KEY_REMEMBER_PLAYBACK_MODE, enabled)
            if (enabled) {
                if (!preferences.contains(KEY_PLAYBACK_MODE)) {
                    putString(KEY_PLAYBACK_MODE, VideoPlaybackMode.SEQUENCE.name)
                }
            } else {
                remove(KEY_PLAYBACK_MODE)
            }
        }.apply()
    }

    fun readSubtitleStyle(): SubtitleStyleSettings = SubtitleStyleSettings(
        textScale = SubtitleStylePolicy.enumOrDefault(
            preferences.getString(KEY_SUBTITLE_TEXT_SCALE, null),
            SubtitleTextScale.PERCENT_100,
        ),
        foregroundColor = SubtitleStylePolicy.enumOrDefault(
            preferences.getString(KEY_SUBTITLE_FOREGROUND, null),
            SubtitleForegroundColor.WHITE,
        ),
        backgroundStyle = SubtitleStylePolicy.enumOrDefault(
            preferences.getString(KEY_SUBTITLE_BACKGROUND, null),
            SubtitleBackgroundStyle.OPAQUE,
        ),
        bottomMargin = SubtitleStylePolicy.enumOrDefault(
            preferences.getString(KEY_SUBTITLE_BOTTOM_MARGIN, null),
            SubtitleBottomMargin.PERCENT_8,
        ),
        customized = preferences.getBoolean(KEY_SUBTITLE_CUSTOMIZED, false),
    )

    fun writeSubtitleStyle(settings: SubtitleStyleSettings) {
        preferences.edit()
            .putString(KEY_SUBTITLE_TEXT_SCALE, settings.textScale.name)
            .putString(KEY_SUBTITLE_FOREGROUND, settings.foregroundColor.name)
            .putString(KEY_SUBTITLE_BACKGROUND, settings.backgroundStyle.name)
            .putString(KEY_SUBTITLE_BOTTOM_MARGIN, settings.bottomMargin.name)
            .putBoolean(KEY_SUBTITLE_CUSTOMIZED, true)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "player_settings"
        const val KEY_GESTURE_SENSITIVITY = "gesture_sensitivity"
        const val KEY_DOUBLE_TAP_SEEK_MS = "double_tap_seek_ms"
        const val KEY_REMEMBER_PLAYBACK_MODE = "remember_playback_mode"
        const val KEY_PLAYBACK_MODE = "playback_mode"
        const val KEY_SUBTITLE_TEXT_SCALE = "subtitle_text_scale"
        const val KEY_SUBTITLE_FOREGROUND = "subtitle_foreground"
        const val KEY_SUBTITLE_BACKGROUND = "subtitle_background"
        const val KEY_SUBTITLE_BOTTOM_MARGIN = "subtitle_bottom_margin"
        const val KEY_SUBTITLE_CUSTOMIZED = "subtitle_customized"
    }
}
