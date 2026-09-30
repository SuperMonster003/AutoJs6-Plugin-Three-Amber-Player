package io.github.supermonster003.autojs6.plugin.three.amber.player

internal enum class VideoPlaybackMode {
    SEQUENCE,
    SHUFFLE,
    REPEAT_ONE,
}

internal object VideoPlaybackModePolicy {

    fun restoreRemembered(enabled: Boolean, storedValue: String?): VideoPlaybackMode {
        if (!enabled) return VideoPlaybackMode.SEQUENCE
        return runCatching { VideoPlaybackMode.valueOf(storedValue.orEmpty()) }
            .getOrDefault(VideoPlaybackMode.SEQUENCE)
    }

    fun normalizeForItemCount(mode: VideoPlaybackMode, itemCount: Int): VideoPlaybackMode {
        require(itemCount > 0)
        return if (itemCount == 1 && mode == VideoPlaybackMode.SHUFFLE) {
            VideoPlaybackMode.SEQUENCE
        } else {
            mode
        }
    }

    fun next(current: VideoPlaybackMode, itemCount: Int): VideoPlaybackMode {
        require(itemCount > 0)
        return if (itemCount == 1) {
            if (current == VideoPlaybackMode.REPEAT_ONE) {
                VideoPlaybackMode.SEQUENCE
            } else {
                VideoPlaybackMode.REPEAT_ONE
            }
        } else {
            when (current) {
                VideoPlaybackMode.SEQUENCE -> VideoPlaybackMode.SHUFFLE
                VideoPlaybackMode.SHUFFLE -> VideoPlaybackMode.REPEAT_ONE
                VideoPlaybackMode.REPEAT_ONE -> VideoPlaybackMode.SEQUENCE
            }
        }
    }
}
