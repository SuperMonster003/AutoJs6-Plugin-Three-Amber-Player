package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.media.audiofx.LoudnessEnhancer
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi

/** Owns one best-effort LoudnessEnhancer for the current ExoPlayer audio session. */
@OptIn(UnstableApi::class)
internal class PlayerLoudnessEnhancer : AutoCloseable {

    private var audioSessionId = C.AUDIO_SESSION_ID_UNSET
    private var effect: LoudnessEnhancer? = null
    private var level = VolumeBoostLevel.OFF

    fun onAudioSessionIdChanged(value: Int): Boolean {
        if (audioSessionId == value) return true
        releaseEffect()
        audioSessionId = value
        return applyCurrentLevel()
    }

    fun setLevel(value: VolumeBoostLevel): Boolean {
        level = value
        return applyCurrentLevel()
    }

    private fun applyCurrentLevel(): Boolean {
        if (level == VolumeBoostLevel.OFF) {
            releaseEffect()
            return true
        }
        if (audioSessionId == C.AUDIO_SESSION_ID_UNSET || audioSessionId <= 0) return true
        return runCatching {
            val enhancer = effect ?: LoudnessEnhancer(audioSessionId).also { effect = it }
            enhancer.setTargetGain(VolumeBoostPolicy.targetGainMillibels(level))
            enhancer.enabled = true
        }.isSuccess.also { applied ->
            if (!applied) releaseEffect()
        }
    }

    private fun releaseEffect() {
        effect?.let { enhancer ->
            runCatching { enhancer.enabled = false }
            runCatching { enhancer.release() }
        }
        effect = null
    }

    override fun close() {
        level = VolumeBoostLevel.OFF
        audioSessionId = C.AUDIO_SESSION_ID_UNSET
        releaseEffect()
    }
}
