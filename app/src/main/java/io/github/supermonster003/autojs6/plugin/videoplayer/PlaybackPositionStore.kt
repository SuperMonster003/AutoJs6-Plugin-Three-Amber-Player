package io.github.supermonster003.autojs6.plugin.videoplayer

import android.content.Context

/** SharedPreferences-backed store; all rules live in [PlaybackPositionMemory]. */
internal class PlaybackPositionStore(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun read(uri: String): RememberedPosition? =
        entries()[PlaybackPositionMemory.contentKey(uri)]

    fun save(uri: String, positionMs: Long, durationMs: Long) {
        val updated = PlaybackPositionMemory.updated(
            entries(),
            PlaybackPositionMemory.contentKey(uri),
            positionMs,
            durationMs,
            System.currentTimeMillis(),
        )
        write(updated)
    }

    fun clear(uri: String) {
        val next = entries().toMutableMap()
        if (next.remove(PlaybackPositionMemory.contentKey(uri)) != null) {
            write(next)
        }
    }

    private fun entries(): Map<String, RememberedPosition> =
        PlaybackPositionMemory.decode(preferences.getString(KEY_ENTRIES, null))

    private fun write(entries: Map<String, RememberedPosition>) {
        preferences.edit()
            .putString(KEY_ENTRIES, PlaybackPositionMemory.encode(entries))
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "playback_positions"
        const val KEY_ENTRIES = "entries"
    }
}
