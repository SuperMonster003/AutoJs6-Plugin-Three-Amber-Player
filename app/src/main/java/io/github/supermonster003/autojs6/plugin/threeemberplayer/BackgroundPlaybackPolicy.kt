package io.github.supermonster003.autojs6.plugin.threeemberplayer

internal enum class PlaybackExitTarget {
    PICTURE_IN_PICTURE,
    BACKGROUND_AUDIO,
    RELEASE,
}

internal enum class BackgroundServiceState {
    IDLE,
    PLAYING,
    PAUSED,
    TERMINATED,
}

internal enum class BackgroundServiceEvent {
    HANDOFF_PLAYING,
    PLAYER_PLAYING,
    PLAYER_PAUSED,
    PLAYBACK_ENDED,
    PLAYER_ERROR,
    SETTING_DISABLED,
    UI_RECLAIMED,
}

internal data class BackgroundServiceTransition(
    val state: BackgroundServiceState,
    val stopService: Boolean = false,
    val releasePlayer: Boolean = false,
)

/** Android-free policy for choosing PiP/background audio and owning the service lifecycle. */
internal object BackgroundPlaybackPolicy {

    fun exitTarget(
        inPictureInPicture: Boolean,
        backgroundAudioEnabled: Boolean,
        playbackActive: Boolean,
        activityFinishing: Boolean,
    ): PlaybackExitTarget = when {
        inPictureInPicture -> PlaybackExitTarget.PICTURE_IN_PICTURE
        activityFinishing -> PlaybackExitTarget.RELEASE
        backgroundAudioEnabled && playbackActive -> PlaybackExitTarget.BACKGROUND_AUDIO
        else -> PlaybackExitTarget.RELEASE
    }

    fun transition(
        state: BackgroundServiceState,
        event: BackgroundServiceEvent,
    ): BackgroundServiceTransition {
        if (state == BackgroundServiceState.TERMINATED) {
            return BackgroundServiceTransition(BackgroundServiceState.TERMINATED)
        }
        return when (event) {
            BackgroundServiceEvent.HANDOFF_PLAYING,
            BackgroundServiceEvent.PLAYER_PLAYING,
            -> BackgroundServiceTransition(BackgroundServiceState.PLAYING)

            BackgroundServiceEvent.PLAYER_PAUSED -> BackgroundServiceTransition(
                BackgroundServiceState.PAUSED,
            )

            BackgroundServiceEvent.UI_RECLAIMED -> BackgroundServiceTransition(
                state = BackgroundServiceState.IDLE,
                stopService = false,
                releasePlayer = false,
            )

            BackgroundServiceEvent.PLAYBACK_ENDED,
            BackgroundServiceEvent.PLAYER_ERROR,
            BackgroundServiceEvent.SETTING_DISABLED,
            -> BackgroundServiceTransition(
                state = BackgroundServiceState.TERMINATED,
                stopService = true,
                releasePlayer = true,
            )
        }
    }
}
