package io.github.supermonster003.autojs6.plugin.threeemberplayer

import kotlin.math.ceil

/** Timing and eligibility rules for the non-disruptive queue auto-advance prompt. */
internal object AutoAdvancePromptPolicy {

    const val COUNTDOWN_MILLIS = 3_000L

    fun shouldPauseAtItemEnd(
        itemCount: Int,
        playbackMode: VideoPlaybackMode,
        abLoopActive: Boolean,
        stopAtEndOfVideo: Boolean,
    ): Boolean = stopAtEndOfVideo || (
        itemCount > 1 && playbackMode != VideoPlaybackMode.REPEAT_ONE && !abLoopActive
        )

    fun shouldShowPrompt(
        itemCount: Int,
        hasNextMediaItem: Boolean,
        playbackMode: VideoPlaybackMode,
        abLoopActive: Boolean,
        stopAtEndOfVideo: Boolean,
    ): Boolean = itemCount > 1 && hasNextMediaItem &&
        playbackMode != VideoPlaybackMode.REPEAT_ONE && !abLoopActive && !stopAtEndOfVideo

    fun remainingSeconds(deadlineElapsedRealtimeMs: Long, nowElapsedRealtimeMs: Long): Int {
        val remaining = (deadlineElapsedRealtimeMs - nowElapsedRealtimeMs).coerceAtLeast(0L)
        return ceil(remaining / 1_000.0).toInt()
    }
}
