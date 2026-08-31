package io.github.supermonster003.autojs6.plugin.threeemberplayer

internal enum class PlaybackInputKey {
    SPACE,
    ENTER,
    DPAD_CENTER,
    DPAD_LEFT,
    DPAD_RIGHT,
    DPAD_UP,
    DPAD_DOWN,
    OTHER,
}

internal enum class PlaybackKeyPhase {
    DOWN,
    UP,
}

internal enum class PlaybackFocusTarget {
    NONE,
    PLAYBACK_SURFACE,
    TIME_BAR,
    CONTROL,
}

internal enum class PlaybackKeyAction {
    DELEGATE,
    CONSUME,
    FOCUS_PLAY_PAUSE,
    TOGGLE_PLAY_PAUSE,
    SEEK_BACK,
    SEEK_FORWARD,
}

/** Android-free keyboard/DPAD mapping. Media keys deliberately remain owned by MediaSession. */
internal object PlayerKeyPolicy {

    fun resolve(
        key: PlaybackInputKey,
        phase: PlaybackKeyPhase,
        focus: PlaybackFocusTarget,
        controlsVisible: Boolean,
        repeatCount: Int = 0,
    ): PlaybackKeyAction {
        val playbackFocus = !controlsVisible || focus == PlaybackFocusTarget.NONE ||
            focus == PlaybackFocusTarget.PLAYBACK_SURFACE
        return when (key) {
            PlaybackInputKey.SPACE -> if (phase == PlaybackKeyPhase.UP) {
                PlaybackKeyAction.TOGGLE_PLAY_PAUSE
            } else {
                PlaybackKeyAction.CONSUME
            }

            PlaybackInputKey.ENTER -> when {
                playbackFocus && phase == PlaybackKeyPhase.DOWN -> PlaybackKeyAction.FOCUS_PLAY_PAUSE
                else -> PlaybackKeyAction.DELEGATE
            }

            PlaybackInputKey.DPAD_CENTER,
            PlaybackInputKey.DPAD_UP,
            PlaybackInputKey.DPAD_DOWN,
            -> if (playbackFocus && phase == PlaybackKeyPhase.DOWN) {
                PlaybackKeyAction.FOCUS_PLAY_PAUSE
            } else {
                PlaybackKeyAction.DELEGATE
            }

            PlaybackInputKey.DPAD_LEFT,
            PlaybackInputKey.DPAD_RIGHT,
            -> {
                val seeks = playbackFocus || focus == PlaybackFocusTarget.TIME_BAR
                when {
                    !seeks -> PlaybackKeyAction.DELEGATE
                    phase == PlaybackKeyPhase.UP -> PlaybackKeyAction.CONSUME
                    repeatCount > 0 -> PlaybackKeyAction.CONSUME
                    key == PlaybackInputKey.DPAD_LEFT -> PlaybackKeyAction.SEEK_BACK
                    else -> PlaybackKeyAction.SEEK_FORWARD
                }
            }

            PlaybackInputKey.OTHER -> PlaybackKeyAction.DELEGATE
        }
    }
}
