package io.github.supermonster003.autojs6.plugin.videoplayer

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.text.format.Formatter
import android.util.Rational
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.TimeBar
import io.github.supermonster003.autojs6.plugin.videoplayer.databinding.ActivityVideoPlayerBinding
import kotlin.math.abs
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var request: AndroidPlaybackRequest
    private lateinit var positionStore: PlaybackPositionStore
    private lateinit var settingsStore: PlayerSettingsStore
    private lateinit var audioManager: AudioManager
    private lateinit var insetsController: WindowInsetsControllerCompat
    private lateinit var trackController: PlayerTrackController
    private lateinit var systemIntegration: PlayerSystemIntegration
    private var hostProgressClient: HostPlaybackProgressClient? = null

    private var player: ExoPlayer? = null
    private var frameCapture: PlayerFrameCapture? = null
    private var scrubThumbnailProvider: ScrubThumbnailProvider? = null
    private var requestAccepted = false

    private var resumePosition = 0L
    private var resumePlayWhenReady = true
    private var playbackSpeed = 1f
    private var resizeMode = PlayerResizeMode.FIT
    private var orientationMode = PlayerOrientationMode.AUTO
    private var controlsLocked = false
    private var playbackMode = VideoPlaybackMode.SEQUENCE
    private var activeMediaItemIndex = 0
    private var resumeMediaItemIndex = 0
    private val resumedMediaItemIndexes = HashSet<Int>()
    private val lastPositionsByIndex = HashMap<Int, Long>()
    private val durationsByIndex = HashMap<Int, Long>()
    private lateinit var gestureSettings: PlayerGestureSettings
    private var sleepTimerMode = SleepTimerMode.OFF
    private var sleepTimerDeadlineElapsedRealtimeMs = 0L
    private var manualZoomScale = PlayerGesturePolicy.DEFAULT_ZOOM_SCALE
    private var zoomPivotXFraction = 0.5f
    private var zoomPivotYFraction = 0.5f
    private var orientationSuggestionApplied = false
    private var orientationManuallyChanged = false
    private var pendingResumeToastPosition = -1L

    private var controlsVisible = true
    private var scrubbing = false
    private var speedBoostActive = false
    private var seekDragStartPosition = 0L
    private var seekDragTarget = 0L
    private var verticalDragStartFraction = 0f
    private var hasAudioTrackChoices = false
    private var hasSubtitleTracks = false
    private var inPictureInPicture = false
    private var controlsVisibleBeforePictureInPicture = true
    private var videoWidth = 0
    private var videoHeight = 0
    private var videoRotationDegrees = 0
    private var videoPixelWidthHeightRatio = 1f
    private var pipReceiverRegistered = false
    private var screenshotInProgress = false
    private var scrubPreviewPosition = 0L
    private var scrubPreviewBitmap: Bitmap? = null
    @Volatile
    private var xvidFourCcCompatibilityApplied = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private val hideControlsRunnable = Runnable { hideControls() }
    private val hideOsdRunnable = Runnable { binding.osdPanel.isVisible = false }
    private val hideUnlockRunnable = Runnable { binding.unlockButton.isVisible = false }
    private val sleepTimerRunnable = Runnable { updateSleepTimer() }
    private val pictureInPictureActionToken = Integer.toHexString(System.identityHashCode(this))
    private val pictureInPicturePlayAction = "$ACTION_PIP_PLAY_PREFIX.$pictureInPictureActionToken"
    private val pictureInPicturePauseAction = "$ACTION_PIP_PAUSE_PREFIX.$pictureInPictureActionToken"
    private val pictureInPictureActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                pictureInPicturePlayAction -> player?.play()
                pictureInPicturePauseAction -> player?.pause()
            }
            updatePictureInPictureParameters()
        }
    }
    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            mainHandler.postDelayed(this, PROGRESS_INTERVAL_MS)
        }
    }

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
        activeMediaItemIndex = request.startIndex
        resumeMediaItemIndex = request.startIndex
        hostProgressClient = request.hostSession?.let { session ->
            HostPlaybackProgressClient(session, requireNotNull(request.hostTargetId))
        }
        positionStore = PlaybackPositionStore(this)
        settingsStore = PlayerSettingsStore(this)
        gestureSettings = settingsStore.read()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            frameCapture = PlayerFrameCapture(this)
        }

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        trackController = PlayerTrackController(
            activity = this,
            playerProvider = { player },
            onAvailabilityChanged = ::onTrackAvailabilityChanged,
            onDialogDismissed = ::postponeControlsAutoHide,
        )
        systemIntegration = PlayerSystemIntegration(this)
        restorePlaybackState(savedInstanceState)
        setUpWindow()
        setUpControls()
        registerPictureInPictureActions()
        applyResizeMode(showOsd = false)
        binding.playerView.post { applyManualZoom() }
        applyOrientationMode(showOsd = false)
        applyLockState()
        scheduleSleepTimer()
    }

    override fun onStart() {
        super.onStart()
        if (requestAccepted) {
            initializePlayer()
            mainHandler.removeCallbacks(progressRunnable)
            mainHandler.post(progressRunnable)
        }
    }

    override fun onStop() {
        if (requestAccepted && !inPictureInPicture) {
            mainHandler.removeCallbacks(progressRunnable)
            savePlaybackPosition()
            releasePlayer()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (requestAccepted && player != null) {
            mainHandler.removeCallbacks(progressRunnable)
            savePlaybackPosition()
            releasePlayer()
        }
        if (pipReceiverRegistered) {
            unregisterReceiver(pictureInPictureActionReceiver)
            pipReceiverRegistered = false
        }
        hideScrubPreview()
        scrubThumbnailProvider?.close()
        scrubThumbnailProvider = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            frameCapture?.close()
            frameCapture = null
        }
        if (requestAccepted) {
            request.hostSession?.let { session -> runCatching { session.close() } }
        }
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.O until Build.VERSION_CODES.S &&
            canEnterPictureInPicture()
        ) {
            enterPictureInPicture()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPictureInPicture = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            controlsVisibleBeforePictureInPicture = controlsVisible
            mainHandler.removeCallbacks(hideControlsRunnable)
            binding.topBar.isVisible = false
            binding.bottomBar.isVisible = false
            binding.unlockButton.isVisible = false
            binding.osdPanel.isVisible = false
            hideScrubPreview()
        } else if (controlsLocked) {
            applyLockState()
        } else if (controlsVisibleBeforePictureInPicture || player?.isPlaying != true) {
            showControls()
        } else {
            hideControls()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        capturePlaybackState()
        outState.putLong(STATE_POSITION, resumePosition)
        outState.putBoolean(STATE_PLAY_WHEN_READY, resumePlayWhenReady)
        outState.putFloat(STATE_SPEED, playbackSpeed)
        outState.putInt(STATE_RESIZE_MODE, resizeMode.ordinal)
        outState.putInt(STATE_ORIENTATION_MODE, orientationMode.ordinal)
        outState.putBoolean(STATE_LOCKED, controlsLocked)
        outState.putInt(STATE_PLAYBACK_MODE, playbackMode.ordinal)
        outState.putInt(STATE_MEDIA_ITEM_INDEX, player?.currentMediaItemIndex ?: activeMediaItemIndex)
        outState.putInt(STATE_SLEEP_TIMER_MODE, sleepTimerMode.ordinal)
        outState.putLong(STATE_SLEEP_TIMER_DEADLINE, sleepTimerDeadlineElapsedRealtimeMs)
        outState.putFloat(STATE_ZOOM_SCALE, manualZoomScale)
        outState.putFloat(STATE_ZOOM_PIVOT_X, zoomPivotXFraction)
        outState.putFloat(STATE_ZOOM_PIVOT_Y, zoomPivotYFraction)
        outState.putBoolean(STATE_ORIENTATION_MANUAL, orientationManuallyChanged)
        outState.putInt(STATE_AUDIO_GROUP, trackController.selectedAudioKey?.groupIndex ?: -1)
        outState.putInt(STATE_AUDIO_TRACK, trackController.selectedAudioKey?.trackIndex ?: -1)
        outState.putInt(STATE_SUBTITLE_GROUP, trackController.selectedSubtitleKey?.groupIndex ?: -1)
        outState.putInt(STATE_SUBTITLE_TRACK, trackController.selectedSubtitleKey?.trackIndex ?: -1)
        super.onSaveInstanceState(outState)
    }

    private fun restorePlaybackState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            resumePosition = savedInstanceState.getLong(STATE_POSITION, 0L)
            resumePlayWhenReady = savedInstanceState.getBoolean(STATE_PLAY_WHEN_READY, true)
            playbackSpeed = savedInstanceState.getFloat(STATE_SPEED, 1f)
            resizeMode = enumFromOrdinal<PlayerResizeMode>(savedInstanceState.getInt(STATE_RESIZE_MODE, 0))
            orientationMode = enumFromOrdinal<PlayerOrientationMode>(savedInstanceState.getInt(STATE_ORIENTATION_MODE, 0))
            controlsLocked = savedInstanceState.getBoolean(STATE_LOCKED, false)
            playbackMode = enumFromOrdinal<VideoPlaybackMode>(
                savedInstanceState.getInt(STATE_PLAYBACK_MODE, VideoPlaybackMode.SEQUENCE.ordinal),
            )
            resumeMediaItemIndex = savedInstanceState.getInt(STATE_MEDIA_ITEM_INDEX, request.startIndex)
                .coerceIn(request.items.indices)
            activeMediaItemIndex = resumeMediaItemIndex
            sleepTimerMode = enumFromOrdinal<SleepTimerMode>(
                savedInstanceState.getInt(STATE_SLEEP_TIMER_MODE, SleepTimerMode.OFF.ordinal),
            )
            sleepTimerDeadlineElapsedRealtimeMs = savedInstanceState.getLong(
                STATE_SLEEP_TIMER_DEADLINE,
                0L,
            )
            manualZoomScale = savedInstanceState.getFloat(
                STATE_ZOOM_SCALE,
                PlayerGesturePolicy.DEFAULT_ZOOM_SCALE,
            ).coerceIn(PlayerGesturePolicy.MIN_ZOOM_SCALE, PlayerGesturePolicy.MAX_ZOOM_SCALE)
            zoomPivotXFraction = savedInstanceState.getFloat(STATE_ZOOM_PIVOT_X, 0.5f)
                .coerceIn(0f, 1f)
            zoomPivotYFraction = savedInstanceState.getFloat(STATE_ZOOM_PIVOT_Y, 0.5f)
                .coerceIn(0f, 1f)
            orientationManuallyChanged = savedInstanceState.getBoolean(STATE_ORIENTATION_MANUAL, false)
            trackController.restoreSelections(
                audioGroupIndex = savedInstanceState.getInt(STATE_AUDIO_GROUP, -1),
                audioTrackIndex = savedInstanceState.getInt(STATE_AUDIO_TRACK, -1),
                subtitleGroupIndex = savedInstanceState.getInt(STATE_SUBTITLE_GROUP, -1),
                subtitleTrackIndex = savedInstanceState.getInt(STATE_SUBTITLE_TRACK, -1),
            )
        } else {
            val item = request.items[resumeMediaItemIndex]
            val hostResume = item.relativePath?.let { relativePath ->
                hostProgressClient?.resumePosition(relativePath)
            }
            val rememberedPosition = hostResume ?: takeIf {
                hostProgressClient == null ||
                    hostProgressClient?.state == HostPlaybackHistoryState.UNSUPPORTED
            }?.let {
                positionStore.read(positionKey(item))?.positionMs
            }
            rememberedPosition?.let { remembered ->
                resumePosition = remembered
                pendingResumeToastPosition = remembered
            }
        }
    }

    private inline fun <reified T : Enum<T>> enumFromOrdinal(ordinal: Int): T {
        val values = enumValues<T>()
        return values[ordinal.coerceIn(0, values.size - 1)]
    }

    private fun setUpWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        insetsController = WindowInsetsControllerCompat(window, binding.root).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            binding.topBar.setPadding(bars.left, bars.top, bars.right, 0)
            binding.bottomBar.setPadding(bars.left, 0, bars.right, bars.bottom)
            insets
        }
    }

    private fun setUpControls() {
        binding.toolbar.title = currentItem().displayName
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_audio_track -> {
                    mainHandler.removeCallbacks(hideControlsRunnable)
                    trackController.showAudioDialog()
                    true
                }
                R.id.action_subtitle_track -> {
                    mainHandler.removeCallbacks(hideControlsRunnable)
                    trackController.showSubtitleDialog()
                    true
                }
                R.id.action_repeat -> {
                    togglePlaybackMode()
                    true
                }
                R.id.action_playlist -> {
                    showPlaylistDialog()
                    true
                }
                R.id.action_previous_video -> {
                    player?.seekToPreviousMediaItem()
                    true
                }
                R.id.action_next_video -> {
                    player?.seekToNextMediaItem()
                    true
                }
                R.id.action_picture_in_picture -> {
                    val entered = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                        enterPictureInPicture()
                    if (!entered) {
                        Toast.makeText(this, R.string.pip_requires_playback, Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.action_sleep_timer -> {
                    showSleepTimerDialog()
                    true
                }
                R.id.action_screenshot -> {
                    captureCurrentFrame()
                    true
                }
                R.id.action_gesture_settings -> {
                    showGestureSettingsDialog()
                    true
                }
                R.id.action_video_info -> {
                    showVideoInfoDialog()
                    true
                }
                else -> false
            }
        }
        PlayerGestureController(binding.gestureArea, gestureHost)
        binding.playPauseButton.setOnClickListener {
            togglePlayPause()
            postponeControlsAutoHide()
        }
        binding.seekBackButton.setOnClickListener {
            seekBy(-PlayerGesturePolicy.DOUBLE_TAP_SEEK_MS)
            postponeControlsAutoHide()
        }
        binding.seekForwardButton.setOnClickListener {
            seekBy(PlayerGesturePolicy.DOUBLE_TAP_SEEK_MS)
            postponeControlsAutoHide()
        }
        binding.speedButton.setOnClickListener { showSpeedDialog() }
        binding.resizeButton.setOnClickListener {
            if (!resetManualZoom(showOsd = true)) {
                resizeMode = resizeMode.next()
                applyResizeMode(showOsd = true)
            }
            postponeControlsAutoHide()
        }
        binding.rotateButton.setOnClickListener {
            orientationManuallyChanged = true
            orientationSuggestionApplied = true
            orientationMode = orientationMode.next()
            applyOrientationMode(showOsd = true)
            postponeControlsAutoHide()
        }
        binding.lockButton.setOnClickListener { setControlsLocked(true) }
        binding.unlockButton.setOnClickListener { setControlsLocked(false) }
        binding.retryButton.setOnClickListener { retryPlayback() }
        binding.openWithOtherAppButton.setOnClickListener {
            if (!ExternalPlaybackLauncher.open(this, currentItem())) {
                Toast.makeText(this, R.string.error_no_external_player, Toast.LENGTH_SHORT).show()
            }
        }
        binding.timeBar.addListener(scrubListener)
        updateSpeedButton()
        updateToolbarMenu()
    }

    private fun initializePlayer() {
        if (player != null) return
        xvidFourCcCompatibilityApplied = false
        val mediaItems = request.items.mapIndexed { index, item -> mediaItem(index, item) }
        val extractorsFactory = XvidCompatibleExtractorsFactory(
            onCompatibilityApplied = {
                xvidFourCcCompatibilityApplied = true
            },
        )
        val mediaSourceFactory = request.hostSession?.let { session ->
            DefaultMediaSourceFactory(
                DefaultDataSource.Factory(
                    this,
                    ExplorerSessionDataSource.Factory(
                        session = session,
                        targetId = requireNotNull(request.hostTargetId),
                        relativePathsByUri = request.hostRelativePathsByUri(),
                    ),
                ),
                extractorsFactory,
            )
        } ?: DefaultMediaSourceFactory(this, extractorsFactory)
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setSeekBackIncrementMs(PlayerGesturePolicy.DOUBLE_TAP_SEEK_MS)
            .setSeekForwardIncrementMs(PlayerGesturePolicy.DOUBLE_TAP_SEEK_MS)
            .build().also { exoPlayer ->
                exoPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build(),
                    true,
                )
                exoPlayer.setHandleAudioBecomingNoisy(true)
                trackController.configureInitialParameters(exoPlayer)
                exoPlayer.setMediaItems(mediaItems, resumeMediaItemIndex, resumePosition)
                exoPlayer.playWhenReady = resumePlayWhenReady
                exoPlayer.setPlaybackSpeed(playbackSpeed)
                applyPlaybackMode(exoPlayer)
                exoPlayer.addListener(playerListener)
                binding.playerView.player = exoPlayer
                systemIntegration.attach(exoPlayer)
                exoPlayer.prepare()
        }
        resumedMediaItemIndexes += resumeMediaItemIndex
        updateActiveItemUi()
        binding.retryButton.isVisible = true
        binding.playbackErrorPanel.isVisible = false
        updatePlayPauseButton()
        updateToolbarMenu()
        updatePictureInPictureParameters()
        showControls()
        if (pendingResumeToastPosition >= 0L) {
            Toast.makeText(
                this,
                getString(
                    R.string.resume_from_position,
                    PlayerGesturePolicy.formatTime(pendingResumeToastPosition),
                ),
                Toast.LENGTH_SHORT,
            ).show()
            pendingResumeToastPosition = -1L
        }
    }

    private fun mediaItem(index: Int, item: AndroidPlaybackItem): MediaItem {
        val builder = MediaItem.Builder()
            .setMediaId(index.toString())
            .setUri(item.sourceUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(item.displayName)
                    .setDisplayTitle(item.displayName)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
                    .build(),
            )
        if (item.mimeType != "video/*") builder.setMimeType(item.mimeType)
        if (item.subtitles.isNotEmpty()) {
            builder.setSubtitleConfigurations(
                item.subtitles.map { subtitle ->
                    MediaItem.SubtitleConfiguration.Builder(subtitle.sourceUri)
                        .setMimeType(subtitle.mimeType)
                        .setLabel(subtitle.displayName)
                        .apply { subtitle.languageTag?.let(::setLanguage) }
                        .build()
                },
            )
        }
        return builder.build()
    }

    private fun currentItem(): AndroidPlaybackItem =
        request.items[activeMediaItemIndex.coerceIn(request.items.indices)]

    private fun updateActiveItemUi() {
        if (!::binding.isInitialized) return
        val item = currentItem()
        binding.toolbar.title = item.displayName
        binding.openWithOtherAppButton.isVisible = item.externalUri != null
        scrubThumbnailProvider?.close()
        scrubThumbnailProvider = null
        hideScrubPreview()
        updateToolbarMenu()
    }

    private fun resumeItemIfAvailable(index: Int) {
        if (!resumedMediaItemIndexes.add(index)) return
        val item = request.items[index]
        val hostResume = item.relativePath?.let { relativePath ->
            hostProgressClient?.resumePosition(relativePath)
        }
        val rememberedPosition = hostResume ?: takeIf {
            hostProgressClient == null ||
                hostProgressClient?.state == HostPlaybackHistoryState.UNSUPPORTED
        }?.let {
            positionStore.read(positionKey(item))?.positionMs
        }
        rememberedPosition?.takeIf { it > 0L }?.let { position ->
            player?.seekTo(index, position)
            Toast.makeText(
                this,
                getString(R.string.resume_from_position, PlayerGesturePolicy.formatTime(position)),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun persistCachedItemPosition(index: Int, completed: Boolean) {
        val duration = durationsByIndex[index] ?: return
        val position = if (completed) duration else lastPositionsByIndex[index] ?: return
        persistItemPosition(index, position, duration, completed)
    }

    private fun positionKey(item: AndroidPlaybackItem): String {
        val targetId = request.hostTargetId
        val relativePath = item.relativePath
        return if (targetId != null && relativePath != null) {
            "explorer-session:$targetId:$relativePath"
        } else {
            item.externalUri?.toString() ?: item.sourceUri.toString()
        }
    }

    private fun releasePlayer() {
        val exoPlayer = player ?: return
        capturePlaybackState()
        speedBoostActive = false
        systemIntegration.detach()
        trackController.clearAvailability()
        binding.playerView.player = null
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
        player = null
        setKeepScreenOn(false)
        updateToolbarMenu()
        updatePictureInPictureParameters()
    }

    private fun capturePlaybackState() {
        player?.let { exoPlayer ->
            resumePosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            resumePlayWhenReady = exoPlayer.playWhenReady
        }
    }

    private fun savePlaybackPosition() {
        val exoPlayer = player ?: return
        val duration = exoPlayer.duration
        if (duration <= 0L || duration == C.TIME_UNSET) return
        persistItemPosition(
            index = exoPlayer.currentMediaItemIndex,
            positionMillis = exoPlayer.currentPosition.coerceAtLeast(0L),
            durationMillis = duration,
            completed = false,
        )
    }

    private fun persistItemPosition(
        index: Int,
        positionMillis: Long,
        durationMillis: Long,
        completed: Boolean,
    ) {
        val item = request.items.getOrNull(index) ?: return
        if (durationMillis <= 0L || durationMillis == C.TIME_UNSET) return
        val boundedPosition = positionMillis.coerceIn(0L, durationMillis)
        val handledByHost = item.relativePath?.let { relativePath ->
            hostProgressClient?.report(relativePath, boundedPosition, durationMillis, completed)
        }
        if (handledByHost == true || hostProgressClient?.state == HostPlaybackHistoryState.DISABLED) {
            return
        }
        if (completed) {
            positionStore.clear(positionKey(item))
        } else {
            positionStore.save(positionKey(item), boundedPosition, durationMillis)
        }
    }

    private fun retryPlayback() {
        binding.playbackErrorPanel.isVisible = false
        releasePlayer()
        resumePlayWhenReady = true
        initializePlayer()
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            setKeepScreenOn(isPlaying)
            if (isPlaying) postponeControlsAutoHide() else showControls()
            updateToolbarMenu()
            updatePictureInPictureParameters()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                val exoPlayer = player
                if (exoPlayer != null) {
                    persistItemPosition(
                        index = exoPlayer.currentMediaItemIndex,
                        positionMillis = exoPlayer.duration.coerceAtLeast(0L),
                        durationMillis = exoPlayer.duration,
                        completed = true,
                    )
                }
                if (sleepTimerMode == SleepTimerMode.END_OF_VIDEO) {
                    finishSleepTimer()
                }
                showControls()
            }
            updatePlayPauseButton()
            updateToolbarMenu()
            updatePictureInPictureParameters()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            updatePlayPauseButton()
            updateToolbarMenu()
            updatePictureInPictureParameters()
        }

        override fun onTracksChanged(tracks: Tracks) {
            trackController.onTracksChanged(tracks)
            if (xvidFourCcCompatibilityApplied && hasUnsupportedXvidVideoTrack(tracks)) {
                player?.pause()
                setKeepScreenOn(false)
                hideScrubPreview()
                showPlaybackError(
                    message = getString(R.string.error_xvid_decoder_unavailable),
                    retryable = false,
                )
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val nextIndex = mediaItem?.mediaId?.toIntOrNull()
                ?.takeIf { it in request.items.indices }
                ?: player?.currentMediaItemIndex
                ?: return
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
                persistCachedItemPosition(activeMediaItemIndex, completed = true)
            } else if (nextIndex != activeMediaItemIndex) {
                persistCachedItemPosition(
                    activeMediaItemIndex,
                    completed = reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO,
                )
                activeMediaItemIndex = nextIndex
                trackController.resetSelectionsForMediaItem()
                resumeItemIfAvailable(nextIndex)
            }
            updateActiveItemUi()
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            videoWidth = videoSize.width
            videoHeight = videoSize.height
            // Media3 applies container rotation before reporting VideoSize on API 21+.
            videoRotationDegrees = 0
            videoPixelWidthHeightRatio = videoSize.pixelWidthHeightRatio
            applySuggestedOrientationIfNeeded(videoSize)
            updatePictureInPictureParameters()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateToolbarMenu()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            updateToolbarMenu()
        }

        override fun onPlayerError(error: PlaybackException) {
            setKeepScreenOn(false)
            capturePlaybackState()
            hideScrubPreview()
            if (isXvidVideoDecoderFailure(error)) {
                showPlaybackError(
                    message = getString(R.string.error_xvid_decoder_failed),
                    retryable = false,
                )
            } else {
                showPlaybackError(
                    message = getString(R.string.error_cannot_play_video, error.errorCodeName),
                    retryable = true,
                )
            }
            updateToolbarMenu()
            updatePictureInPictureParameters()
        }
    }

    private fun isXvidVideoDecoderFailure(error: PlaybackException): Boolean {
        if (!xvidFourCcCompatibilityApplied) return false
        val rendererError = error as? ExoPlaybackException ?: return false
        if (rendererError.type != ExoPlaybackException.TYPE_RENDERER ||
            rendererError.rendererFormat?.sampleMimeType != MimeTypes.VIDEO_MP4V
        ) {
            return false
        }
        return error.errorCode in setOf(
            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
            PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED,
            PlaybackException.ERROR_CODE_DECODING_FAILED,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES,
            PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
        )
    }

    private fun hasUnsupportedXvidVideoTrack(tracks: Tracks): Boolean {
        var foundXvidVideoTrack = false
        tracks.groups.forEach { group ->
            if (group.type != C.TRACK_TYPE_VIDEO) return@forEach
            repeat(group.length) { trackIndex ->
                if (group.getTrackFormat(trackIndex).sampleMimeType != MimeTypes.VIDEO_MP4V) {
                    return@repeat
                }
                foundXvidVideoTrack = true
                if (group.isTrackSupported(trackIndex, true)) return false
            }
        }
        return foundXvidVideoTrack
    }

    private fun showPlaybackError(message: CharSequence, retryable: Boolean) {
        binding.playbackErrorText.text = message
        binding.retryButton.isVisible = retryable
        binding.playbackErrorPanel.isVisible = true
    }

    private val scrubListener = object : TimeBar.OnScrubListener {
        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            scrubbing = true
            mainHandler.removeCallbacks(hideControlsRunnable)
            binding.positionText.text = PlayerGesturePolicy.formatTime(position)
            showScrubPreview(position)
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            binding.positionText.text = PlayerGesturePolicy.formatTime(position)
            showScrubPreview(position)
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            scrubbing = false
            hideScrubPreview()
            if (!canceled) player?.seekTo(position)
            updateProgress()
            postponeControlsAutoHide()
        }
    }

    private fun showScrubPreview(position: Long) {
        val duration = player?.duration?.takeIf { it > 0L } ?: return
        scrubPreviewPosition = position.coerceIn(0L, duration)
        binding.scrubPreviewText.text = PlayerGesturePolicy.formatTime(scrubPreviewPosition)
        binding.scrubPreviewPanel.isVisible = true
        positionScrubPreview(scrubPreviewPosition, duration)

        val item = currentItem()
        val provider = scrubThumbnailProvider ?: ScrubThumbnailProvider(
            context = this,
            uri = item.sourceUri,
            targetWidth = SCRUB_THUMBNAIL_WIDTH_PX,
            targetHeight = SCRUB_THUMBNAIL_HEIGHT_PX,
            descriptorOpener = item.relativePath
                ?.takeIf { item.sourceUri.scheme == VideoIntentFactory.HOST_SOURCE_SCHEME }
                ?.let { relativePath ->
                    {
                        requireNotNull(request.hostSession).openFile(
                            requireNotNull(request.hostTargetId),
                            relativePath,
                        )
                    }
                },
        ).also { scrubThumbnailProvider = it }
        provider.request(scrubPreviewPosition) { _, bitmap ->
            if (!scrubbing) {
                bitmap?.recycle()
                return@request
            }
            scrubPreviewBitmap?.recycle()
            scrubPreviewBitmap = bitmap
            binding.scrubThumbnail.setImageBitmap(bitmap)
            binding.scrubThumbnail.isVisible = bitmap != null
            positionScrubPreview(scrubPreviewPosition, duration)
        }
    }

    private fun positionScrubPreview(position: Long, duration: Long) {
        binding.scrubPreviewPanel.post {
            if (!binding.scrubPreviewPanel.isVisible || duration <= 0L) return@post
            val rootLocation = IntArray(2)
            val timeBarLocation = IntArray(2)
            binding.root.getLocationOnScreen(rootLocation)
            binding.timeBar.getLocationOnScreen(timeBarLocation)
            val fraction = (position.toDouble() / duration).coerceIn(0.0, 1.0)
            val thumbCenter = timeBarLocation[0] - rootLocation[0] +
                (binding.timeBar.width * fraction).roundToInt()
            val margin = SCRUB_PREVIEW_MARGIN_DP * resources.displayMetrics.density
            binding.scrubPreviewPanel.x = (thumbCenter - binding.scrubPreviewPanel.width / 2f)
                .coerceIn(
                    margin,
                    (binding.root.width - binding.scrubPreviewPanel.width - margin)
                        .coerceAtLeast(margin),
                )
            binding.scrubPreviewPanel.y = (timeBarLocation[1] - rootLocation[1] -
                binding.scrubPreviewPanel.height - margin).coerceAtLeast(margin)
        }
    }

    private fun hideScrubPreview() {
        if (!::binding.isInitialized) return
        binding.scrubPreviewPanel.isVisible = false
        binding.scrubThumbnail.setImageDrawable(null)
        scrubPreviewBitmap?.recycle()
        scrubPreviewBitmap = null
    }

    // region Controls visibility

    private fun showControls() {
        if (inPictureInPicture) return
        controlsVisible = true
        if (!controlsLocked) {
            binding.topBar.isVisible = true
            binding.bottomBar.isVisible = true
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
        postponeControlsAutoHide()
    }

    private fun hideControls() {
        controlsVisible = false
        binding.topBar.isVisible = false
        binding.bottomBar.isVisible = false
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun postponeControlsAutoHide() {
        mainHandler.removeCallbacks(hideControlsRunnable)
        if (player?.isPlaying == true && !scrubbing) {
            mainHandler.postDelayed(hideControlsRunnable, PlayerGesturePolicy.CONTROLS_AUTO_HIDE_MS)
        }
    }

    private fun setControlsLocked(locked: Boolean) {
        controlsLocked = locked
        applyLockState()
    }

    private fun applyLockState() {
        if (inPictureInPicture) {
            binding.topBar.isVisible = false
            binding.bottomBar.isVisible = false
            binding.unlockButton.isVisible = false
            return
        }
        if (controlsLocked) {
            hideControls()
            revealUnlockButton()
        } else {
            binding.unlockButton.isVisible = false
            mainHandler.removeCallbacks(hideUnlockRunnable)
            showControls()
        }
    }

    private fun revealUnlockButton() {
        binding.unlockButton.isVisible = true
        mainHandler.removeCallbacks(hideUnlockRunnable)
        mainHandler.postDelayed(hideUnlockRunnable, UNLOCK_BUTTON_HIDE_MS)
    }

    // endregion

    // region Picture in picture

    private fun registerPictureInPictureActions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val filter = IntentFilter().apply {
            addAction(pictureInPicturePlayAction)
            addAction(pictureInPicturePauseAction)
        }
        ContextCompat.registerReceiver(
            this,
            pictureInPictureActionReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        pipReceiverRegistered = true
    }

    private fun canEnterPictureInPicture(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || inPictureInPicture) return false
        if (binding.playbackErrorPanel.isVisible) return false
        val exoPlayer = player ?: return false
        return exoPlayer.playWhenReady &&
            exoPlayer.playbackState != Player.STATE_IDLE &&
            exoPlayer.playbackState != Player.STATE_ENDED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPictureInPicture(): Boolean {
        if (!canEnterPictureInPicture()) return false
        return runCatching {
            enterPictureInPictureMode(buildPictureInPictureParams())
        }.getOrDefault(false)
    }

    private fun updatePictureInPictureParameters() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        runCatching { setPictureInPictureParams(buildPictureInPictureParams()) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPictureInPictureParams(): PictureInPictureParams {
        val builder = PictureInPictureParams.Builder()
            .setActions(listOf(buildPictureInPictureRemoteAction()))
        val sourceRect = Rect()
        if (binding.playerView.getGlobalVisibleRect(sourceRect) && !sourceRect.isEmpty) {
            builder.setSourceRectHint(sourceRect)
        }
        PlaybackMediaPolicy.pipAspectRatio(
            width = videoWidth,
            height = videoHeight,
            rotationDegrees = videoRotationDegrees,
            pixelWidthHeightRatio = videoPixelWidthHeightRatio,
        )?.let { ratio ->
            builder.setAspectRatio(Rational(ratio.numerator, ratio.denominator))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(canEnterPictureInPicture())
        }
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPictureInPictureRemoteAction(): RemoteAction {
        val showPause = shouldShowPauseAction()
        val action = if (showPause) pictureInPicturePauseAction else pictureInPicturePlayAction
        val titleRes = if (showPause) R.string.action_pause else R.string.action_play
        val iconRes = if (showPause) R.drawable.ic_pause else R.drawable.ic_play_arrow
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            if (showPause) PIP_PAUSE_REQUEST_CODE else PIP_PLAY_REQUEST_CODE,
            Intent(action).setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val title = getString(titleRes)
        return RemoteAction(
            Icon.createWithResource(this, iconRes),
            title,
            title,
            pendingIntent,
        )
    }

    // endregion

    // region Playback controls

    private fun togglePlayPause() {
        val exoPlayer = player ?: return
        when {
            exoPlayer.playbackState == Player.STATE_ENDED -> {
                exoPlayer.seekTo(0L)
                exoPlayer.play()
            }
            exoPlayer.playWhenReady -> exoPlayer.pause()
            else -> exoPlayer.play()
        }
    }

    private fun seekBy(deltaMs: Long) {
        val exoPlayer = player ?: return
        val upperBound = exoPlayer.duration.takeIf { it > 0L } ?: Long.MAX_VALUE
        val target = (exoPlayer.currentPosition + deltaMs).coerceIn(0L, upperBound)
        exoPlayer.seekTo(target)
        updateProgress()
    }

    private fun updatePlayPauseButton() {
        binding.playPauseButton.setImageResource(
            if (shouldShowPauseAction()) R.drawable.ic_pause else R.drawable.ic_play_arrow,
        )
    }

    private fun shouldShowPauseAction(): Boolean {
        val exoPlayer = player ?: return false
        return exoPlayer.playWhenReady &&
            exoPlayer.playbackState != Player.STATE_ENDED &&
            exoPlayer.playbackState != Player.STATE_IDLE
    }

    private fun updateProgress() {
        val exoPlayer = player ?: return
        val duration = exoPlayer.duration.takeIf { it > 0L } ?: 0L
        val index = exoPlayer.currentMediaItemIndex
        if (index in request.items.indices) {
            lastPositionsByIndex[index] = exoPlayer.currentPosition.coerceAtLeast(0L)
            if (duration > 0L) durationsByIndex[index] = duration
        }
        binding.timeBar.setDuration(duration)
        binding.durationText.text = PlayerGesturePolicy.formatTime(duration)
        if (!scrubbing) {
            val position = exoPlayer.currentPosition.coerceAtLeast(0L)
            binding.timeBar.setPosition(position)
            binding.positionText.text = PlayerGesturePolicy.formatTime(position)
        }
        binding.timeBar.setBufferedPosition(exoPlayer.bufferedPosition.coerceAtLeast(0L))
    }

    private fun showSpeedDialog() {
        val options = PlayerGesturePolicy.SPEED_OPTIONS
        val labels = options.map(PlayerGesturePolicy::formatSpeed).toTypedArray()
        val checkedIndex = options.indexOf(playbackSpeed).takeIf { it >= 0 } ?: options.indexOf(1f)
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_playback_speed)
            .setSingleChoiceItems(labels, checkedIndex) { dialog, which ->
                playbackSpeed = options[which]
                if (!speedBoostActive) player?.setPlaybackSpeed(playbackSpeed)
                updateSpeedButton()
                dialog.dismiss()
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun updateSpeedButton() {
        binding.speedButton.text = PlayerGesturePolicy.formatSpeed(playbackSpeed)
        binding.speedButton.setTextColor(
            getColor(if (playbackSpeed == 1f) android.R.color.white else R.color.color_secondary),
        )
    }

    private fun togglePlaybackMode() {
        playbackMode = VideoPlaybackModePolicy.next(playbackMode, request.items.size)
        if (playbackMode == VideoPlaybackMode.REPEAT_ONE && sleepTimerMode == SleepTimerMode.END_OF_VIDEO) {
            sleepTimerMode = SleepTimerMode.OFF
            sleepTimerDeadlineElapsedRealtimeMs = 0L
            mainHandler.removeCallbacks(sleepTimerRunnable)
            Toast.makeText(this, R.string.sleep_timer_cancelled, Toast.LENGTH_SHORT).show()
        }
        player?.let(::applyPlaybackMode)
        updateToolbarMenu()
        showOsd(
            playbackModeIcon(),
            getString(playbackModeTitle()),
        )
        postponeControlsAutoHide()
    }

    private fun applyPlaybackMode(exoPlayer: ExoPlayer) {
        exoPlayer.shuffleModeEnabled = playbackMode == VideoPlaybackMode.SHUFFLE
        exoPlayer.repeatMode = if (playbackMode == VideoPlaybackMode.REPEAT_ONE) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    @DrawableRes
    private fun playbackModeIcon(): Int = when (playbackMode) {
        VideoPlaybackMode.SEQUENCE -> R.drawable.ic_sequence
        VideoPlaybackMode.SHUFFLE -> R.drawable.ic_shuffle
        VideoPlaybackMode.REPEAT_ONE -> R.drawable.ic_repeat_one
    }

    private fun playbackModeTitle(): Int = when (playbackMode) {
        VideoPlaybackMode.SEQUENCE -> R.string.playback_mode_sequence
        VideoPlaybackMode.SHUFFLE -> R.string.playback_mode_shuffle
        VideoPlaybackMode.REPEAT_ONE -> R.string.repeat_one
    }

    private fun showPlaylistDialog() {
        if (request.items.size <= 1) return
        mainHandler.removeCallbacks(hideControlsRunnable)
        val checkedIndex = player?.currentMediaItemIndex
            ?.takeIf { it in request.items.indices }
            ?: activeMediaItemIndex
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.video_playlist_count, request.items.size))
            .setSingleChoiceItems(
                request.items.map(AndroidPlaybackItem::displayName).toTypedArray(),
                checkedIndex,
            ) { dialog, which ->
                player?.seekToDefaultPosition(which)
                player?.play()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun onTrackAvailabilityChanged(hasAudioChoices: Boolean, hasSubtitles: Boolean) {
        hasAudioTrackChoices = hasAudioChoices
        hasSubtitleTracks = hasSubtitles
        updateToolbarMenu()
    }

    private fun updateToolbarMenu() {
        val menu = binding.toolbar.menu
        menu.findItem(R.id.action_audio_track)?.apply {
            isVisible = hasAudioTrackChoices
            isEnabled = player != null
            icon?.mutate()?.setTint(getColor(android.R.color.white))
        }
        menu.findItem(R.id.action_subtitle_track)?.apply {
            isVisible = hasSubtitleTracks
            isEnabled = player != null
            icon?.mutate()?.setTint(
                getColor(
                    if (trackController.selectedSubtitleKey == null) {
                        android.R.color.white
                    } else {
                        R.color.color_secondary
                    },
                ),
            )
        }
        menu.findItem(R.id.action_repeat)?.apply {
            isEnabled = player != null
            title = getString(playbackModeTitle())
            setIcon(playbackModeIcon())
            icon?.mutate()?.setTint(
                getColor(
                    if (playbackMode == VideoPlaybackMode.SEQUENCE) {
                        android.R.color.white
                    } else {
                        R.color.color_secondary
                    },
                ),
            )
        }
        menu.findItem(R.id.action_playlist)?.apply {
            isVisible = request.items.size > 1
            isEnabled = player != null
        }
        menu.findItem(R.id.action_previous_video)?.apply {
            isVisible = request.items.size > 1
            isEnabled = player?.hasPreviousMediaItem() == true
        }
        menu.findItem(R.id.action_next_video)?.apply {
            isVisible = request.items.size > 1
            isEnabled = player?.hasNextMediaItem() == true
        }
        menu.findItem(R.id.action_picture_in_picture)?.apply {
            isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            isEnabled = canEnterPictureInPicture()
        }
        menu.findItem(R.id.action_sleep_timer)?.apply {
            isEnabled = player != null
            title = sleepTimerMenuTitle()
            icon?.mutate()?.setTint(
                getColor(
                    if (sleepTimerMode == SleepTimerMode.OFF) {
                        android.R.color.white
                    } else {
                        R.color.color_secondary
                    },
                ),
            )
        }
        menu.findItem(R.id.action_screenshot)?.apply {
            isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            isEnabled = player != null && !screenshotInProgress
        }
        menu.findItem(R.id.action_gesture_settings)?.isEnabled = player != null
        menu.findItem(R.id.action_video_info)?.isEnabled = player != null
    }

    private fun showSleepTimerDialog() {
        val modes = SleepTimerMode.entries
        val labels = modes.map(::sleepTimerModeLabel).toTypedArray()
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_sleep_timer)
            .setSingleChoiceItems(labels, modes.indexOf(sleepTimerMode)) { dialog, which ->
                setSleepTimer(modes[which])
                dialog.dismiss()
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun sleepTimerModeLabel(mode: SleepTimerMode): String = getString(
        when (mode) {
            SleepTimerMode.OFF -> R.string.sleep_timer_off
            SleepTimerMode.MINUTES_15 -> R.string.sleep_timer_15_minutes
            SleepTimerMode.MINUTES_30 -> R.string.sleep_timer_30_minutes
            SleepTimerMode.MINUTES_45 -> R.string.sleep_timer_45_minutes
            SleepTimerMode.MINUTES_60 -> R.string.sleep_timer_60_minutes
            SleepTimerMode.END_OF_VIDEO -> R.string.sleep_timer_end_of_video
        },
    )

    private fun setSleepTimer(mode: SleepTimerMode) {
        val previousMode = sleepTimerMode
        sleepTimerMode = mode
        sleepTimerDeadlineElapsedRealtimeMs = SleepTimerPolicy.deadlineElapsedRealtimeMs(
            mode,
            SystemClock.elapsedRealtime(),
        ) ?: 0L
        if (mode == SleepTimerMode.END_OF_VIDEO && playbackMode == VideoPlaybackMode.REPEAT_ONE) {
            playbackMode = VideoPlaybackMode.SEQUENCE
            player?.let(::applyPlaybackMode)
        }
        scheduleSleepTimer()
        val message = when {
            mode.durationMinutes != null -> getString(
                R.string.sleep_timer_set_minutes,
                mode.durationMinutes,
            )
            mode == SleepTimerMode.END_OF_VIDEO -> getString(R.string.sleep_timer_end_of_video)
            previousMode != SleepTimerMode.OFF -> getString(R.string.sleep_timer_cancelled)
            else -> null
        }
        message?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
    }

    private fun scheduleSleepTimer() {
        mainHandler.removeCallbacks(sleepTimerRunnable)
        if (sleepTimerMode.durationMinutes == null) {
            updateToolbarMenu()
            return
        }
        updateSleepTimer()
    }

    private fun updateSleepTimer() {
        mainHandler.removeCallbacks(sleepTimerRunnable)
        if (sleepTimerMode.durationMinutes == null || sleepTimerDeadlineElapsedRealtimeMs <= 0L) {
            updateToolbarMenu()
            return
        }
        val remaining = SleepTimerPolicy.remainingMs(
            sleepTimerDeadlineElapsedRealtimeMs,
            SystemClock.elapsedRealtime(),
        )
        if (remaining == 0L) {
            finishSleepTimer()
            return
        }
        updateToolbarMenu()
        mainHandler.postDelayed(sleepTimerRunnable, remaining.coerceAtMost(SLEEP_TIMER_TICK_MS))
    }

    private fun finishSleepTimer() {
        player?.pause()
        resumePlayWhenReady = false
        sleepTimerMode = SleepTimerMode.OFF
        sleepTimerDeadlineElapsedRealtimeMs = 0L
        mainHandler.removeCallbacks(sleepTimerRunnable)
        updateToolbarMenu()
        showControls()
        Toast.makeText(this, R.string.sleep_timer_finished, Toast.LENGTH_SHORT).show()
    }

    private fun sleepTimerMenuTitle(): String = when {
        sleepTimerMode == SleepTimerMode.END_OF_VIDEO -> getString(
            R.string.gesture_setting_value,
            getString(R.string.action_sleep_timer),
            getString(R.string.sleep_timer_end_of_video),
        )
        sleepTimerMode.durationMinutes != null && sleepTimerDeadlineElapsedRealtimeMs > 0L -> {
            getString(
                R.string.sleep_timer_active,
                SleepTimerPolicy.remainingMinutes(
                    sleepTimerDeadlineElapsedRealtimeMs,
                    SystemClock.elapsedRealtime(),
                ),
            )
        }
        else -> getString(R.string.action_sleep_timer)
    }

    private fun captureCurrentFrame() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        if (screenshotInProgress) {
            Toast.makeText(this, R.string.screenshot_in_progress, Toast.LENGTH_SHORT).show()
            return
        }
        val exoPlayer = player
        val capture = frameCapture
        if (exoPlayer == null || capture == null || exoPlayer.videoSize.width <= 0) {
            Toast.makeText(this, R.string.screenshot_not_ready, Toast.LENGTH_SHORT).show()
            return
        }
        screenshotInProgress = true
        updateToolbarMenu()
        capture.capture(binding.playerView) { result ->
            screenshotInProgress = false
            updateToolbarMenu()
            val message = when (result) {
                is FrameCaptureResult.Success -> R.string.screenshot_saved
                FrameCaptureResult.VideoNotReady -> R.string.screenshot_not_ready
                FrameCaptureResult.CopyFailed,
                FrameCaptureResult.SaveFailed,
                -> R.string.screenshot_save_failed
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            postponeControlsAutoHide()
        }
    }

    private fun showGestureSettingsDialog() {
        val items = arrayOf(
            getString(
                R.string.gesture_setting_value,
                getString(R.string.gesture_sensitivity),
                gestureSensitivityLabel(gestureSettings.sensitivity),
            ),
            getString(
                R.string.gesture_setting_value,
                getString(R.string.gesture_double_tap_seek),
                getString(
                    R.string.gesture_double_tap_seek_seconds,
                    gestureSettings.doubleTapSeekMs / 1_000L,
                ),
            ),
        )
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_gesture_settings)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showGestureSensitivityDialog()
                    1 -> showDoubleTapSeekDialog()
                }
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun showGestureSensitivityDialog() {
        val options = GestureSensitivity.entries
        val labels = options.map(::gestureSensitivityLabel).toTypedArray()
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.gesture_sensitivity)
            .setSingleChoiceItems(labels, options.indexOf(gestureSettings.sensitivity)) { dialog, which ->
                val sensitivity = options[which]
                gestureSettings = gestureSettings.copy(sensitivity = sensitivity)
                settingsStore.writeSensitivity(sensitivity)
                dialog.dismiss()
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun gestureSensitivityLabel(sensitivity: GestureSensitivity): String = getString(
        when (sensitivity) {
            GestureSensitivity.LOW -> R.string.gesture_sensitivity_low
            GestureSensitivity.NORMAL -> R.string.gesture_sensitivity_normal
            GestureSensitivity.HIGH -> R.string.gesture_sensitivity_high
        },
    )

    private fun showDoubleTapSeekDialog() {
        val options = PlayerGesturePolicy.DOUBLE_TAP_SEEK_OPTIONS_MS
        val labels = options.map { milliseconds ->
            getString(R.string.gesture_double_tap_seek_seconds, milliseconds / 1_000L)
        }.toTypedArray()
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.gesture_double_tap_seek)
            .setSingleChoiceItems(labels, options.indexOf(gestureSettings.doubleTapSeekMs)) { dialog, which ->
                val milliseconds = options[which]
                gestureSettings = gestureSettings.copy(doubleTapSeekMs = milliseconds)
                settingsStore.writeDoubleTapSeekMs(milliseconds)
                dialog.dismiss()
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun showVideoInfoDialog() {
        val exoPlayer = player ?: return
        val videoFormat = exoPlayer.videoFormat
        val audioFormat = exoPlayer.audioFormat
        val unknown = getString(R.string.media_info_unknown)
        val resolution = videoFormat
            ?.takeIf { it.width > 0 && it.height > 0 }
            ?.let { getString(R.string.media_info_resolution_value, it.width, it.height) }
            ?: unknown
        val frameRate = videoFormat
            ?.frameRate
            ?.let(PlaybackMediaPolicy::formatFrameRate)
            ?.let { getString(R.string.media_info_frame_rate_value, it) }
            ?: unknown
        val videoCodec = PlaybackMediaPolicy.formatCodec(
            videoFormat?.sampleMimeType,
            videoFormat?.codecs,
        ) ?: unknown
        val audioCodec = PlaybackMediaPolicy.formatCodec(
            audioFormat?.sampleMimeType,
            audioFormat?.codecs,
        ) ?: unknown
        val item = currentItem()
        val containerMime = videoFormat?.containerMimeType
            ?: audioFormat?.containerMimeType
            ?: item.mimeType
        val declaredSize = item.declaredSize.takeIf { it >= 0L }
            ?.let { Formatter.formatFileSize(this, it) }
            ?: unknown
        val rows = listOf(
            R.string.media_info_file_name to item.displayName,
            R.string.media_info_declared_size to declaredSize,
            R.string.media_info_container_mime to containerMime,
            R.string.media_info_resolution to resolution,
            R.string.media_info_frame_rate to frameRate,
            R.string.media_info_video_codec to videoCodec,
            R.string.media_info_audio_codec to audioCodec,
        ).joinToString("\n") { (label, value) ->
            getString(R.string.media_info_row, getString(label), value)
        }
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_video_info)
            .setMessage(rows)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener { postponeControlsAutoHide() }
            .show()
    }

    private fun applyResizeMode(showOsd: Boolean) {
        binding.playerView.resizeMode = when (resizeMode) {
            PlayerResizeMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            PlayerResizeMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            PlayerResizeMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
        if (showOsd) {
            val label = when (resizeMode) {
                PlayerResizeMode.FIT -> R.string.resize_mode_fit
                PlayerResizeMode.FILL -> R.string.resize_mode_fill
                PlayerResizeMode.ZOOM -> R.string.resize_mode_zoom
            }
            showOsd(R.drawable.ic_aspect_ratio, getString(label))
        }
    }

    private fun applyManualZoom() {
        binding.playerView.apply {
            if (width > 0 && height > 0) {
                pivotX = width * zoomPivotXFraction
                pivotY = height * zoomPivotYFraction
            }
            scaleX = manualZoomScale
            scaleY = manualZoomScale
        }
    }

    private fun resetManualZoom(showOsd: Boolean): Boolean {
        if (abs(manualZoomScale - PlayerGesturePolicy.DEFAULT_ZOOM_SCALE) < ZOOM_EPSILON) {
            return false
        }
        manualZoomScale = PlayerGesturePolicy.DEFAULT_ZOOM_SCALE
        zoomPivotXFraction = 0.5f
        zoomPivotYFraction = 0.5f
        applyManualZoom()
        if (showOsd) {
            showOsd(
                R.drawable.ic_aspect_ratio,
                getString(
                    R.string.zoom_scale_value,
                    PlayerGesturePolicy.formatSpeed(manualZoomScale),
                ),
            )
        }
        return true
    }

    private fun applyOrientationMode(showOsd: Boolean) {
        requestedOrientation = when (orientationMode) {
            PlayerOrientationMode.AUTO -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            PlayerOrientationMode.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            PlayerOrientationMode.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
        if (showOsd) {
            val label = when (orientationMode) {
                PlayerOrientationMode.AUTO -> R.string.orientation_auto
                PlayerOrientationMode.LANDSCAPE -> R.string.orientation_landscape
                PlayerOrientationMode.PORTRAIT -> R.string.orientation_portrait
            }
            showOsd(R.drawable.ic_screen_rotation, getString(label))
        }
    }

    private fun applySuggestedOrientationIfNeeded(videoSize: VideoSize) {
        if (orientationSuggestionApplied || orientationManuallyChanged ||
            orientationMode != PlayerOrientationMode.AUTO
        ) {
            return
        }
        val suggestion = PlaybackMediaPolicy.suggestedOrientation(
            width = videoSize.width,
            height = videoSize.height,
            rotationDegrees = 0,
            pixelWidthHeightRatio = videoSize.pixelWidthHeightRatio,
        )
        requestedOrientation = when (suggestion) {
            VideoOrientationSuggestion.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            VideoOrientationSuggestion.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            VideoOrientationSuggestion.NONE -> return
        }
        orientationSuggestionApplied = true
    }

    // endregion

    // region Gestures

    private val gestureHost = object : PlayerGestureController.Host {

        override fun isGestureBlocked(): Boolean =
            player == null || binding.playbackErrorPanel.isVisible || inPictureInPicture

        override fun isLocked(): Boolean = controlsLocked

        override fun onLockedSingleTap() {
            revealUnlockButton()
        }

        override fun onSingleTap() {
            if (controlsVisible) hideControls() else showControls()
        }

        override fun onDoubleTap(zone: DoubleTapZone) {
            val exoPlayer = player ?: return
            if (resetManualZoom(showOsd = true)) return
            when (zone) {
                DoubleTapZone.REWIND -> {
                    seekBy(-gestureSettings.doubleTapSeekMs)
                    showSeekOsd(R.drawable.ic_fast_rewind, exoPlayer)
                }
                DoubleTapZone.FORWARD -> {
                    seekBy(gestureSettings.doubleTapSeekMs)
                    showSeekOsd(R.drawable.ic_fast_forward, exoPlayer)
                }
                DoubleTapZone.TOGGLE_PLAYBACK -> togglePlayPause()
            }
        }

        override fun onSeekDragStart(): Boolean {
            val exoPlayer = player ?: return false
            if (exoPlayer.duration <= 0L) return false
            seekDragStartPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            seekDragTarget = seekDragStartPosition
            mainHandler.removeCallbacks(hideControlsRunnable)
            return true
        }

        override fun onSeekDragUpdate(totalDragPx: Float, viewWidthPx: Int) {
            val exoPlayer = player ?: return
            val duration = exoPlayer.duration.takeIf { it > 0L } ?: return
            seekDragTarget = PlayerGesturePolicy.seekDragTarget(
                seekDragStartPosition,
                totalDragPx,
                viewWidthPx,
                duration,
                gestureSettings.sensitivity,
            )
            val icon = if (seekDragTarget >= seekDragStartPosition) {
                R.drawable.ic_fast_forward
            } else {
                R.drawable.ic_fast_rewind
            }
            showOsd(
                icon,
                "${PlayerGesturePolicy.formatTime(seekDragTarget)} / ${PlayerGesturePolicy.formatTime(duration)}",
                sticky = true,
            )
        }

        override fun onSeekDragEnd(commit: Boolean) {
            if (commit) player?.seekTo(seekDragTarget)
            updateProgress()
            hideOsdSoon()
            postponeControlsAutoHide()
        }

        override fun onVerticalDragStart(zone: VerticalDragZone) {
            verticalDragStartFraction = when (zone) {
                VerticalDragZone.BRIGHTNESS -> currentBrightnessFraction()
                VerticalDragZone.VOLUME -> currentVolumeFraction()
            }
        }

        override fun onVerticalDragUpdate(zone: VerticalDragZone, totalDragUpPx: Float, viewHeightPx: Int) {
            val fraction = PlayerGesturePolicy.draggedFraction(
                verticalDragStartFraction,
                totalDragUpPx,
                viewHeightPx,
                gestureSettings.sensitivity,
            )
            when (zone) {
                VerticalDragZone.BRIGHTNESS -> {
                    applyBrightnessFraction(fraction)
                    showOsd(R.drawable.ic_brightness, PlayerGesturePolicy.percentLabel(fraction), sticky = true)
                }
                VerticalDragZone.VOLUME -> {
                    val applied = applyVolumeFraction(fraction)
                    showOsd(R.drawable.ic_volume_up, PlayerGesturePolicy.percentLabel(applied), sticky = true)
                }
            }
        }

        override fun onVerticalDragEnd() {
            hideOsdSoon()
        }

        override fun onScaleStart(): Boolean {
            if (player == null) return false
            resizeMode = PlayerResizeMode.FIT
            applyResizeMode(showOsd = false)
            mainHandler.removeCallbacks(hideControlsRunnable)
            return true
        }

        override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
            manualZoomScale = PlayerGesturePolicy.zoomScale(
                manualZoomScale,
                scaleFactor,
                gestureSettings.sensitivity,
            )
            val width = binding.gestureArea.width.takeIf { it > 0 } ?: 1
            val height = binding.gestureArea.height.takeIf { it > 0 } ?: 1
            zoomPivotXFraction = (focusX / width).coerceIn(0f, 1f)
            zoomPivotYFraction = (focusY / height).coerceIn(0f, 1f)
            applyManualZoom()
            showOsd(
                R.drawable.ic_aspect_ratio,
                getString(
                    R.string.zoom_scale_value,
                    PlayerGesturePolicy.formatSpeed(manualZoomScale),
                ),
                sticky = true,
            )
        }

        override fun onScaleEnd() {
            hideOsdSoon()
            postponeControlsAutoHide()
        }

        override fun onLongPressStart(): Boolean {
            val exoPlayer = player ?: return false
            if (!exoPlayer.isPlaying) return false
            speedBoostActive = true
            exoPlayer.setPlaybackSpeed(PlayerGesturePolicy.LONG_PRESS_BOOST_SPEED)
            showOsd(
                R.drawable.ic_speed,
                PlayerGesturePolicy.formatSpeed(PlayerGesturePolicy.LONG_PRESS_BOOST_SPEED),
                sticky = true,
            )
            return true
        }

        override fun onLongPressEnd() {
            if (!speedBoostActive) return
            speedBoostActive = false
            player?.setPlaybackSpeed(playbackSpeed)
            hideOsdSoon()
        }
    }

    private fun showSeekOsd(@DrawableRes iconRes: Int, exoPlayer: ExoPlayer) {
        val duration = exoPlayer.duration.takeIf { it > 0L } ?: return
        showOsd(
            iconRes,
            "${PlayerGesturePolicy.formatTime(exoPlayer.currentPosition)} / " +
                PlayerGesturePolicy.formatTime(duration),
        )
    }

    // endregion

    // region Brightness and volume

    private fun currentBrightnessFraction(): Float {
        val current = window.attributes.screenBrightness
        if (current >= 0f) return current.coerceIn(0f, 1f)
        val system = runCatching {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        }.getOrDefault(128)
        return (system / 255f).coerceIn(0f, 1f)
    }

    private fun applyBrightnessFraction(fraction: Float) {
        window.attributes = window.attributes.apply {
            screenBrightness = fraction.coerceIn(PlayerGesturePolicy.MIN_BRIGHTNESS, 1f)
        }
    }

    private fun currentVolumeFraction(): Float {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return 0f
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / max
    }

    private fun applyVolumeFraction(fraction: Float): Float {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return 0f
        val index = (fraction * max).roundToInt().coerceIn(0, max)
        runCatching { audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0) }
        return index.toFloat() / max
    }

    // endregion

    private fun showOsd(@DrawableRes iconRes: Int, text: String, sticky: Boolean = false) {
        binding.osdIcon.setImageResource(iconRes)
        binding.osdText.text = text
        binding.osdPanel.isVisible = true
        mainHandler.removeCallbacks(hideOsdRunnable)
        if (!sticky) mainHandler.postDelayed(hideOsdRunnable, OSD_HIDE_MS)
    }

    private fun hideOsdSoon() {
        mainHandler.removeCallbacks(hideOsdRunnable)
        mainHandler.postDelayed(hideOsdRunnable, OSD_LINGER_MS)
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
        const val STATE_SPEED = "speed"
        const val STATE_RESIZE_MODE = "resize_mode"
        const val STATE_ORIENTATION_MODE = "orientation_mode"
        const val STATE_LOCKED = "locked"
        const val STATE_PLAYBACK_MODE = "playback_mode"
        const val STATE_MEDIA_ITEM_INDEX = "media_item_index"
        const val STATE_SLEEP_TIMER_MODE = "sleep_timer_mode"
        const val STATE_SLEEP_TIMER_DEADLINE = "sleep_timer_deadline"
        const val STATE_ZOOM_SCALE = "zoom_scale"
        const val STATE_ZOOM_PIVOT_X = "zoom_pivot_x"
        const val STATE_ZOOM_PIVOT_Y = "zoom_pivot_y"
        const val STATE_ORIENTATION_MANUAL = "orientation_manual"
        const val STATE_AUDIO_GROUP = "audio_group"
        const val STATE_AUDIO_TRACK = "audio_track"
        const val STATE_SUBTITLE_GROUP = "subtitle_group"
        const val STATE_SUBTITLE_TRACK = "subtitle_track"
        const val ACTION_PIP_PLAY_PREFIX =
            "io.github.supermonster003.autojs6.plugin.videoplayer.action.PIP_PLAY"
        const val ACTION_PIP_PAUSE_PREFIX =
            "io.github.supermonster003.autojs6.plugin.videoplayer.action.PIP_PAUSE"
        const val PIP_PLAY_REQUEST_CODE = 6_001
        const val PIP_PAUSE_REQUEST_CODE = 6_002
        const val PROGRESS_INTERVAL_MS = 500L
        const val OSD_HIDE_MS = 800L
        const val OSD_LINGER_MS = 400L
        const val UNLOCK_BUTTON_HIDE_MS = 3_000L
        const val SLEEP_TIMER_TICK_MS = 60_000L
        const val SCRUB_THUMBNAIL_WIDTH_PX = 320
        const val SCRUB_THUMBNAIL_HEIGHT_PX = 180
        const val SCRUB_PREVIEW_MARGIN_DP = 8f
        const val ZOOM_EPSILON = 0.001f
    }
}
