package io.github.supermonster003.autojs6.plugin.videoplayer

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import io.github.supermonster003.autojs6.plugin.videoplayer.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var request: AndroidPlaybackRequest
    private var player: ExoPlayer? = null
    private var resumePosition = 0L
    private var resumePlayWhenReady = true
    private var requestAccepted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val resolvedRequest = AndroidVideoIntentPolicy.resolveInternal(intent)
        if (resolvedRequest == null) {
            Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        request = resolvedRequest
        requestAccepted = true
        resumePosition = savedInstanceState?.getLong(STATE_POSITION, 0L) ?: 0L
        resumePlayWhenReady = savedInstanceState?.getBoolean(STATE_PLAY_WHEN_READY, true) ?: true

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = request.displayName
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.playbackErrorPanel.isVisible = false
        binding.openWithOtherAppButton.setOnClickListener {
            if (!ExternalPlaybackLauncher.open(this, request)) {
                Toast.makeText(this, R.string.error_no_external_player, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (requestAccepted) initializePlayer()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        capturePlaybackState()
        outState.putLong(STATE_POSITION, resumePosition)
        outState.putBoolean(STATE_PLAY_WHEN_READY, resumePlayWhenReady)
        super.onSaveInstanceState(outState)
    }

    private fun initializePlayer() {
        if (player != null) return
        val mediaItemBuilder = MediaItem.Builder().setUri(request.targetUri)
        if (request.mimeType != "video/*") {
            mediaItemBuilder.setMimeType(request.mimeType)
        }
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            exoPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true,
            )
            exoPlayer.setHandleAudioBecomingNoisy(true)
            exoPlayer.setMediaItem(mediaItemBuilder.build())
            if (resumePosition > 0L) exoPlayer.seekTo(resumePosition)
            exoPlayer.playWhenReady = resumePlayWhenReady
            exoPlayer.addListener(playerListener)
            binding.playerView.player = exoPlayer
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        val exoPlayer = player ?: return
        capturePlaybackState()
        binding.playerView.player = null
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
        player = null
        setKeepScreenOn(false)
    }

    private fun capturePlaybackState() {
        player?.let { exoPlayer ->
            resumePosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            resumePlayWhenReady = exoPlayer.playWhenReady
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            setKeepScreenOn(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            setKeepScreenOn(false)
            binding.playbackErrorText.text = getString(
                R.string.error_cannot_play_video,
                error.errorCodeName,
            )
            binding.playbackErrorPanel.isVisible = true
        }
    }

    private fun setKeepScreenOn(enabled: Boolean) {
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private companion object {
        const val STATE_POSITION = "position"
        const val STATE_PLAY_WHEN_READY = "play_when_ready"
    }
}
