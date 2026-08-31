package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgroundPlaybackPolicyTest {

    @Test
    fun pictureInPictureAlwaysWinsOverBackgroundAudio() {
        assertEquals(
            PlaybackExitTarget.PICTURE_IN_PICTURE,
            BackgroundPlaybackPolicy.exitTarget(
                inPictureInPicture = true,
                backgroundAudioEnabled = true,
                playbackActive = true,
                activityFinishing = false,
            ),
        )
    }

    @Test
    fun backgroundAudioRequiresExplicitSettingAndActivePlayback() {
        assertEquals(
            PlaybackExitTarget.BACKGROUND_AUDIO,
            BackgroundPlaybackPolicy.exitTarget(false, true, true, false),
        )
        assertEquals(
            PlaybackExitTarget.RELEASE,
            BackgroundPlaybackPolicy.exitTarget(false, false, true, false),
        )
        assertEquals(
            PlaybackExitTarget.RELEASE,
            BackgroundPlaybackPolicy.exitTarget(false, true, false, false),
        )
        assertEquals(
            PlaybackExitTarget.RELEASE,
            BackgroundPlaybackPolicy.exitTarget(false, true, true, true),
        )
    }

    @Test
    fun serviceKeepsPausedSessionButStopsAndReleasesOnTerminalEvents() {
        val paused = BackgroundPlaybackPolicy.transition(
            BackgroundServiceState.PLAYING,
            BackgroundServiceEvent.PLAYER_PAUSED,
        )
        assertEquals(BackgroundServiceState.PAUSED, paused.state)
        assertFalse(paused.stopService)
        assertFalse(paused.releasePlayer)

        listOf(
            BackgroundServiceEvent.PLAYBACK_ENDED,
            BackgroundServiceEvent.PLAYER_ERROR,
            BackgroundServiceEvent.SETTING_DISABLED,
        ).forEach { event ->
            val result = BackgroundPlaybackPolicy.transition(BackgroundServiceState.PLAYING, event)
            assertEquals(BackgroundServiceState.TERMINATED, result.state)
            assertTrue(result.stopService)
            assertTrue(result.releasePlayer)
        }
    }

    @Test
    fun uiReclaimKeepsPreparedServiceWithoutReleasingTransferredPlayer() {
        val result = BackgroundPlaybackPolicy.transition(
            BackgroundServiceState.PAUSED,
            BackgroundServiceEvent.UI_RECLAIMED,
        )
        assertEquals(BackgroundServiceState.IDLE, result.state)
        assertFalse(result.stopService)
        assertFalse(result.releasePlayer)
    }
}
