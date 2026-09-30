package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.content.Context
import androidx.core.content.edit

/**
 * Stores exactly one recent video. Looking up a different video removes the previous record even
 * if the new video is closed before it reaches a persistable position.
 */
internal class PlaybackPositionStore(context: Context) {

    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        // Versions through 1.4.0 retained up to 200 entries in this legacy preference file. Those
        // records cannot satisfy the single-recent-video contract and intentionally do not migrate.
        appContext.getSharedPreferences(LEGACY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .takeIf { it.all.isNotEmpty() }
            ?.edit { clear() }
    }

    fun resumePosition(identity: String): Long? {
        val targetKey = PlaybackPositionMemory.contentKey(identity)
        val raw = preferences.getString(KEY_RECENT_POSITION, null)
        val record = PlaybackPositionMemory.decode(raw)
        val position = PlaybackPositionMemory.resumePosition(record, targetKey)
        if (position == null && raw != null) preferences.edit { remove(KEY_RECENT_POSITION) }
        return position
    }

    fun save(identity: String, positionMs: Long, durationMs: Long) {
        val record = PlaybackPositionMemory.updated(
            targetKey = PlaybackPositionMemory.contentKey(identity),
            positionMs = positionMs,
            durationMs = durationMs,
            nowMs = System.currentTimeMillis(),
        )
        preferences.edit {
            if (record == null) remove(KEY_RECENT_POSITION)
            else putString(KEY_RECENT_POSITION, PlaybackPositionMemory.encode(record))
        }
    }

    fun clear(identity: String) {
        val targetKey = PlaybackPositionMemory.contentKey(identity)
        val record = PlaybackPositionMemory.decode(preferences.getString(KEY_RECENT_POSITION, null))
        if (record?.targetKey == targetKey) preferences.edit { remove(KEY_RECENT_POSITION) }
    }

    fun clearAll() = preferences.edit { clear() }

    private companion object {
        const val PREFERENCES_NAME = "recent_playback_position"
        const val KEY_RECENT_POSITION = "recent"
        const val LEGACY_PREFERENCES_NAME = "playback_positions"
    }
}
