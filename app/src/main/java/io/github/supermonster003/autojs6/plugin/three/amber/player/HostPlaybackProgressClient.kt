package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal enum class HostPlaybackHistoryState {
    UNKNOWN,
    ENABLED,
    DISABLED,
    UNSUPPORTED,
}

/** Optional v12 playback-progress client; unsupported calls safely fall back to plugin memory. */
internal class HostPlaybackProgressClient(
    private val session: IExplorerActionHostSession,
) {

    var state: HostPlaybackHistoryState = HostPlaybackHistoryState.UNKNOWN
        private set

    fun resumePosition(targetId: String, relativePath: String): Long? {
        val result = runCatching { session.getPlaybackProgress(targetId, relativePath) }
            .getOrElse {
                state = HostPlaybackHistoryState.UNSUPPORTED
                return null
            }
        val enabled = result.getBoolean(
            ExplorerActionHostSessionKeys.PLAYBACK_HISTORY_ENABLED,
            false,
        )
        state = if (enabled) HostPlaybackHistoryState.ENABLED else HostPlaybackHistoryState.DISABLED
        if (!enabled || !result.getBoolean(ExplorerActionHostSessionKeys.PLAYBACK_PROGRESS_PRESENT, false)) {
            return null
        }
        val position = result.getLong(ExplorerActionHostSessionKeys.PLAYBACK_POSITION_MILLIS, -1L)
        val duration = result.getLong(ExplorerActionHostSessionKeys.PLAYBACK_DURATION_MILLIS, -1L)
        return position.takeIf { it >= 0L && duration > 0L && it <= duration }
    }

    fun report(
        targetId: String,
        relativePath: String,
        positionMillis: Long,
        durationMillis: Long,
        completed: Boolean,
    ): Boolean {
        if (state == HostPlaybackHistoryState.DISABLED) return true
        if (state == HostPlaybackHistoryState.UNSUPPORTED) return false
        return runCatching {
            session.reportPlaybackProgress(
                targetId,
                relativePath,
                positionMillis,
                durationMillis,
                if (completed) {
                    ExplorerActionHostSessionValues.PLAYBACK_REPORT_COMPLETED
                } else {
                    ExplorerActionHostSessionValues.PLAYBACK_REPORT_PROGRESS
                },
            )
            if (state == HostPlaybackHistoryState.UNKNOWN) {
                state = HostPlaybackHistoryState.ENABLED
            }
            true
        }.getOrElse {
            state = HostPlaybackHistoryState.UNSUPPORTED
            false
        }
    }
}
