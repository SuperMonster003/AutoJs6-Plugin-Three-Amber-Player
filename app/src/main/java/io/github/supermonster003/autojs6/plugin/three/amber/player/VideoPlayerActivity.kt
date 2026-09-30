package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.format.Formatter
import android.util.Rational
import android.view.Display
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
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
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.TimeBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivityVideoPlayerBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.BottomSheetVideoQueueBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.DialogSubtitleOffsetBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.settings.AppPreferenceStore
import io.github.supermonster003.autojs6.plugin.three.amber.player.theme.VideoThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.three.amber.player.theme.VideoThemeViewStyler
import io.github.supermonster003.autojs6.plugin.three.amber.player.theme.VideoThemedActivity
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class ManualSubtitleDocument(
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val size: Long,
)

private data class SessionSubtitleSource(
    val sourceUri: Uri,
    val displayName: String,
    val mimeType: String,
    val languageTag: String?,
    val stableSuffix: String,
)

private sealed interface ManualSubtitleResolution {
    data class Success(val document: ManualSubtitleDocument) : ManualSubtitleResolution
    data object InvalidType : ManualSubtitleResolution
    data object TooLarge : ManualSubtitleResolution
    data object Unreadable : ManualSubtitleResolution
}

@androidx.annotation.OptIn(UnstableApi::class)
class VideoPlayerActivity : VideoThemedActivity(), BackgroundPlaybackClient {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var request: AndroidPlaybackRequest
    private lateinit var positionStore: PlaybackPositionStore
    private lateinit var appPreferenceStore: AppPreferenceStore
    private lateinit var settingsStore: PlayerSettingsStore
    private lateinit var audioManager: AudioManager
    private lateinit var insetsController: WindowInsetsControllerCompat
    private lateinit var trackController: PlayerTrackController
    private lateinit var systemIntegration: PlayerSystemIntegration
    private var backgroundAudioEnabled = false
    private var hostProgressClient: HostPlaybackProgressClient? = null
    private var queueDialog: BottomSheetDialog? = null
    private var queueSheetBinding: BottomSheetVideoQueueBinding? = null
    private var queueAdapter: VideoQueueAdapter? = null

