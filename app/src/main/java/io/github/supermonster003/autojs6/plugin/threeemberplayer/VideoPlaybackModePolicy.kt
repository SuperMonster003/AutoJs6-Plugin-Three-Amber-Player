package io.github.supermonster003.autojs6.plugin.threeemberplayer

internal enum class VideoPlaybackMode {
    SEQUENCE,
    SHUFFLE,
    REPEAT_ONE,
}

internal object VideoPlaybackModePolicy {

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
