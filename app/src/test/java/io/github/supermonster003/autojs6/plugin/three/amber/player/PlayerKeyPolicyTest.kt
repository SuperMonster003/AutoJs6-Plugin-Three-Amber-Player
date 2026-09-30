package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerKeyPolicyTest {

    @Test
    fun spaceAlwaysTogglesPlaybackOnKeyUp() {
        assertEquals(
            PlaybackKeyAction.CONSUME,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.SPACE,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.CONTROL,
                controlsVisible = true,
            ),
        )
        assertEquals(
            PlaybackKeyAction.TOGGLE_PLAY_PAUSE,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.SPACE,
                PlaybackKeyPhase.UP,
                PlaybackFocusTarget.CONTROL,
                controlsVisible = true,
            ),
        )
    }

    @Test
    fun enterActivatesFocusedControlButFocusesPlayFromVideo() {
        assertEquals(
            PlaybackKeyAction.DELEGATE,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.ENTER,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.CONTROL,
                controlsVisible = true,
            ),
        )
        assertEquals(
            PlaybackKeyAction.FOCUS_PLAY_PAUSE,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.ENTER,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.PLAYBACK_SURFACE,
                controlsVisible = true,
            ),
        )
    }

    @Test
    fun leftAndRightSeekFromVideoOrTimeBarAndDelegateBetweenControls() {
        assertEquals(
            PlaybackKeyAction.SEEK_BACK,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.DPAD_LEFT,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.PLAYBACK_SURFACE,
                controlsVisible = true,
            ),
        )
        assertEquals(
            PlaybackKeyAction.SEEK_FORWARD,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.DPAD_RIGHT,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.TIME_BAR,
                controlsVisible = true,
            ),
        )
        assertEquals(
            PlaybackKeyAction.DELEGATE,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.DPAD_RIGHT,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.CONTROL,
                controlsVisible = true,
            ),
        )
    }

    @Test
    fun hiddenControlsMakeDpadSeekAndSuppressRepeats() {
        assertEquals(
            PlaybackKeyAction.SEEK_FORWARD,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.DPAD_RIGHT,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.CONTROL,
                controlsVisible = false,
            ),
        )
        assertEquals(
            PlaybackKeyAction.CONSUME,
            PlayerKeyPolicy.resolve(
                PlaybackInputKey.DPAD_RIGHT,
                PlaybackKeyPhase.DOWN,
                PlaybackFocusTarget.CONTROL,
                controlsVisible = false,
                repeatCount = 1,
            ),
        )
    }
}