    private var player: ExoPlayer? = null
    private var frameCapture: PlayerFrameCapture? = null
    private var transformedTextureView: TextureView? = null
    private var transformedTextureAttachedToPlayer = false
    private var loudnessEnhancer = PlayerLoudnessEnhancer()
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
    private lateinit var subtitleStyle: SubtitleStyleSettings
    private var sleepTimerMode = SleepTimerMode.OFF
    private var sleepTimerDeadlineElapsedRealtimeMs = 0L
    private var autoAdvancePromptDeadlineElapsedRealtimeMs = 0L
    private var manualZoomScale = PlayerGesturePolicy.DEFAULT_ZOOM_SCALE
    private var zoomPivotXFraction = 0.5f
    private var zoomPivotYFraction = 0.5f
    private var mirrorMode = VideoMirrorMode.NONE
    private var volumeBoostLevel = VolumeBoostLevel.OFF
    private var volumeBoostWarningAcknowledged = false
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
    private var subtitleSourceRegistry = SubtitleSourceRegistry()
    private val subtitleEncodingWarnings = Collections.synchronizedSet(HashSet<String>())
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var backgroundPlaybackSessionId: String
    private var backgroundOwnershipTransferred = false
    private val touchExplorationStateListener = AccessibilityManager.TouchExplorationStateChangeListener {
        if (!::binding.isInitialized) return@TouchExplorationStateChangeListener
        if (it) {
            if (controlsLocked) revealUnlockButton() else showControls()
        } else {
            postponeControlsAutoHide()
        }
    }
    private val unsupportedHdrWarnings = HashSet<String>()
    private var subtitleOffsetMs = 0L
    private var subtitleRouteRevision = 0L
    private var manualSubtitle: ManualSubtitleDocument? = null
    private var manualSubtitleMediaIndex = -1
    private var resolvingManualSubtitle = false
    private var reloadingSubtitleSources = false
    private var abLoopState = AbLoopState()
    private var frameRepeatDirection = 0
    @Volatile
    private var xvidFourCcCompatibilityApplied = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private val hideControlsRunnable = Runnable { hideControls() }
    private val hideOsdRunnable = Runnable { binding.osdPanel.isVisible = false }
    private val hideUnlockRunnable = Runnable { binding.unlockButton.isVisible = false }
    private val sleepTimerRunnable = Runnable { updateSleepTimer() }
    private val autoAdvancePromptRunnable = Runnable { updateAutoAdvancePrompt() }
    private val frameRepeatRunnable = object : Runnable {
        override fun run() {
            val direction = frameRepeatDirection
            if (direction == 0) return
            stepFrame(direction)
            mainHandler.postDelayed(this, FRAME_REPEAT_INTERVAL_MS)
        }
    }
    private val subtitlePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let(::resolveManualSubtitle)
    }
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
            mainHandler.postDelayed(
                this,
                if (abLoopState.active) AB_LOOP_PROGRESS_INTERVAL_MS else PROGRESS_INTERVAL_MS,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestedBackgroundSessionId = intent.getStringExtra(
            BackgroundPlaybackService.EXTRA_SESSION_ID,
        )
        val backgroundRequest = requestedBackgroundSessionId
            ?.takeIf {
                intent.action == BackgroundPlaybackService.ACTION_RESUME_BACKGROUND_PLAYBACK
            }
            ?.let(BackgroundPlaybackCoordinator::playbackRequest)
        val resolvedRequest = backgroundRequest ?: AndroidVideoIntentPolicy.resolveInternal(intent)
        if (resolvedRequest == null) {
            Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        request = resolvedRequest
        requestAccepted = true
        backgroundPlaybackSessionId = requestedBackgroundSessionId ?: UUID.randomUUID().toString()
        BackgroundPlaybackCoordinator.stopIfDifferent(backgroundPlaybackSessionId)
        BackgroundPlaybackCoordinator.registerClient(backgroundPlaybackSessionId, this)
        activeMediaItemIndex = request.startIndex
        resumeMediaItemIndex = request.startIndex
        hostProgressClient = request.hostSession?.let { session ->
            HostPlaybackProgressClient(session)
        }
        positionStore = PlaybackPositionStore(this)
        appPreferenceStore = AppPreferenceStore(this)
        if (!appPreferenceStore.rememberPlaybackPosition) positionStore.clearAll()
        settingsStore = PlayerSettingsStore(this)
        backgroundAudioEnabled = settingsStore.continueAudioInBackground &&
            BackgroundPlaybackPermissionPolicy.canPostControls(this)
        if (settingsStore.continueAudioInBackground && !backgroundAudioEnabled) {
            settingsStore.setContinueAudioInBackground(false)
            BackgroundPlaybackCoordinator.stopForDisabledSetting()
        }
        playbackMode = VideoPlaybackModePolicy.normalizeForItemCount(
            settingsStore.readPlaybackMode(),
            request.items.size,
        )
        gestureSettings = settingsStore.read()
        if (backgroundAudioEnabled) {
            BackgroundPlaybackCoordinator.prepare(this, request.displayName)
        }
        subtitleStyle = settingsStore.readSubtitleStyle()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.addTouchExplorationStateChangeListener(touchExplorationStateListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            frameCapture = PlayerFrameCapture(this)
        }

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyPlayerTheme()
        SubtitleStyleApplier.apply(binding.playerView.subtitleView, subtitleStyle)
        trackController = PlayerTrackController(
            activity = this,
            playerProvider = { player },
            onAvailabilityChanged = ::onTrackAvailabilityChanged,
            onLoadSubtitleRequested = ::launchSubtitlePicker,
            onSubtitleOffsetRequested = ::showSubtitleOffsetDialog,
            subtitleOffsetButtonLabel = { enabled ->
                if (enabled) {
                    getString(
                        R.string.subtitle_offset_value,
                        SubtitleTimingPolicy.formatOffset(subtitleOffsetMs),
                    )
                } else {
                    getString(R.string.subtitle_offset_external_only)
                }
            },
            onSubtitleSelectionChanged = ::updateToolbarMenu,
            onDialogDismissed = ::postponeControlsAutoHide,
        )
        systemIntegration = PlayerSystemIntegration(
            activity = this,
            notificationId = BackgroundPlaybackService.NOTIFICATION_ID.takeIf {
                backgroundAudioEnabled
            },
            notificationChannelId = if (backgroundAudioEnabled) {
                BackgroundPlaybackService.NOTIFICATION_CHANNEL_ID
            } else {
                PlayerSystemIntegration.NOTIFICATION_CHANNEL_ID
            },
        )
        restorePlaybackState(savedInstanceState)
        setUpWindow()
        setUpControls()
        registerPictureInPictureActions()
        applyResizeMode(showOsd = false)
        binding.playerView.post { applyVideoTransform() }
        applyOrientationMode(showOsd = false)
        applyLockState()
        scheduleSleepTimer()
    }

    override fun onStart() {
        super.onStart()
        if (requestAccepted) {
            val backgroundPlayback = BackgroundPlaybackCoordinator.reclaim(backgroundPlaybackSessionId)
            if (backgroundPlayback != null) {
                adoptBackgroundPlayback(backgroundPlayback)
            } else {
                initializePlayer()
            }
            if (canUseBackgroundAudio() && player != null &&
                !inPictureInPicture &&
                !isInPictureInPictureMode
            ) {
                BackgroundPlaybackCoordinator.prepare(this, request.displayName)
            }
            mainHandler.removeCallbacks(progressRunnable)
            mainHandler.post(progressRunnable)
        }
    }

    override fun onPause() {
        if (requestAccepted && !inPictureInPicture && player != null) {
            handoffToBackgroundPlayback()
        }
        super.onPause()
    }

    override fun onStop() {
        if (requestAccepted && !inPictureInPicture) {
            dismissQueuePanel()
            cancelAutoAdvancePrompt(showFeedback = false)
            mainHandler.removeCallbacks(progressRunnable)
            savePlaybackPosition()
            releasePlayer()
            if (!backgroundOwnershipTransferred) BackgroundPlaybackCoordinator.stopPrepared()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (requestAccepted && player != null) {
            mainHandler.removeCallbacks(progressRunnable)
            savePlaybackPosition()
            releasePlayer()
        }
        dismissQueuePanel()
        if (::backgroundPlaybackSessionId.isInitialized) {
            BackgroundPlaybackCoordinator.unregisterClient(backgroundPlaybackSessionId, this)
        }
        if (::accessibilityManager.isInitialized) {
            accessibilityManager.removeTouchExplorationStateChangeListener(touchExplorationStateListener)
        }
        if (pipReceiverRegistered) {
            unregisterReceiver(pictureInPictureActionReceiver)
            pipReceiverRegistered = false
        }
        hideScrubPreview()
        scrubThumbnailProvider?.close()
        scrubThumbnailProvider = null
        subtitleSourceRegistry.clear()
        loudnessEnhancer.close()
        frameRepeatDirection = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            frameCapture?.close()
            frameCapture = null
        }
        if (requestAccepted && !backgroundOwnershipTransferred) {
            request.hostSession?.let { session -> runCatching { session.close() } }
        }
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onBackgroundPlaybackTerminated() {
        backgroundOwnershipTransferred = true
        if (!isFinishing) finishAndRemoveTask()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.playerView.post { applyVideoTransform() }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (!requestAccepted || !::binding.isInitialized || !::gestureSettings.isInitialized) {
            return super.dispatchKeyEvent(event)
        }
        val key = when (event.keyCode) {
            KeyEvent.KEYCODE_SPACE -> PlaybackInputKey.SPACE
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            -> PlaybackInputKey.ENTER
            KeyEvent.KEYCODE_DPAD_CENTER -> PlaybackInputKey.DPAD_CENTER
            KeyEvent.KEYCODE_DPAD_LEFT -> PlaybackInputKey.DPAD_LEFT
            KeyEvent.KEYCODE_DPAD_RIGHT -> PlaybackInputKey.DPAD_RIGHT
            KeyEvent.KEYCODE_DPAD_UP -> PlaybackInputKey.DPAD_UP
            KeyEvent.KEYCODE_DPAD_DOWN -> PlaybackInputKey.DPAD_DOWN
            else -> PlaybackInputKey.OTHER
        }
        val phase = when (event.action) {
            KeyEvent.ACTION_DOWN -> PlaybackKeyPhase.DOWN
            KeyEvent.ACTION_UP -> PlaybackKeyPhase.UP
            else -> return super.dispatchKeyEvent(event)
        }
        val action = PlayerKeyPolicy.resolve(
            key = key,
            phase = phase,
            focus = playbackFocusTarget(),
            controlsVisible = controlsVisible,
            repeatCount = event.repeatCount,
        )
        return when (action) {
            PlaybackKeyAction.DELEGATE -> super.dispatchKeyEvent(event)
            PlaybackKeyAction.CONSUME -> true
            PlaybackKeyAction.FOCUS_PLAY_PAUSE -> {
                showControls()
                binding.playPauseButton.requestFocus()
                super.dispatchKeyEvent(event)
            }
            PlaybackKeyAction.TOGGLE_PLAY_PAUSE -> {
                showControls()
                togglePlayPause()
                postponeControlsAutoHide()
                true
            }
            PlaybackKeyAction.SEEK_BACK,
            PlaybackKeyAction.SEEK_FORWARD,
            -> {
                showControls()
                val delta = if (action == PlaybackKeyAction.SEEK_BACK) {
                    -gestureSettings.doubleTapSeekMs
                } else {
                    gestureSettings.doubleTapSeekMs
                }
                seekBy(delta)
                player?.let { exoPlayer ->
                    showSeekOsd(
                        if (delta < 0L) R.drawable.ic_fast_rewind else R.drawable.ic_fast_forward,
                        exoPlayer,
                    )
                }
                postponeControlsAutoHide()
                true
            }
        }
    }

    private fun playbackFocusTarget(): PlaybackFocusTarget {
        if (!controlsVisible) return PlaybackFocusTarget.PLAYBACK_SURFACE
        val focused = currentFocus ?: return PlaybackFocusTarget.NONE
        if (!focused.isShown || focused === binding.root || focused === binding.playerView ||
            focused === binding.gestureArea
        ) {
            return PlaybackFocusTarget.PLAYBACK_SURFACE
        }
        return if (focused === binding.timeBar) {
            PlaybackFocusTarget.TIME_BAR
        } else {
            PlaybackFocusTarget.CONTROL
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && canEnterPictureInPicture() &&
            enterPictureInPicture()
        ) {
            BackgroundPlaybackCoordinator.stopPrepared()
            return
        }
        handoffToBackgroundPlayback()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPictureInPicture = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            BackgroundPlaybackCoordinator.stopPrepared()
            controlsVisibleBeforePictureInPicture = controlsVisible
            mainHandler.removeCallbacks(hideControlsRunnable)
            binding.topBar.isVisible = false
            binding.bottomBar.isVisible = false
            binding.unlockButton.isVisible = false
            binding.osdPanel.isVisible = false
            binding.autoAdvancePromptPanel.isVisible = false
            hideScrubPreview()
        } else if (controlsLocked) {
            applyLockState()
        } else if (controlsVisibleBeforePictureInPicture || player?.isPlaying != true) {
            showControls()
        } else {
            hideControls()
        }
        if (!isInPictureInPictureMode && autoAdvancePromptDeadlineElapsedRealtimeMs > 0L) {
            updateAutoAdvancePrompt()
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
        outState.putLong(
            STATE_AUTO_ADVANCE_PROMPT_DEADLINE,
            autoAdvancePromptDeadlineElapsedRealtimeMs,
        )
        outState.putFloat(STATE_ZOOM_SCALE, manualZoomScale)
        outState.putFloat(STATE_ZOOM_PIVOT_X, zoomPivotXFraction)
        outState.putFloat(STATE_ZOOM_PIVOT_Y, zoomPivotYFraction)
        outState.putInt(STATE_MIRROR_MODE, mirrorMode.ordinal)
        outState.putInt(STATE_VOLUME_BOOST_LEVEL, volumeBoostLevel.ordinal)
        outState.putBoolean(STATE_VOLUME_BOOST_WARNING, volumeBoostWarningAcknowledged)
        outState.putBoolean(STATE_ORIENTATION_MANUAL, orientationManuallyChanged)
        outState.putInt(STATE_AUDIO_GROUP, trackController.selectedAudioKey?.groupIndex ?: -1)
        outState.putInt(STATE_AUDIO_TRACK, trackController.selectedAudioKey?.trackIndex ?: -1)
        outState.putInt(STATE_SUBTITLE_GROUP, trackController.selectedSubtitleKey?.groupIndex ?: -1)
        outState.putInt(STATE_SUBTITLE_TRACK, trackController.selectedSubtitleKey?.trackIndex ?: -1)
        outState.putString(STATE_SUBTITLE_ID, trackController.selectedSubtitleId)
        outState.putLong(STATE_SUBTITLE_OFFSET, subtitleOffsetMs)
        outState.putLong(STATE_AB_POINT_A, abLoopState.pointAMs ?: C.TIME_UNSET)
        outState.putLong(STATE_AB_POINT_B, abLoopState.pointBMs ?: C.TIME_UNSET)
        manualSubtitle?.let { subtitle ->
            outState.putInt(STATE_MANUAL_SUBTITLE_MEDIA_INDEX, manualSubtitleMediaIndex)
            outState.putString(STATE_MANUAL_SUBTITLE_URI, subtitle.uri.toString())
            outState.putString(STATE_MANUAL_SUBTITLE_NAME, subtitle.displayName)
            outState.putString(STATE_MANUAL_SUBTITLE_MIME, subtitle.mimeType)
            outState.putLong(STATE_MANUAL_SUBTITLE_SIZE, subtitle.size)
        }
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
            autoAdvancePromptDeadlineElapsedRealtimeMs = savedInstanceState.getLong(
                STATE_AUTO_ADVANCE_PROMPT_DEADLINE,
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
            mirrorMode = enumFromOrdinal<VideoMirrorMode>(
                savedInstanceState.getInt(STATE_MIRROR_MODE, VideoMirrorMode.NONE.ordinal),
            )
            volumeBoostLevel = VolumeBoostPolicy.fromStoredOrdinal(
                savedInstanceState.getInt(
                    STATE_VOLUME_BOOST_LEVEL,
                    VolumeBoostLevel.OFF.ordinal,
                ),
            )
            volumeBoostWarningAcknowledged = savedInstanceState.getBoolean(
                STATE_VOLUME_BOOST_WARNING,
                false,
            )
            orientationManuallyChanged = savedInstanceState.getBoolean(STATE_ORIENTATION_MANUAL, false)
            trackController.restoreSelections(
                audioGroupIndex = savedInstanceState.getInt(STATE_AUDIO_GROUP, -1),
                audioTrackIndex = savedInstanceState.getInt(STATE_AUDIO_TRACK, -1),
                subtitleGroupIndex = savedInstanceState.getInt(STATE_SUBTITLE_GROUP, -1),
                subtitleTrackIndex = savedInstanceState.getInt(STATE_SUBTITLE_TRACK, -1),
                subtitleId = savedInstanceState.getString(STATE_SUBTITLE_ID),
            )
            subtitleOffsetMs = SubtitleTimingPolicy.normalizeOffsetMs(
                savedInstanceState.getLong(STATE_SUBTITLE_OFFSET, 0L),
            )
            abLoopState = AbLoopState(
                pointAMs = savedInstanceState.getLong(STATE_AB_POINT_A, C.TIME_UNSET)
                    .takeUnless { it == C.TIME_UNSET },
                pointBMs = savedInstanceState.getLong(STATE_AB_POINT_B, C.TIME_UNSET)
                    .takeUnless { it == C.TIME_UNSET },
            )
            restoreManualSubtitle(savedInstanceState)
        } else {
            val item = request.items[resumeMediaItemIndex]
            val rememberedPosition = rememberedPosition(item)
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

    private fun applyPlayerTheme() {
        val overlay = VideoThemePaletteGenerator.generate(videoPalette.source, dark = true)
        val control = overlay.playerControl
        val content = overlay.onSurface
        binding.toolbar.setTitleTextColor(content)
        binding.toolbar.navigationIcon = binding.toolbar.navigationIcon?.tinted(content)
        VideoThemeViewStyler.tintMenu(binding.toolbar.menu, control)

        listOf(
            binding.playPauseButton,
            binding.seekBackButton,
            binding.seekForwardButton,
            binding.frameBackButton,
            binding.frameForwardButton,
            binding.resizeButton,
            binding.rotateButton,
            binding.lockButton,
            binding.unlockButton,
        ).forEach { button ->
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(control))
        }
        binding.speedButton.setTextColor(control)
        binding.abLoopButton.setTextColor(control)
        binding.positionText.setTextColor(content)
        binding.durationText.setTextColor(content)
        binding.timeBar.setPlayedColor(control)
        binding.timeBar.setScrubberColor(control)
        binding.timeBar.setBufferedColor(VideoThemePaletteGenerator.withAlpha(control, 0x70))
        binding.timeBar.setUnplayedColor(VideoThemePaletteGenerator.withAlpha(content, 0x3D))
        binding.playerView.findViewById<android.widget.ProgressBar>(androidx.media3.ui.R.id.exo_buffering)
            ?.indeterminateTintList = ColorStateList.valueOf(control)

        val overlaySurface = VideoThemePaletteGenerator.withAlpha(
            overlay.surfaceContainerHighest,
            0xE8,
        )
        binding.osdPanel.backgroundTintList = ColorStateList.valueOf(overlaySurface)
        binding.unlockButton.backgroundTintList = ColorStateList.valueOf(overlaySurface)
        binding.scrubPreviewPanel.backgroundTintList = ColorStateList.valueOf(overlaySurface)
        binding.autoAdvancePromptPanel.setCardBackgroundColor(overlaySurface)
        binding.osdIcon.imageTintList = ColorStateList.valueOf(control)
        binding.osdText.setTextColor(content)
        binding.scrubPreviewText.setTextColor(content)
        binding.autoAdvancePromptText.setTextColor(content)
        binding.cancelAutoAdvanceButton.setTextColor(control)
        binding.cancelAutoAdvanceButton.rippleColor = ColorStateList.valueOf(
            VideoThemePaletteGenerator.withAlpha(control, 0x24),
        )

        binding.playbackErrorPanel.setBackgroundColor(
            VideoThemePaletteGenerator.withAlpha(overlay.errorContainer, 0xF2),
        )
        binding.playbackErrorText.setTextColor(overlay.onErrorContainer)
        binding.retryButton.backgroundTintList = ColorStateList.valueOf(overlay.error)
        binding.retryButton.setTextColor(overlay.onError)
        binding.openWithOtherAppButton.backgroundTintList = ColorStateList.valueOf(overlay.primary)
        binding.openWithOtherAppButton.setTextColor(overlay.onPrimary)
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
                    showQueuePanel()
                    true
                }
                R.id.action_previous_video -> {
                    cancelAutoAdvancePrompt(showFeedback = false)
                    player?.seekToPreviousMediaItem()
                    true
                }
                R.id.action_next_video -> {
                    cancelAutoAdvancePrompt(showFeedback = false)
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
                R.id.action_mirror -> {
                    showMirrorDialog()
                    true
                }
                R.id.action_volume_boost -> {
                    showVolumeBoostDialog()
                    true
                }
                R.id.action_brightness -> {
                    showBrightnessDialog()
                    true
                }
                R.id.action_volume -> {
                    showVolumeDialog()
                    true
                }
                R.id.action_zoom -> {
                    showZoomDialog()
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
            seekBy(-gestureSettings.doubleTapSeekMs)
            postponeControlsAutoHide()
        }
        binding.seekForwardButton.setOnClickListener {
            seekBy(gestureSettings.doubleTapSeekMs)
            postponeControlsAutoHide()
        }
        bindFrameStepButton(binding.frameBackButton, direction = -1)
        bindFrameStepButton(binding.frameForwardButton, direction = 1)
        binding.abLoopButton.setOnClickListener {
            toggleAbLoopPoint()
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
        binding.cancelAutoAdvanceButton.setOnClickListener {
            cancelAutoAdvancePrompt(showFeedback = true)
        }
        binding.retryButton.setOnClickListener { retryPlayback() }
        binding.openWithOtherAppButton.setOnClickListener {
            if (!ExternalPlaybackLauncher.open(this, currentItem())) {
                Toast.makeText(this, R.string.error_no_external_player, Toast.LENGTH_SHORT).show()
            }
        }
        binding.timeBar.addListener(scrubListener)
        updateSpeedButton()
        updatePrecisionControls()
        updateControlAccessibility()
        updateToolbarMenu()
    }

    private fun updateControlAccessibility() {
        if (!::binding.isInitialized || !::gestureSettings.isInitialized) return
        val seconds = (gestureSettings.doubleTapSeekMs / 1_000L).coerceAtLeast(1L)
        binding.seekBackButton.contentDescription = getString(
            R.string.action_seek_back_seconds,
            seconds,
        )
        binding.seekForwardButton.contentDescription = getString(
            R.string.action_seek_forward_seconds,
            seconds,
        )
        updatePlayPauseButton()
        updateSpeedButton()
        binding.resizeButton.contentDescription = getString(
            R.string.control_state_description,
            getString(R.string.action_resize_mode),
            resizeAccessibilityValue(),
        )
        binding.rotateButton.contentDescription = getString(
            R.string.control_state_description,
            getString(R.string.action_rotate_screen),
            getString(orientationModeLabel()),
        )
    }

    private fun initializePlayer() {
        if (player != null) return
        xvidFourCcCompatibilityApplied = false
        val mediaItems = buildMediaItems()
        val activityReference = WeakReference(this)
        val extractorsFactory = XvidCompatibleExtractorsFactory(
            onCompatibilityApplied = {
                activityReference.get()?.xvidFourCcCompatibilityApplied = true
            },
        )
        val upstreamFactory: DataSource.Factory = request.hostSession?.let { session ->
            DefaultDataSource.Factory(
                applicationContext,
                ExplorerSessionDataSource.Factory(
                    session = session,
                    routesByUri = request.hostFileRoutesByUri(),
                ),
            )
        } ?: DefaultDataSource.Factory(applicationContext)
        val mediaSourceFactory = DefaultMediaSourceFactory(
            SubtitleTransformingDataSource.Factory(
                upstreamFactory = upstreamFactory,
                registry = subtitleSourceRegistry,
                onUncertainEncoding = { route ->
                    activityReference.get()?.showSubtitleEncodingWarning(route)
                },
            ),
            extractorsFactory,
        )
        player = ExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .setSeekBackIncrementMs(gestureSettings.doubleTapSeekMs)
            .setSeekForwardIncrementMs(gestureSettings.doubleTapSeekMs)
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
                loudnessEnhancer.setLevel(volumeBoostLevel)
                binding.playerView.player = exoPlayer
                SubtitleStyleApplier.apply(binding.playerView.subtitleView, subtitleStyle)
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
        restoreAutoAdvancePromptIfNeeded()
    }

    private fun buildMediaItems(): List<MediaItem> {
        val routes = ArrayList<SubtitleSourceRoute>()
        val mediaItems = request.items.mapIndexed { index, item -> mediaItem(index, item, routes) }
        subtitleSourceRegistry.replace(routes)
        return mediaItems
    }

    private fun mediaItem(
        index: Int,
        item: AndroidPlaybackItem,
        routes: MutableList<SubtitleSourceRoute>,
    ): MediaItem {
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
        val subtitleSources = buildList {
            item.subtitles.forEachIndexed { subtitleIndex, subtitle ->
                add(
                    SessionSubtitleSource(
                        sourceUri = subtitle.sourceUri,
                        displayName = subtitle.displayName,
                        mimeType = subtitle.mimeType,
                        languageTag = subtitle.languageTag,
                        stableSuffix = "sidecar-$subtitleIndex",
                    ),
                )
            }
            manualSubtitle?.takeIf { manualSubtitleMediaIndex == index }?.let { subtitle ->
                add(
                    SessionSubtitleSource(
                        sourceUri = subtitle.uri,
                        displayName = subtitle.displayName,
                        mimeType = subtitle.mimeType,
                        languageTag = null,
                        stableSuffix = "manual",
                    ),
                )
            }
        }
        if (subtitleSources.isNotEmpty()) {
            builder.setSubtitleConfigurations(
                subtitleSources.mapIndexed { subtitleIndex, subtitle ->
                    val stableId = "$EXTERNAL_SUBTITLE_ID_PREFIX$index:${subtitle.stableSuffix}"
                    val virtualUri = Uri.Builder()
                        .scheme(SUBTITLE_MEMORY_SCHEME)
                        .authority(SUBTITLE_MEMORY_AUTHORITY)
                        .appendPath(index.toString())
                        .appendPath(subtitleIndex.toString())
                        .appendQueryParameter("revision", subtitleRouteRevision.toString())
                        .build()
                    routes += SubtitleSourceRoute(
                        virtualUri = virtualUri,
                        sourceUri = subtitle.sourceUri,
                        mimeType = subtitle.mimeType,
                        offsetMs = if (index == activeMediaItemIndex) subtitleOffsetMs else 0L,
                        stableId = stableId,
                    )
                    MediaItem.SubtitleConfiguration.Builder(virtualUri)
                        .setId(stableId)
                        .setMimeType(subtitle.mimeType)
                        .setLabel(
                            ExternalSubtitleTrackPolicy.encodedLabel(stableId, subtitle.displayName),
                        )
                        .apply { subtitle.languageTag?.let(::setLanguage) }
                        .build()
                },
            )
        }
        return builder.build()
    }

    private fun showSubtitleEncodingWarning(route: SubtitleSourceRoute) {
        if (!subtitleEncodingWarnings.add(route.stableId)) return
        mainHandler.post {
            if (!isFinishing && !isDestroyed) {
                Toast.makeText(this, R.string.subtitle_encoding_uncertain, Toast.LENGTH_LONG).show()
            }
        }
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
        updatePrecisionControls()
        renderQueuePanel()
    }

    private fun resumeItemIfAvailable(index: Int) {
        if (!resumedMediaItemIndexes.add(index)) return
        val item = request.items[index]
        val rememberedPosition = rememberedPosition(item)
        rememberedPosition?.takeIf { it > 0L }?.let { position ->
            player?.seekTo(index, position)
            Toast.makeText(
                this,
                getString(R.string.resume_from_position, PlayerGesturePolicy.formatTime(position)),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun rememberedPosition(item: AndroidPlaybackItem): Long? {
        if (!appPreferenceStore.rememberPlaybackPosition) {
            positionStore.clearAll()
            return null
        }
        // Always touch the local single-entry store first. A different target therefore clears the
        // prior record immediately even when an AutoJs6 Host Session supplies the actual resume.
        val localResume = positionStore.resumePosition(positionKey(item))
        val hostResume = item.relativePath?.let { relativePath ->
            item.hostTargetId?.let { targetId ->
                hostProgressClient?.resumePosition(targetId, relativePath)
            }
        }
        return hostResume ?: localResume.takeIf {
            hostProgressClient == null ||
                hostProgressClient?.state == HostPlaybackHistoryState.UNSUPPORTED
        }
    }

    private fun persistCachedItemPosition(index: Int, completed: Boolean) {
        val duration = durationsByIndex[index] ?: return
        val position = if (completed) duration else lastPositionsByIndex[index] ?: return
        persistItemPosition(index, position, duration, completed)
    }

    private fun positionKey(item: AndroidPlaybackItem): String {
        val targetId = item.hostTargetId
        val relativePath = item.relativePath
        return if (targetId != null && relativePath != null) {
            "explorer-session:$targetId:$relativePath"
        } else {
            item.externalUri?.toString() ?: item.sourceUri.toString()
        }
    }

    private fun handoffToBackgroundPlayback(): Boolean {
        val exoPlayer = player ?: return false
        val canContinueInBackground = canUseBackgroundAudio()
        val playbackActive = exoPlayer.playWhenReady &&
            exoPlayer.playbackState != Player.STATE_IDLE &&
            exoPlayer.playbackState != Player.STATE_ENDED
        val target = BackgroundPlaybackPolicy.exitTarget(
            inPictureInPicture = inPictureInPicture,
            backgroundAudioEnabled = canContinueInBackground,
            playbackActive = playbackActive,
            activityFinishing = isFinishing,
        )
        if (target != PlaybackExitTarget.BACKGROUND_AUDIO) return false
        if (speedBoostActive) {
            speedBoostActive = false
            exoPlayer.setPlaybackSpeed(playbackSpeed)
        }
        val value = BackgroundPlaybackHandoff(
            sessionId = backgroundPlaybackSessionId,
            player = exoPlayer,
            request = request,
            subtitleSourceRegistry = subtitleSourceRegistry,
            loudnessEnhancer = loudnessEnhancer,
            hostProgressClient = hostProgressClient,
            foregroundTrackSelectionParameters = exoPlayer.trackSelectionParameters,
            activeMediaItemIndex = exoPlayer.currentMediaItemIndex,
            playbackMode = playbackMode,
            abLoopState = abLoopState,
            sleepTimerMode = sleepTimerMode,
            sleepTimerDeadlineElapsedRealtimeMs = sleepTimerDeadlineElapsedRealtimeMs,
            volumeBoostLevel = volumeBoostLevel,
        )
        // Detaching the Activity's notification cancels the shared notification ID and
        // Android can revoke the prepared service's foreground state. Do this before the
        // service reasserts foreground ownership, while this Activity is still visible.
        systemIntegration.detach()
        if (!BackgroundPlaybackCoordinator.handoff(this, intent, value)) {
            systemIntegration.attach(exoPlayer)
            return false
        }

        savePlaybackPosition()
        capturePlaybackState()
        stopFrameRepeat()
        trackController.clearAvailability()
        transformedTextureView?.takeIf { transformedTextureAttachedToPlayer }?.let {
            exoPlayer.clearVideoTextureView(it)
        }
        transformedTextureAttachedToPlayer = false
        binding.playerView.player = null
        exoPlayer.removeListener(playerListener)
        player = null
        disposeTransformedTextureView()
        setKeepScreenOn(false)

        // Ownership of these session resources moved to the foreground service. Fresh empty
        // instances keep this Activity's eventual onDestroy cleanup idempotent.
        subtitleSourceRegistry = SubtitleSourceRegistry()
        loudnessEnhancer = PlayerLoudnessEnhancer()
        hostProgressClient = null
        backgroundOwnershipTransferred = true
        return true
    }

    private fun canUseBackgroundAudio(): Boolean {
        if (!backgroundAudioEnabled) return false
        if (settingsStore.continueAudioInBackground &&
            BackgroundPlaybackPermissionPolicy.canPostControls(this)
        ) {
            return true
        }
        backgroundAudioEnabled = false
        settingsStore.setContinueAudioInBackground(false)
        BackgroundPlaybackCoordinator.stopForDisabledSetting()
        return false
    }

    private fun adoptBackgroundPlayback(value: BackgroundPlaybackHandoff) {
        if (player != null || value.sessionId != backgroundPlaybackSessionId) return
        request = value.request
        hostProgressClient = value.hostProgressClient
        subtitleSourceRegistry.clear()
        subtitleSourceRegistry = value.subtitleSourceRegistry
        loudnessEnhancer.close()
        loudnessEnhancer = value.loudnessEnhancer
        playbackMode = value.playbackMode
        abLoopState = value.abLoopState
        sleepTimerMode = value.sleepTimerMode
        sleepTimerDeadlineElapsedRealtimeMs = value.sleepTimerDeadlineElapsedRealtimeMs
        volumeBoostLevel = value.volumeBoostLevel
        val exoPlayer = value.player
        player = exoPlayer
        activeMediaItemIndex = exoPlayer.currentMediaItemIndex.coerceIn(request.items.indices)
        resumeMediaItemIndex = activeMediaItemIndex
        resumePosition = exoPlayer.currentPosition.coerceAtLeast(0L)
        resumePlayWhenReady = exoPlayer.playWhenReady
        playbackSpeed = exoPlayer.playbackParameters.speed
        backgroundOwnershipTransferred = false

        exoPlayer.addListener(playerListener)
        applyPlaybackMode(exoPlayer)
        binding.playerView.player = exoPlayer
        SubtitleStyleApplier.apply(binding.playerView.subtitleView, subtitleStyle)
        systemIntegration.attach(exoPlayer)
        trackController.onTracksChanged(exoPlayer.currentTracks)
        loudnessEnhancer.setLevel(volumeBoostLevel)
        loudnessEnhancer.onAudioSessionIdChanged(exoPlayer.audioSessionId)
        val videoSize = exoPlayer.videoSize
        videoWidth = videoSize.width
        videoHeight = videoSize.height
        videoPixelWidthHeightRatio = videoSize.pixelWidthHeightRatio
        resumedMediaItemIndexes += activeMediaItemIndex
        updateActiveItemUi()
        binding.retryButton.isVisible = true
        binding.playbackErrorPanel.isVisible = false
        updatePlayPauseButton()
        updatePrecisionControls()
        updateControlAccessibility()
        updateToolbarMenu()
        updatePictureInPictureParameters()
        binding.playerView.post { applyVideoTransform() }
        scheduleSleepTimer()
        showControls()
    }

    private fun releasePlayer() {
        val exoPlayer = player ?: return
        cancelAutoAdvancePrompt(showFeedback = false)
        capturePlaybackState()
        speedBoostActive = false
        stopFrameRepeat()
        systemIntegration.detach()
        loudnessEnhancer.close()
        trackController.clearAvailability()
        transformedTextureView?.takeIf { transformedTextureAttachedToPlayer }?.let {
            exoPlayer.clearVideoTextureView(it)
        }
        transformedTextureAttachedToPlayer = false
        binding.playerView.player = null
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
        player = null
        disposeTransformedTextureView()
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
        if (!appPreferenceStore.rememberPlaybackPosition) {
            positionStore.clearAll()
            return
        }
        if (completed) positionStore.clear(positionKey(item))
        val handledByHost = item.relativePath?.let { relativePath ->
            item.hostTargetId?.let { targetId ->
                hostProgressClient?.report(
                    targetId,
                    relativePath,
                    boundedPosition,
                    durationMillis,
                    completed,
                )
            }
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
        cancelAutoAdvancePrompt(showFeedback = false)
        binding.playbackErrorPanel.isVisible = false
        releasePlayer()
        resumePlayWhenReady = true
        initializePlayer()
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            setKeepScreenOn(isPlaying)
            if (isPlaying && canUseBackgroundAudio() && !inPictureInPicture) {
                // Completion and foreground playback errors tear down the prepared service. If
                // playback is restarted in the same Activity, prepare it again while the process
                // is still TOP so a later Home transition never needs a background FGS start.
                BackgroundPlaybackCoordinator.prepare(
                    this@VideoPlayerActivity,
                    request.displayName,
                )
            }
            if (isPlaying) postponeControlsAutoHide() else showControls()
            updateToolbarMenu()
            updatePictureInPictureParameters()
            updatePrecisionControls()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                cancelAutoAdvancePrompt(showFeedback = false)
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
                BackgroundPlaybackCoordinator.stopPrepared()
                showControls()
            }
            updatePlayPauseButton()
            updateToolbarMenu()
            updatePictureInPictureParameters()
            updatePrecisionControls()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            if (!playWhenReady && reason == Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM) {
                handlePausedAtMediaItemEnd()
            } else if (playWhenReady && autoAdvancePromptDeadlineElapsedRealtimeMs > 0L) {
                clearAutoAdvancePrompt()
            }
            updatePlayPauseButton()
            updateToolbarMenu()
            updatePictureInPictureParameters()
            updatePrecisionControls()
        }

        override fun onTracksChanged(tracks: Tracks) {
            trackController.onTracksChanged(tracks)
            maybeWarnAboutUnsupportedHdr()
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
            if (nextIndex != activeMediaItemIndex) {
                cancelAutoAdvancePrompt(showFeedback = false)
            }
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
                val loopStart = abLoopState.pointAMs
                if (abLoopState.active && loopStart != null) {
                    player?.seekTo(loopStart)
                } else {
                    persistCachedItemPosition(activeMediaItemIndex, completed = true)
                }
            } else if (nextIndex != activeMediaItemIndex) {
                val needsSubtitleRefresh = subtitleOffsetMs != 0L
                subtitleOffsetMs = 0L
                clearAbLoop(showFeedback = false)
                persistCachedItemPosition(
                    activeMediaItemIndex,
                    completed = reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO,
                )
                activeMediaItemIndex = nextIndex
                trackController.resetSelectionsForMediaItem()
                resumeItemIfAvailable(nextIndex)
                if (needsSubtitleRefresh && !reloadingSubtitleSources) {
                    mainHandler.post {
                        if (player?.currentMediaItemIndex == nextIndex) {
                            reloadSubtitleSources(selectedExternalId = null)
                        }
                    }
                }
            }
            updateActiveItemUi()
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            videoWidth = videoSize.width
            videoHeight = videoSize.height
            // Media3 applies container rotation before reporting VideoSize on API 21+.
            videoRotationDegrees = 0
            videoPixelWidthHeightRatio = videoSize.pixelWidthHeightRatio
            // The selected video format may settle after the initial tracks callback.
            // Recheck here so HDR media still gets its one-time display warning.
            maybeWarnAboutUnsupportedHdr()
            applySuggestedOrientationIfNeeded(videoSize)
            binding.playerView.post { applyVideoTransform() }
            updatePictureInPictureParameters()
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            if (!loudnessEnhancer.onAudioSessionIdChanged(audioSessionId) &&
                volumeBoostLevel != VolumeBoostLevel.OFF
            ) {
                disableUnavailableVolumeBoost()
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateToolbarMenu()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            updateToolbarMenu()
        }

        override fun onPlayerError(error: PlaybackException) {
            cancelAutoAdvancePrompt(showFeedback = false)
            setKeepScreenOn(false)
            BackgroundPlaybackCoordinator.stopPrepared()
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
                            requireNotNull(item.hostTargetId),
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
        if (isTouchExplorationEnabled()) {
            controlsVisible = true
            return
        }
        controlsVisible = false
        binding.topBar.isVisible = false
        binding.bottomBar.isVisible = false
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun postponeControlsAutoHide() {
        mainHandler.removeCallbacks(hideControlsRunnable)
        if (player?.isPlaying == true && !scrubbing && !isTouchExplorationEnabled()) {
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
        if (!isTouchExplorationEnabled()) {
            mainHandler.postDelayed(hideUnlockRunnable, UNLOCK_BUTTON_HIDE_MS)
        }
    }

    private fun isTouchExplorationEnabled(): Boolean =
        ::accessibilityManager.isInitialized && accessibilityManager.isTouchExplorationEnabled

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
        }.getOrDefault(false).also { entered ->
            if (entered) inPictureInPicture = true
        }
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
            // onUserLeaveHint enters PiP synchronously and marks ownership before onPause. Letting
            // Android auto-enter as well creates a callback race where background audio can claim
            // the player before onPictureInPictureModeChanged arrives.
            builder.setAutoEnterEnabled(false)
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

    // region Queue auto-advance prompt

    private fun handlePausedAtMediaItemEnd() {
        val exoPlayer = player ?: return
        val index = exoPlayer.currentMediaItemIndex
        val duration = exoPlayer.duration
        if (duration > 0L && duration != C.TIME_UNSET) {
            persistItemPosition(
                index = index,
                positionMillis = duration,
                durationMillis = duration,
                completed = true,
            )
        }
        if (sleepTimerMode == SleepTimerMode.END_OF_VIDEO) {
            cancelAutoAdvancePrompt(showFeedback = false)
            finishSleepTimer()
            return
        }
        if (
            AutoAdvancePromptPolicy.shouldShowPrompt(
                itemCount = request.items.size,
                hasNextMediaItem = exoPlayer.hasNextMediaItem(),
                playbackMode = playbackMode,
                abLoopActive = abLoopState.active,
                stopAtEndOfVideo = false,
            )
        ) {
            startAutoAdvancePrompt()
        } else {
            // pauseAtEndOfMediaItems is queue-wide. At the final sequence item, resume once so
            // Media3 can enter STATE_ENDED instead of leaving a stale, paused end frame.
            exoPlayer.play()
        }
    }

    private fun startAutoAdvancePrompt() {
        autoAdvancePromptDeadlineElapsedRealtimeMs =
            SystemClock.elapsedRealtime() + AutoAdvancePromptPolicy.COUNTDOWN_MILLIS
        updateAutoAdvancePrompt()
        mainHandler.post {
            if (autoAdvancePromptDeadlineElapsedRealtimeMs > 0L && !controlsLocked) {
                hideControls()
            }
        }
    }

    private fun restoreAutoAdvancePromptIfNeeded() {
        if (autoAdvancePromptDeadlineElapsedRealtimeMs <= 0L) return
        val exoPlayer = player ?: return
        val eligible = AutoAdvancePromptPolicy.shouldShowPrompt(
            itemCount = request.items.size,
            hasNextMediaItem = exoPlayer.hasNextMediaItem(),
            playbackMode = playbackMode,
            abLoopActive = abLoopState.active,
            stopAtEndOfVideo = sleepTimerMode == SleepTimerMode.END_OF_VIDEO,
        )
        if (!eligible) {
            clearAutoAdvancePrompt()
            return
        }
        exoPlayer.pause()
        updateAutoAdvancePrompt()
    }

    private fun updateAutoAdvancePrompt() {
        mainHandler.removeCallbacks(autoAdvancePromptRunnable)
        val deadline = autoAdvancePromptDeadlineElapsedRealtimeMs
        if (deadline <= 0L) {
            binding.autoAdvancePromptPanel.isVisible = false
            return
        }
        val now = SystemClock.elapsedRealtime()
        val seconds = AutoAdvancePromptPolicy.remainingSeconds(deadline, now)
        if (seconds <= 0) {
            finishAutoAdvancePrompt()
            return
        }
        binding.autoAdvancePromptText.text = getString(R.string.auto_advance_prompt, seconds)
        binding.autoAdvancePromptPanel.isVisible = !inPictureInPicture
        mainHandler.postDelayed(
            autoAdvancePromptRunnable,
            (deadline - now).coerceAtMost(AUTO_ADVANCE_PROMPT_TICK_MS).coerceAtLeast(1L),
        )
    }

    private fun finishAutoAdvancePrompt() {
        if (autoAdvancePromptDeadlineElapsedRealtimeMs <= 0L) return
        clearAutoAdvancePrompt()
        player?.play()
    }

    private fun cancelAutoAdvancePrompt(showFeedback: Boolean) {
        if (autoAdvancePromptDeadlineElapsedRealtimeMs <= 0L) return
        clearAutoAdvancePrompt()
        if (showFeedback) {
            Toast.makeText(this, R.string.auto_advance_cancelled, Toast.LENGTH_SHORT).show()
            if (!controlsLocked) showControls()
        }
    }

    private fun clearAutoAdvancePrompt() {
        autoAdvancePromptDeadlineElapsedRealtimeMs = 0L
        mainHandler.removeCallbacks(autoAdvancePromptRunnable)
        if (::binding.isInitialized) binding.autoAdvancePromptPanel.isVisible = false
    }

    // endregion

    // region Playback controls

    private fun togglePlayPause() {
        val exoPlayer = player ?: return
        if (autoAdvancePromptDeadlineElapsedRealtimeMs > 0L) {
            finishAutoAdvancePrompt()
            return
        }
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

    @SuppressLint("ClickableViewAccessibility")
    private fun bindFrameStepButton(button: ImageButton, direction: Int) {
        button.setOnClickListener {
            stepFrame(direction)
            postponeControlsAutoHide()
        }
        button.setOnLongClickListener {
            if (!canStepFrame()) return@setOnLongClickListener false
            frameRepeatDirection = direction
            mainHandler.removeCallbacks(frameRepeatRunnable)
            stepFrame(direction)
            mainHandler.postDelayed(frameRepeatRunnable, FRAME_REPEAT_INTERVAL_MS)
            true
        }
        button.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                stopFrameRepeat()
            }
            false
        }
    }

    private fun canStepFrame(): Boolean {
        val exoPlayer = player ?: return false
        return !exoPlayer.playWhenReady && exoPlayer.playbackState != Player.STATE_IDLE &&
            exoPlayer.duration > 0L
    }

    private fun stepFrame(direction: Int) {
        if (!canStepFrame()) return
        val exoPlayer = player ?: return
        exoPlayer.setSeekParameters(SeekParameters.EXACT)
        val target = FrameStepPolicy.targetPositionMs(
            positionMs = exoPlayer.currentPosition,
            durationMs = exoPlayer.duration,
            frameRate = exoPlayer.videoFormat?.frameRate,
            direction = direction,
        )
        exoPlayer.seekTo(target)
        exoPlayer.setSeekParameters(SeekParameters.DEFAULT)
        updateProgress()
        showOsd(
            if (direction < 0) R.drawable.ic_frame_previous else R.drawable.ic_frame_next,
            PlayerGesturePolicy.formatTime(target),
        )
    }

    private fun stopFrameRepeat() {
        frameRepeatDirection = 0
        mainHandler.removeCallbacks(frameRepeatRunnable)
    }

    private fun toggleAbLoopPoint() {
        cancelAutoAdvancePrompt(showFeedback = false)
        val exoPlayer = player ?: return
        val duration = exoPlayer.duration.takeIf { it > 0L } ?: return
        val previous = abLoopState
        abLoopState = AbLoopPolicy.toggle(previous, exoPlayer.currentPosition, duration)
        when {
            abLoopState.awaitingPointB -> showOsd(
                R.drawable.ic_repeat,
                getString(
                    R.string.ab_loop_point_a_set,
                    PlayerGesturePolicy.formatTime(requireNotNull(abLoopState.pointAMs)),
                ),
            )
            abLoopState.active -> {
                if (playbackMode == VideoPlaybackMode.REPEAT_ONE) {
                    playbackMode = VideoPlaybackMode.SEQUENCE
                }
                // Keep the current item pinned even when B is at the media boundary. The regular
                // progress tick still performs the precise B -> A seek; repeat-one is a safety net
                // against an automatic queue transition between ticks.
                applyPlaybackMode(exoPlayer)
                if (sleepTimerMode == SleepTimerMode.END_OF_VIDEO) {
                    sleepTimerMode = SleepTimerMode.OFF
                    sleepTimerDeadlineElapsedRealtimeMs = 0L
                    scheduleSleepTimer()
                }
                showOsd(
                    R.drawable.ic_repeat,
                    getString(
                        R.string.ab_loop_active,
                        PlayerGesturePolicy.formatTime(requireNotNull(abLoopState.pointAMs)),
                        PlayerGesturePolicy.formatTime(requireNotNull(abLoopState.pointBMs)),
                    ),
                )
            }
            previous != AbLoopState() -> showOsd(
                R.drawable.ic_repeat,
                getString(R.string.ab_loop_cleared),
            )
        }
        updatePrecisionControls()
        updateToolbarMenu()
    }

    private fun clearAbLoop(showFeedback: Boolean) {
        if (abLoopState == AbLoopState()) return
        abLoopState = AbLoopState()
        player?.let(::applyPlaybackMode)
        if (showFeedback && ::binding.isInitialized) {
            showOsd(R.drawable.ic_repeat, getString(R.string.ab_loop_cleared))
        }
        if (::binding.isInitialized) updatePrecisionControls()
    }

    private fun updatePrecisionControls() {
        if (!::binding.isInitialized) return
        val exoPlayer = player
        val paused = exoPlayer != null && !exoPlayer.playWhenReady &&
            exoPlayer.playbackState != Player.STATE_IDLE && !inPictureInPicture &&
            !binding.playbackErrorPanel.isVisible
        binding.precisionControls.isVisible = paused
        binding.frameBackButton.isEnabled = paused && exoPlayer?.duration?.let { it > 0L } == true
        binding.frameForwardButton.isEnabled = binding.frameBackButton.isEnabled
        binding.abLoopButton.isEnabled = binding.frameBackButton.isEnabled
        val overlay = VideoThemePaletteGenerator.generate(videoPalette.source, dark = true)
        binding.abLoopButton.text = getString(
            when {
                abLoopState.active -> R.string.ab_loop_active_label
                abLoopState.awaitingPointB -> R.string.ab_loop_point_a_label
                else -> R.string.ab_loop_clear_label
            },
        )
        binding.abLoopButton.setTextColor(
            if (abLoopState == AbLoopState()) overlay.playerControl else overlay.secondary,
        )
        binding.abLoopButton.contentDescription = getString(
            R.string.control_state_description,
            getString(R.string.action_ab_loop),
            binding.abLoopButton.text,
        )
    }

    private fun updatePlayPauseButton() {
        val showPause = shouldShowPauseAction()
        binding.playPauseButton.setImageResource(
            if (showPause) R.drawable.ic_pause else R.drawable.ic_play_arrow,
        )
        binding.playPauseButton.setContentDescription(
            getString(if (showPause) R.string.action_pause else R.string.action_play),
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
        if (exoPlayer.isPlaying) {
            AbLoopPolicy.loopTargetMs(abLoopState, exoPlayer.currentPosition)?.let(exoPlayer::seekTo)
        }
        val duration = exoPlayer.duration.takeIf { it > 0L } ?: 0L
        val index = exoPlayer.currentMediaItemIndex
        if (index in request.items.indices) {
            lastPositionsByIndex[index] = exoPlayer.currentPosition.coerceAtLeast(0L)
            if (duration > 0L && durationsByIndex.put(index, duration) != duration) {
                renderQueuePanel()
            }
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
            .showWithPalette()
    }

    private fun updateSpeedButton() {
        binding.speedButton.text = PlayerGesturePolicy.formatSpeed(playbackSpeed)
        binding.speedButton.contentDescription = getString(
            R.string.control_state_description,
            getString(R.string.action_playback_speed),
            binding.speedButton.text,
        )
        val overlay = VideoThemePaletteGenerator.generate(videoPalette.source, dark = true)
        binding.speedButton.setTextColor(
            if (playbackSpeed == 1f) overlay.playerControl else overlay.secondary,
        )
    }

    private fun togglePlaybackMode() {
        cancelAutoAdvancePrompt(showFeedback = false)
        if (abLoopState != AbLoopState()) {
            clearAbLoop(showFeedback = false)
            Toast.makeText(this, R.string.ab_loop_cancelled_for_mode, Toast.LENGTH_SHORT).show()
        }
        playbackMode = VideoPlaybackModePolicy.next(playbackMode, request.items.size)
        if (playbackMode == VideoPlaybackMode.REPEAT_ONE && sleepTimerMode == SleepTimerMode.END_OF_VIDEO) {
            sleepTimerMode = SleepTimerMode.OFF
            sleepTimerDeadlineElapsedRealtimeMs = 0L
            mainHandler.removeCallbacks(sleepTimerRunnable)
            Toast.makeText(this, R.string.sleep_timer_cancelled, Toast.LENGTH_SHORT).show()
        }
        settingsStore.writePlaybackMode(playbackMode)
        player?.let(::applyPlaybackMode)
        updateToolbarMenu()
        renderQueuePanel()
        showOsd(
            playbackModeIcon(),
            getString(playbackModeTitle()),
        )
        postponeControlsAutoHide()
    }

    private fun applyPlaybackMode(exoPlayer: ExoPlayer) {
        exoPlayer.shuffleModeEnabled = playbackMode == VideoPlaybackMode.SHUFFLE
        exoPlayer.repeatMode = if (abLoopState.active || playbackMode == VideoPlaybackMode.REPEAT_ONE) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
        exoPlayer.setPauseAtEndOfMediaItems(
            AutoAdvancePromptPolicy.shouldPauseAtItemEnd(
                itemCount = request.items.size,
                playbackMode = playbackMode,
                abLoopActive = abLoopState.active,
                stopAtEndOfVideo = sleepTimerMode == SleepTimerMode.END_OF_VIDEO,
            ),
        )
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

    private fun showQueuePanel() {
        if (request.hostSession == null && request.items.size <= 1) return
        mainHandler.removeCallbacks(hideControlsRunnable)
        dismissQueuePanel(postponeAutoHide = false)
        val sheetBinding = BottomSheetVideoQueueBinding.inflate(layoutInflater)
        val adapter = VideoQueueAdapter(videoPalette) { index ->
            player?.takeIf { index in 0 until it.mediaItemCount }?.let { exoPlayer ->
                cancelAutoAdvancePrompt(showFeedback = false)
                exoPlayer.seekToDefaultPosition(index)
                exoPlayer.play()
                renderQueuePanel()
            }
        }
        styleQueuePanel(sheetBinding)
        sheetBinding.queueList.layoutManager = LinearLayoutManager(this)
        sheetBinding.queueList.adapter = adapter
        sheetBinding.queueModeButton.setOnClickListener { togglePlaybackMode() }
        val dialog = BottomSheetDialog(this).apply {
            setContentView(sheetBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            setOnDismissListener {
                queueDialog = null
                queueSheetBinding = null
                queueAdapter = null
                postponeControlsAutoHide()
            }
        }
        queueDialog = dialog
        queueSheetBinding = sheetBinding
        queueAdapter = adapter
        renderQueuePanel()
        dialog.show()
        val currentIndex = player?.currentMediaItemIndex
            ?.takeIf { it in request.items.indices }
            ?: activeMediaItemIndex
        sheetBinding.queueList.scrollToPosition(currentIndex)
    }

    private fun styleQueuePanel(sheetBinding: BottomSheetVideoQueueBinding) {
        val palette = videoPalette
        sheetBinding.queueSheetRoot.setBackgroundColor(palette.surfaceContainerLow)
        sheetBinding.queueHandle.setBackgroundColor(palette.outline)
        sheetBinding.queueTitle.setTextColor(palette.onSurface)
        sheetBinding.queueCount.setTextColor(palette.onSurfaceVariant)
        sheetBinding.queueModeButton.backgroundTintList = ColorStateList.valueOf(
            palette.surfaceContainerHigh,
        )
        sheetBinding.queueModeButton.setTextColor(palette.primary)
        sheetBinding.queueModeButton.iconTint = ColorStateList.valueOf(palette.primary)
        sheetBinding.queueModeButton.rippleColor = ColorStateList.valueOf(
            VideoThemePaletteGenerator.withAlpha(palette.primary, 0x24),
        )
    }

    private fun renderQueuePanel() {
        val sheetBinding = queueSheetBinding ?: return
        val adapter = queueAdapter ?: return
        val currentIndex = player?.currentMediaItemIndex
            ?.takeIf { it in request.items.indices }
            ?: activeMediaItemIndex
        sheetBinding.queueCount.text = getString(R.string.video_playlist_count, request.items.size)
        sheetBinding.queueModeButton.text = getString(playbackModeTitle())
        sheetBinding.queueModeButton.setIconResource(playbackModeIcon())
        sheetBinding.queueModeButton.isActivated = playbackMode != VideoPlaybackMode.SEQUENCE
        adapter.submitList(
            request.items.mapIndexed { index, item ->
                VideoQueueRow(
                    index = index,
                    title = item.displayName,
                    durationLabel = durationsByIndex[index]
                        ?.takeIf { it > 0L }
                        ?.let(PlayerGesturePolicy::formatTime)
                        ?: getString(R.string.queue_duration_unknown),
                    current = index == currentIndex,
                )
            },
        )
    }

    private fun dismissQueuePanel(postponeAutoHide: Boolean = true) {
        queueDialog?.setOnDismissListener(null)
        queueDialog?.dismiss()
        queueDialog = null
        queueSheetBinding?.queueList?.adapter = null
        queueSheetBinding = null
        queueAdapter = null
        if (postponeAutoHide && ::binding.isInitialized) postponeControlsAutoHide()
    }

    private fun onTrackAvailabilityChanged(hasAudioChoices: Boolean, hasSubtitles: Boolean) {
        hasAudioTrackChoices = hasAudioChoices
        hasSubtitleTracks = hasSubtitles
        updateToolbarMenu()
    }

    private fun launchSubtitlePicker() {
        if (resolvingManualSubtitle) return
        subtitlePicker.launch(SUBTITLE_DOCUMENT_MIME_TYPES)
    }

    private fun resolveManualSubtitle(uri: Uri) {
        if (resolvingManualSubtitle) return
        resolvingManualSubtitle = true
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { inspectManualSubtitle(uri) }
            resolvingManualSubtitle = false
            when (result) {
                is ManualSubtitleResolution.Success -> {
                    manualSubtitle = result.document
                    manualSubtitleMediaIndex = activeMediaItemIndex
                    val stableId = "$EXTERNAL_SUBTITLE_ID_PREFIX$activeMediaItemIndex:manual"
                    reloadSubtitleSources(selectedExternalId = stableId)
                    Toast.makeText(
                        this@VideoPlayerActivity,
                        getString(R.string.subtitle_loaded, result.document.displayName),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                ManualSubtitleResolution.InvalidType -> Toast.makeText(
                    this@VideoPlayerActivity,
                    R.string.subtitle_load_invalid_type,
                    Toast.LENGTH_LONG,
                ).show()
                ManualSubtitleResolution.TooLarge -> Toast.makeText(
                    this@VideoPlayerActivity,
                    R.string.subtitle_load_too_large,
                    Toast.LENGTH_LONG,
                ).show()
                ManualSubtitleResolution.Unreadable -> Toast.makeText(
                    this@VideoPlayerActivity,
                    R.string.subtitle_load_failed,
                    Toast.LENGTH_LONG,
                ).show()
            }
            postponeControlsAutoHide()
        }
    }

    private fun inspectManualSubtitle(uri: Uri): ManualSubtitleResolution {
        if (uri.scheme != "content") return ManualSubtitleResolution.InvalidType
        var displayName: String? = null
        var declaredSize = -1L
        runCatching {
            contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                null,
                null,
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex >= 0 && !cursor.isNull(nameIndex)) displayName = cursor.getString(nameIndex)
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) declaredSize = cursor.getLong(sizeIndex)
                }
            }
        }.getOrElse { return ManualSubtitleResolution.Unreadable }
        if (declaredSize > SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES) {
            return ManualSubtitleResolution.TooLarge
        }
        val validated = SubtitleDocumentPolicy.validate(displayName, declaredSize.coerceAtLeast(0L))
            ?: return ManualSubtitleResolution.InvalidType
        val actualSize = runCatching {
            contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(MANUAL_SUBTITLE_VALIDATION_BUFFER_SIZE)
                var total = 0L
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    total += read
                    if (total > SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES) return@use total
                }
                total
            }
        }.getOrNull() ?: return ManualSubtitleResolution.Unreadable
        if (actualSize > SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES) return ManualSubtitleResolution.TooLarge
        return ManualSubtitleResolution.Success(
            ManualSubtitleDocument(
                uri = uri,
                displayName = validated.displayName,
                mimeType = validated.mimeType,
                size = actualSize,
            ),
        )
    }

    private fun showSubtitleOffsetDialog() {
        if (!trackController.selectedSubtitleIsExternal) return
        val dialogBinding = DialogSubtitleOffsetBinding.inflate(layoutInflater)
        val initialSeconds = subtitleOffsetMs / 1_000f
        dialogBinding.offsetSlider.value = initialSeconds.coerceIn(-600f, 600f)
        fun render(value: Float) {
            val offset = SubtitleTimingPolicy.normalizeOffsetMs((value * 1_000f).roundToLong())
            dialogBinding.offsetValue.text = getString(
                R.string.subtitle_offset_value,
                SubtitleTimingPolicy.formatOffset(offset),
            )
            showOsd(
                R.drawable.ic_subtitles,
                getString(R.string.subtitle_offset_value, SubtitleTimingPolicy.formatOffset(offset)),
                sticky = true,
            )
        }
        fun apply(value: Float) {
            val offset = SubtitleTimingPolicy.normalizeOffsetMs((value * 1_000f).roundToLong())
            applySubtitleOffset(offset)
        }
        dialogBinding.offsetSlider.addOnChangeListener { _, value, _ -> render(value) }
        dialogBinding.offsetSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) = Unit

            override fun onStopTrackingTouch(slider: Slider) {
                apply(slider.value)
            }
        })
        dialogBinding.offsetDecrease.setOnClickListener {
            dialogBinding.offsetSlider.value = (dialogBinding.offsetSlider.value - 0.1f).coerceAtLeast(-600f)
            apply(dialogBinding.offsetSlider.value)
        }
        dialogBinding.offsetReset.setOnClickListener {
            dialogBinding.offsetSlider.value = 0f
            apply(0f)
        }
        dialogBinding.offsetIncrease.setOnClickListener {
            dialogBinding.offsetSlider.value = (dialogBinding.offsetSlider.value + 0.1f).coerceAtMost(600f)
            apply(dialogBinding.offsetSlider.value)
        }
        render(initialSeconds)
        AlertDialog.Builder(this)
            .setTitle(R.string.subtitle_offset)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener {
                hideOsdSoon()
                postponeControlsAutoHide()
            }
            .showWithPalette()
    }

    private fun applySubtitleOffset(offsetMs: Long) {
        if (!trackController.selectedSubtitleIsExternal) return
        val normalized = SubtitleTimingPolicy.normalizeOffsetMs(offsetMs)
        val selectedId = trackController.selectedSubtitleId ?: return
        if (normalized != subtitleOffsetMs) {
            subtitleOffsetMs = normalized
            reloadSubtitleSources(selectedExternalId = selectedId)
        }
        showOsd(
            R.drawable.ic_subtitles,
            getString(R.string.subtitle_offset_value, SubtitleTimingPolicy.formatOffset(normalized)),
            sticky = true,
        )
    }

    private fun reloadSubtitleSources(selectedExternalId: String?) {
        val exoPlayer = player ?: return
        val index = exoPlayer.currentMediaItemIndex.coerceIn(request.items.indices)
        val position = exoPlayer.currentPosition.coerceAtLeast(0L)
        val playWhenReady = exoPlayer.playWhenReady
        subtitleRouteRevision += 1L
        reloadingSubtitleSources = true
        try {
            trackController.resetSelectionsForMediaItem()
            selectedExternalId?.let(trackController::prepareExternalSubtitleSelection)
            exoPlayer.setMediaItems(buildMediaItems(), index, position)
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.setPlaybackSpeed(playbackSpeed)
            applyPlaybackMode(exoPlayer)
            exoPlayer.prepare()
        } finally {
            reloadingSubtitleSources = false
        }
        updateActiveItemUi()
    }

    private fun restoreManualSubtitle(state: Bundle) {
        val index = state.getInt(STATE_MANUAL_SUBTITLE_MEDIA_INDEX, -1)
        val uri = state.getString(STATE_MANUAL_SUBTITLE_URI)?.let(Uri::parse)
        val displayName = state.getString(STATE_MANUAL_SUBTITLE_NAME)
        val mimeType = state.getString(STATE_MANUAL_SUBTITLE_MIME)
        val size = state.getLong(STATE_MANUAL_SUBTITLE_SIZE, -1L)
        val validated = SubtitleDocumentPolicy.validate(displayName, size)
        if (index !in request.items.indices || uri?.scheme != "content" || validated == null ||
            validated.mimeType != mimeType
        ) {
            return
        }
        manualSubtitleMediaIndex = index
        manualSubtitle = ManualSubtitleDocument(uri, validated.displayName, validated.mimeType, size)
    }

    private fun updateToolbarMenu() {
        val menu = binding.toolbar.menu
        val overlay = VideoThemePaletteGenerator.generate(videoPalette.source, dark = true)
        VideoThemeViewStyler.tintMenu(menu, overlay.playerControl)
        menu.findItem(R.id.action_audio_track)?.apply {
            isVisible = hasAudioTrackChoices
            isEnabled = player != null
        }
        menu.findItem(R.id.action_subtitle_track)?.apply {
            isVisible = true
            isEnabled = player != null
            icon?.mutate()?.setTint(
                if (trackController.selectedSubtitleId == null &&
                    trackController.selectedSubtitleKey == null
                ) {
                    overlay.playerControl
                } else {
                    overlay.secondary
                },
            )
        }
        menu.findItem(R.id.action_repeat)?.apply {
            isEnabled = player != null
            title = getString(playbackModeTitle())
            setIcon(playbackModeIcon())
            icon?.mutate()?.setTint(
                if (playbackMode == VideoPlaybackMode.SEQUENCE) overlay.playerControl else overlay.secondary,
            )
        }
        menu.findItem(R.id.action_playlist)?.apply {
            isVisible = request.hostSession != null || request.items.size > 1
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
                if (sleepTimerMode == SleepTimerMode.OFF) overlay.playerControl else overlay.secondary,
            )
        }
        menu.findItem(R.id.action_screenshot)?.apply {
            isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            isEnabled = player != null && !screenshotInProgress
        }
        menu.findItem(R.id.action_mirror)?.apply {
            isEnabled = player != null
            title = getString(
                R.string.gesture_setting_value,
                getString(R.string.action_mirror),
                mirrorModeLabel(mirrorMode),
            )
            icon?.mutate()?.setTint(
                if (mirrorMode == VideoMirrorMode.NONE) overlay.playerControl else overlay.secondary,
            )
        }
        menu.findItem(R.id.action_volume_boost)?.apply {
            isEnabled = player != null
            title = getString(
                R.string.gesture_setting_value,
                getString(R.string.action_volume_boost),
                volumeBoostLabel(volumeBoostLevel),
            )
            icon?.mutate()?.setTint(
                if (volumeBoostLevel == VolumeBoostLevel.OFF) {
                    overlay.playerControl
                } else {
                    overlay.secondary
                },
            )
        }
        menu.findItem(R.id.action_brightness)?.apply {
            isEnabled = player != null
            title = getString(
                R.string.control_state_description,
                getString(R.string.action_brightness),
                PlayerGesturePolicy.percentLabel(currentBrightnessFraction()),
            )
        }
        menu.findItem(R.id.action_volume)?.apply {
            isEnabled = player != null
            title = getString(
                R.string.control_state_description,
                getString(R.string.action_volume),
                PlayerGesturePolicy.percentLabel(currentVolumeFraction()),
            )
        }
        menu.findItem(R.id.action_zoom)?.apply {
            isEnabled = player != null
            title = getString(
                R.string.control_state_description,
                getString(R.string.action_zoom),
                PlayerGesturePolicy.formatSpeed(manualZoomScale),
            )
        }
        menu.findItem(R.id.action_gesture_settings)?.isEnabled = player != null
        menu.findItem(R.id.action_video_info)?.isEnabled = player != null
        renderQueuePanel()
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
            .showWithPalette()
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
        cancelAutoAdvancePrompt(showFeedback = false)
        if (mode == SleepTimerMode.END_OF_VIDEO && abLoopState != AbLoopState()) {
            clearAbLoop(showFeedback = false)
            Toast.makeText(this, R.string.ab_loop_cancelled_for_mode, Toast.LENGTH_SHORT).show()
        }
        val previousMode = sleepTimerMode
        sleepTimerMode = mode
        sleepTimerDeadlineElapsedRealtimeMs = SleepTimerPolicy.deadlineElapsedRealtimeMs(
            mode,
            SystemClock.elapsedRealtime(),
        ) ?: 0L
        if (mode == SleepTimerMode.END_OF_VIDEO && playbackMode == VideoPlaybackMode.REPEAT_ONE) {
            playbackMode = VideoPlaybackMode.SEQUENCE
        }
        player?.let(::applyPlaybackMode)
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
        cancelAutoAdvancePrompt(showFeedback = false)
        player?.pause()
        resumePlayWhenReady = false
        sleepTimerMode = SleepTimerMode.OFF
        sleepTimerDeadlineElapsedRealtimeMs = 0L
        mainHandler.removeCallbacks(sleepTimerRunnable)
        player?.let(::applyPlaybackMode)
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
        capture.capture(
            playerView = binding.playerView,
            videoSurface = activeVideoOutputView(),
            options = FrameCaptureOptions(
                includeSubtitles = settingsStore.includeSubtitlesInScreenshots,
                renderTransform = currentVideoRenderTransform(),
            ),
        ) { result ->
            screenshotInProgress = false
            updateToolbarMenu()
            when (result) {
                is FrameCaptureResult.Success -> Snackbar.make(
                    binding.root,
                    R.string.screenshot_saved,
                    Snackbar.LENGTH_LONG,
                ).setAction(R.string.action_share) {
                    shareCapturedFrame(result.uri)
                }.show()
                FrameCaptureResult.VideoNotReady -> Toast.makeText(
                    this,
                    R.string.screenshot_not_ready,
                    Toast.LENGTH_SHORT,
                ).show()
                FrameCaptureResult.CopyFailed,
                FrameCaptureResult.SaveFailed,
                -> Toast.makeText(
                    this,
                    R.string.screenshot_save_failed,
                    Toast.LENGTH_SHORT,
                ).show()
            }
            postponeControlsAutoHide()
        }
    }

    private fun shareCapturedFrame(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newUri(contentResolver, getString(R.string.action_share), uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
        }.onFailure {
            Toast.makeText(this, R.string.screenshot_share_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMirrorDialog() {
        val modes = VideoMirrorMode.entries
        val labels = modes.map(::mirrorModeLabel).toTypedArray()
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_mirror)
            .setSingleChoiceItems(labels, modes.indexOf(mirrorMode)) { dialog, which ->
                mirrorMode = modes[which]
                applyVideoTransform()
                updateToolbarMenu()
                showOsd(R.drawable.ic_flip, mirrorModeLabel(mirrorMode))
                dialog.dismiss()
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .showWithPalette()
    }

    private fun mirrorModeLabel(mode: VideoMirrorMode): String = getString(
        when (mode) {
            VideoMirrorMode.NONE -> R.string.mirror_mode_none
            VideoMirrorMode.HORIZONTAL -> R.string.mirror_mode_horizontal
            VideoMirrorMode.VERTICAL -> R.string.mirror_mode_vertical
            VideoMirrorMode.BOTH -> R.string.mirror_mode_both
        },
    )

    private fun showVolumeBoostDialog() {
        val levels = VolumeBoostLevel.entries
        val labels = levels.map(::volumeBoostLabel).toTypedArray()
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_volume_boost)
            .setSingleChoiceItems(labels, levels.indexOf(volumeBoostLevel)) { dialog, which ->
                val selected = levels[which]
                dialog.dismiss()
                if (selected != VolumeBoostLevel.OFF && !volumeBoostWarningAcknowledged) {
                    showVolumeBoostWarning(selected)
                } else {
                    applyVolumeBoost(selected)
                }
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .showWithPalette()
    }

    private fun showVolumeBoostWarning(level: VolumeBoostLevel) {
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.volume_boost_warning_title)
            .setMessage(R.string.volume_boost_warning_message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.volume_boost_continue) { _, _ ->
                volumeBoostWarningAcknowledged = true
                applyVolumeBoost(level)
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .showWithPalette()
    }

    private fun applyVolumeBoost(level: VolumeBoostLevel) {
        volumeBoostLevel = level
        if (!loudnessEnhancer.setLevel(level) && level != VolumeBoostLevel.OFF) {
            disableUnavailableVolumeBoost()
            return
        }
        updateToolbarMenu()
        showOsd(R.drawable.ic_volume_up, volumeBoostLabel(level))
    }

    private fun disableUnavailableVolumeBoost() {
        volumeBoostLevel = VolumeBoostLevel.OFF
        loudnessEnhancer.setLevel(VolumeBoostLevel.OFF)
        updateToolbarMenu()
        Toast.makeText(this, R.string.volume_boost_unavailable, Toast.LENGTH_LONG).show()
    }

    private fun volumeBoostLabel(level: VolumeBoostLevel): String = if (
        level == VolumeBoostLevel.OFF
    ) {
        getString(R.string.volume_boost_off)
    } else {
        getString(R.string.volume_boost_db, level.decibels)
    }

    private fun showBrightnessDialog() {
        showAdjustmentDialog(
            titleRes = R.string.action_brightness,
            descriptionRes = R.string.brightness_slider_description,
            iconRes = R.drawable.ic_brightness,
            valueFrom = PlayerGesturePolicy.MIN_BRIGHTNESS,
            valueTo = 1f,
            stepSize = 0.01f,
            initialValue = currentBrightnessFraction(),
            valueLabel = PlayerGesturePolicy::percentLabel,
        ) { value -> applyBrightnessFraction(value) }
    }

    private fun showVolumeDialog() {
        showAdjustmentDialog(
            titleRes = R.string.action_volume,
            descriptionRes = R.string.volume_slider_description,
            iconRes = R.drawable.ic_volume_up,
            valueFrom = 0f,
            valueTo = 1f,
            stepSize = 0.01f,
            initialValue = currentVolumeFraction(),
            valueLabel = PlayerGesturePolicy::percentLabel,
        ) { value -> applyVolumeFraction(value) }
    }

    private fun showZoomDialog() {
        showAdjustmentDialog(
            titleRes = R.string.action_zoom,
            descriptionRes = R.string.zoom_slider_description,
            iconRes = R.drawable.ic_aspect_ratio,
            valueFrom = PlayerGesturePolicy.MIN_ZOOM_SCALE,
            valueTo = PlayerGesturePolicy.MAX_ZOOM_SCALE,
            stepSize = 0.05f,
            initialValue = manualZoomScale,
            valueLabel = PlayerGesturePolicy::formatSpeed,
        ) { value ->
            resizeMode = PlayerResizeMode.FIT
            manualZoomScale = value
            zoomPivotXFraction = 0.5f
            zoomPivotYFraction = 0.5f
            applyResizeMode(showOsd = false)
            applyVideoTransform()
            updateControlAccessibility()
        }
    }

    private fun showAdjustmentDialog(
        titleRes: Int,
        descriptionRes: Int,
        @DrawableRes iconRes: Int,
        valueFrom: Float,
        valueTo: Float,
        stepSize: Float,
        initialValue: Float,
        valueLabel: (Float) -> String,
        applyValue: (Float) -> Unit,
    ) {
        val padding = (24f * resources.displayMetrics.density).roundToInt()
        val slider = Slider(this).apply {
            this.valueFrom = valueFrom
            this.valueTo = valueTo
            this.stepSize = stepSize
            value = PlayerGesturePolicy.snapToSliderStep(
                value = initialValue,
                valueFrom = valueFrom,
                valueTo = valueTo,
                stepSize = stepSize,
            )
            contentDescription = getString(descriptionRes)
            setLabelFormatter(valueLabel)
            addOnChangeListener { _, changedValue, _ ->
                applyValue(changedValue)
                showOsd(iconRes, valueLabel(changedValue), sticky = true)
            }
        }
        val container = FrameLayout(this).apply {
            setPadding(padding, padding / 2, padding, padding / 2)
            addView(
                slider,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(titleRes)
            .setView(container)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener {
                hideOsdSoon()
                updateToolbarMenu()
                postponeControlsAutoHide()
            }
            .showWithPalette()
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
            .showWithPalette()
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
            .showWithPalette()
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
                updateControlAccessibility()
                dialog.dismiss()
            }
            .setOnDismissListener { postponeControlsAutoHide() }
            .showWithPalette()
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
        val colorSummary = currentMediaColorSummary()
        val rows = listOf(
            R.string.media_info_file_name to item.displayName,
            R.string.media_info_declared_size to declaredSize,
            R.string.media_info_container_mime to containerMime,
            R.string.media_info_resolution to resolution,
            R.string.media_info_frame_rate to frameRate,
            R.string.media_info_video_codec to videoCodec,
            R.string.media_info_hdr_type to hdrTypeLabel(colorSummary.hdrType),
            R.string.media_info_color_space to colorSpaceLabel(colorSummary.colorSpace),
            R.string.media_info_color_range to colorRangeLabel(colorSummary.colorRange),
            R.string.media_info_bit_depth to bitDepthLabel(colorSummary),
            R.string.media_info_audio_codec to audioCodec,
        )
        val text = rows.joinToString("\n") { (label, value) ->
            getString(R.string.media_info_row, getString(label), value)
        }
        mainHandler.removeCallbacks(hideControlsRunnable)
        AlertDialog.Builder(this)
            .setTitle(R.string.action_video_info)
            .setMessage(text)
            .setNeutralButton(R.string.action_copy_all) { _, _ -> copyVideoInfo(text) }
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener { postponeControlsAutoHide() }
            .showWithPalette()
    }

    private fun copyVideoInfo(text: String) {
        val clipboard = getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(
            ClipData.newPlainText(getString(R.string.action_video_info), text),
        )
        Toast.makeText(this, R.string.media_info_copied, Toast.LENGTH_SHORT).show()
    }

    private fun currentMediaColorSummary(): MediaColorSummary {
        val colorInfo = player?.videoFormat?.colorInfo
        val metadata = colorInfo?.let { info ->
            MediaColorMetadata(
                colorSpace = when (info.colorSpace) {
                    C.COLOR_SPACE_BT601 -> MediaColorSpace.BT601
                    C.COLOR_SPACE_BT709 -> MediaColorSpace.BT709
                    C.COLOR_SPACE_BT2020 -> MediaColorSpace.BT2020
                    else -> MediaColorSpace.UNKNOWN
                },
                colorRange = when (info.colorRange) {
                    C.COLOR_RANGE_LIMITED -> MediaColorRange.LIMITED
                    C.COLOR_RANGE_FULL -> MediaColorRange.FULL
                    else -> MediaColorRange.UNKNOWN
                },
                colorTransfer = when (info.colorTransfer) {
                    C.COLOR_TRANSFER_SDR -> MediaColorTransfer.SDR
                    C.COLOR_TRANSFER_ST2084 -> MediaColorTransfer.ST2084
                    C.COLOR_TRANSFER_HLG -> MediaColorTransfer.HLG
                    else -> MediaColorTransfer.OTHER
                },
                lumaBitDepth = info.lumaBitdepth,
                chromaBitDepth = info.chromaBitdepth,
            )
        }
        return HdrMediaInfoPolicy.summarize(metadata)
    }

    private fun hdrTypeLabel(type: MediaHdrType): String = getString(
        when (type) {
            MediaHdrType.NONE -> R.string.hdr_type_none
            MediaHdrType.HDR10 -> R.string.hdr_type_hdr10
            MediaHdrType.HLG -> R.string.hdr_type_hlg
            MediaHdrType.UNKNOWN -> R.string.media_info_unknown
        },
    )

    private fun colorSpaceLabel(colorSpace: MediaColorSpace): String = getString(
        when (colorSpace) {
            MediaColorSpace.BT601 -> R.string.color_space_bt601
            MediaColorSpace.BT709 -> R.string.color_space_bt709
            MediaColorSpace.BT2020 -> R.string.color_space_bt2020
            MediaColorSpace.UNKNOWN -> R.string.media_info_unknown
        },
    )

    private fun colorRangeLabel(colorRange: MediaColorRange): String = getString(
        when (colorRange) {
            MediaColorRange.LIMITED -> R.string.color_range_limited
            MediaColorRange.FULL -> R.string.color_range_full
            MediaColorRange.UNKNOWN -> R.string.media_info_unknown
        },
    )

    private fun bitDepthLabel(summary: MediaColorSummary): String {
        return HdrMediaInfoPolicy.formatBitDepth(
            summary = summary,
            unknownValue = getString(R.string.media_info_unknown),
            singleValue = { bits -> getString(R.string.media_info_bit_depth_value, bits) },
            pairValue = { luma, chroma ->
                getString(R.string.media_info_bit_depth_pair_value, luma, chroma)
            },
        )
    }

    private fun maybeWarnAboutUnsupportedHdr() {
        val hdrType = currentMediaColorSummary().hdrType
        val supportedTypes = supportedHdrTypes()
        val shouldWarn = HdrMediaInfoPolicy.requiresToneMappingWarning(
            hdrType = hdrType,
            supportsHdr10 = Display.HdrCapabilities.HDR_TYPE_HDR10 in supportedTypes,
            supportsHlg = Display.HdrCapabilities.HDR_TYPE_HLG in supportedTypes,
        )
        val warningKey = "$activeMediaItemIndex:${hdrType.name}"
        if (!shouldWarn || !unsupportedHdrWarnings.add(warningKey)) return
        Snackbar.make(
            binding.root,
            getString(R.string.hdr_tone_mapping_warning, hdrTypeLabel(hdrType)),
            Snackbar.LENGTH_LONG,
        ).show()
    }

    @Suppress("DEPRECATION")
    private fun supportedHdrTypes(): IntArray =
        (binding.root.display ?: windowManager.defaultDisplay).hdrCapabilities?.supportedHdrTypes ?: IntArray(0)

    private fun applyResizeMode(showOsd: Boolean) {
        binding.playerView.resizeMode = when (resizeMode) {
            PlayerResizeMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            PlayerResizeMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            PlayerResizeMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
        val label = resizeModeLabel()
        binding.resizeButton.contentDescription = getString(
            R.string.control_state_description,
            getString(R.string.action_resize_mode),
            resizeAccessibilityValue(),
        )
        if (showOsd) showOsd(R.drawable.ic_aspect_ratio, getString(label))
    }

    private fun resizeModeLabel(): Int = when (resizeMode) {
        PlayerResizeMode.FIT -> R.string.resize_mode_fit
        PlayerResizeMode.FILL -> R.string.resize_mode_fill
        PlayerResizeMode.ZOOM -> R.string.resize_mode_zoom
    }

    private fun resizeAccessibilityValue(): String = if (
        abs(manualZoomScale - PlayerGesturePolicy.DEFAULT_ZOOM_SCALE) >= ZOOM_EPSILON
    ) {
        getString(
            R.string.zoom_scale_value,
            PlayerGesturePolicy.formatSpeed(manualZoomScale),
        )
    } else {
        getString(resizeModeLabel())
    }

    private fun currentVideoRenderTransform(): VideoRenderTransform = VideoTransformPolicy.resolve(
        zoomScale = manualZoomScale,
        pivotXFraction = zoomPivotXFraction,
        pivotYFraction = zoomPivotYFraction,
        mirrorMode = mirrorMode,
    )

    private fun applyVideoTransform() {
        val videoSurface = binding.playerView.videoSurfaceView ?: return
        val transform = currentVideoRenderTransform()
        val transformTarget = if (requiresTextureOutput(transform)) {
            useTransformedTextureOutput(videoSurface)
        } else {
            useDefaultVideoOutput(videoSurface)
        }
        transformTarget.apply {
            if (width > 0 && height > 0) {
                pivotX = width * transform.pivotXFraction
                pivotY = height * transform.pivotYFraction
            }
            scaleX = transform.scaleX
            scaleY = transform.scaleY
        }
    }

    /**
     * SurfaceView is retained for the default path because it provides the best HDR output. Some
     * vendor compositors ignore mirror matrices on SurfaceView, so an opt-in TextureView is used
     * only while a manual zoom or mirror transform is active. The TextureView stays inside the
     * video content frame and therefore never transforms subtitles or player controls.
     */
    private fun useTransformedTextureOutput(videoSurface: View): View {
        if (videoSurface is TextureView) return videoSurface
        val parent = videoSurface.parent as? ViewGroup ?: return videoSurface
        val textureView = transformedTextureView?.takeIf { it.parent === parent }
            ?: TextureView(this).also { texture ->
                transformedTextureView?.let { stale ->
                    (stale.parent as? ViewGroup)?.removeView(stale)
                }
                val surfaceIndex = parent.indexOfChild(videoSurface).coerceAtLeast(0)
                parent.addView(
                    texture,
                    (surfaceIndex + 1).coerceAtMost(parent.childCount),
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    ),
                )
                transformedTextureView = texture
            }
        textureView.isVisible = true
        player?.let { exoPlayer ->
            if (!transformedTextureAttachedToPlayer) {
                exoPlayer.setVideoTextureView(textureView)
                transformedTextureAttachedToPlayer = true
            }
        }
        videoSurface.visibility = View.INVISIBLE
        return textureView
    }

    private fun useDefaultVideoOutput(videoSurface: View): View {
        transformedTextureView?.let { textureView ->
            player?.let { exoPlayer ->
                if (transformedTextureAttachedToPlayer) {
                    exoPlayer.clearVideoTextureView(textureView)
                }
            }
            transformedTextureAttachedToPlayer = false
            textureView.isVisible = false
        }
        videoSurface.isVisible = true
        if (videoSurface is SurfaceView) {
            player?.setVideoSurfaceView(videoSurface)
        }
        videoSurface.scaleX = 1f
        videoSurface.scaleY = 1f
        return videoSurface
    }

    private fun activeVideoOutputView(): View? = transformedTextureView
        ?.takeIf { transformedTextureAttachedToPlayer && it.isVisible }
        ?: binding.playerView.videoSurfaceView

    private fun disposeTransformedTextureView() {
        transformedTextureView?.let { textureView ->
            (textureView.parent as? ViewGroup)?.removeView(textureView)
        }
        transformedTextureView = null
        transformedTextureAttachedToPlayer = false
        binding.playerView.videoSurfaceView?.isVisible = true
    }

    private fun requiresTextureOutput(transform: VideoRenderTransform): Boolean =
        abs(transform.scaleX - 1f) >= ZOOM_EPSILON ||
            abs(transform.scaleY - 1f) >= ZOOM_EPSILON

    private fun resetManualZoom(showOsd: Boolean): Boolean {
        if (abs(manualZoomScale - PlayerGesturePolicy.DEFAULT_ZOOM_SCALE) < ZOOM_EPSILON) {
            return false
        }
        manualZoomScale = PlayerGesturePolicy.DEFAULT_ZOOM_SCALE
        zoomPivotXFraction = 0.5f
        zoomPivotYFraction = 0.5f
        applyVideoTransform()
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
        val label = orientationModeLabel()
        binding.rotateButton.contentDescription = getString(
            R.string.control_state_description,
            getString(R.string.action_rotate_screen),
            getString(label),
        )
        if (showOsd) showOsd(R.drawable.ic_screen_rotation, getString(label))
    }

    private fun orientationModeLabel(): Int = when (orientationMode) {
        PlayerOrientationMode.AUTO -> R.string.orientation_auto
        PlayerOrientationMode.LANDSCAPE -> R.string.orientation_landscape
        PlayerOrientationMode.PORTRAIT -> R.string.orientation_portrait
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
            applyVideoTransform()
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

    private fun AlertDialog.Builder.showWithPalette(): AlertDialog = create().also { dialog ->
        showThemedDialog(dialog)
    }

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
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
        const val STATE_AUTO_ADVANCE_PROMPT_DEADLINE = "auto_advance_prompt_deadline"
        const val STATE_ZOOM_SCALE = "zoom_scale"
        const val STATE_ZOOM_PIVOT_X = "zoom_pivot_x"
        const val STATE_ZOOM_PIVOT_Y = "zoom_pivot_y"
        const val STATE_MIRROR_MODE = "mirror_mode"
        const val STATE_VOLUME_BOOST_LEVEL = "volume_boost_level"
        const val STATE_VOLUME_BOOST_WARNING = "volume_boost_warning"
        const val STATE_ORIENTATION_MANUAL = "orientation_manual"
        const val STATE_AUDIO_GROUP = "audio_group"
        const val STATE_AUDIO_TRACK = "audio_track"
        const val STATE_SUBTITLE_GROUP = "subtitle_group"
        const val STATE_SUBTITLE_TRACK = "subtitle_track"
        const val STATE_SUBTITLE_ID = "subtitle_id"
        const val STATE_SUBTITLE_OFFSET = "subtitle_offset"
        const val STATE_AB_POINT_A = "ab_point_a"
        const val STATE_AB_POINT_B = "ab_point_b"
        const val STATE_MANUAL_SUBTITLE_MEDIA_INDEX = "manual_subtitle_media_index"
        const val STATE_MANUAL_SUBTITLE_URI = "manual_subtitle_uri"
        const val STATE_MANUAL_SUBTITLE_NAME = "manual_subtitle_name"
        const val STATE_MANUAL_SUBTITLE_MIME = "manual_subtitle_mime"
        const val STATE_MANUAL_SUBTITLE_SIZE = "manual_subtitle_size"
        const val ACTION_PIP_PLAY_PREFIX =
            "io.github.supermonster003.autojs6.plugin.three.amber.player.action.PIP_PLAY"
        const val ACTION_PIP_PAUSE_PREFIX =
            "io.github.supermonster003.autojs6.plugin.three.amber.player.action.PIP_PAUSE"
        const val PIP_PLAY_REQUEST_CODE = 6_001
        const val PIP_PAUSE_REQUEST_CODE = 6_002
        const val PROGRESS_INTERVAL_MS = 500L
        const val AB_LOOP_PROGRESS_INTERVAL_MS = 50L
        const val FRAME_REPEAT_INTERVAL_MS = 120L
        const val OSD_HIDE_MS = 800L
        const val OSD_LINGER_MS = 400L
        const val UNLOCK_BUTTON_HIDE_MS = 3_000L
        const val SLEEP_TIMER_TICK_MS = 60_000L
        const val AUTO_ADVANCE_PROMPT_TICK_MS = 250L
        const val SCRUB_THUMBNAIL_WIDTH_PX = 320
        const val SCRUB_THUMBNAIL_HEIGHT_PX = 180
        const val SCRUB_PREVIEW_MARGIN_DP = 8f
        const val ZOOM_EPSILON = 0.001f
        const val MANUAL_SUBTITLE_VALIDATION_BUFFER_SIZE = 16 * 1024
        const val SUBTITLE_MEMORY_SCHEME = "threeamber-subtitle"
        const val SUBTITLE_MEMORY_AUTHORITY = "session"
        val SUBTITLE_DOCUMENT_MIME_TYPES = arrayOf("application/*", "text/*")
    }
}
