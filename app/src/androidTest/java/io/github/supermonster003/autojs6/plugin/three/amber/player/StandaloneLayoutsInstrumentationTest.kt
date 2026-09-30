package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivityAboutBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivityLauncherBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivityReleaseHistoryBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivitySettingsBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivityVideoPlayerBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.BottomSheetVideoQueueBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ItemVideoQueueBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StandaloneLayoutsInstrumentationTest {

    @Test
    fun everyStandaloneScreenInflatesWithTheProductionTheme() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.AppTheme)
            val inflater = LayoutInflater.from(context)
            val settings = ActivitySettingsBinding.inflate(inflater)
            listOf(
                ActivityLauncherBinding.inflate(inflater).root,
                settings.root,
                ActivityReleaseHistoryBinding.inflate(inflater).root,
                ActivityAboutBinding.inflate(inflater).root,
                BottomSheetVideoQueueBinding.inflate(inflater).root,
                ItemVideoQueueBinding.inflate(inflater).root,
            ).forEach { root ->
                assertNotNull(root)
                root.measure(
                    View.MeasureSpec.makeMeasureSpec(TEST_WIDTH_PX, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(TEST_HEIGHT_PX, View.MeasureSpec.EXACTLY),
                )
                root.layout(0, 0, root.measuredWidth, root.measuredHeight)
            }
            assertNotNull(settings.rememberPositionSwitch.thumbDrawable)
            assertNotNull(settings.rememberPositionSwitch.trackDrawable)
            assertNotNull(settings.rememberPlaybackModeSwitch.thumbDrawable)
            assertNotNull(settings.rememberPlaybackModeSwitch.trackDrawable)
            assertNotNull(settings.continueAudioInBackgroundSwitch.thumbDrawable)
            assertNotNull(settings.continueAudioInBackgroundSwitch.trackDrawable)
            assertNotNull(settings.includeSubtitlesInScreenshotSwitch.thumbDrawable)
            assertNotNull(settings.includeSubtitlesInScreenshotSwitch.trackDrawable)
            assertNotNull(settings.autoUpdateSwitch.thumbDrawable)
            assertNotNull(settings.autoUpdateSwitch.trackDrawable)
        }
    }

    @Test
    fun playerAndSettingsRemainUsableAtTwoHundredPercentTextAndDisplayScale() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val target = instrumentation.targetContext
            val configuration = Configuration(target.resources.configuration).apply {
                fontScale = 2f
                densityDpi = (densityDpi * 2).coerceAtMost(960)
            }
            val configured = target.createConfigurationContext(configuration)
            val playerContext = ContextThemeWrapper(configured, R.style.VideoPlayerTheme)
            val player = ActivityVideoPlayerBinding.inflate(LayoutInflater.from(playerContext))
            player.precisionControls.visibility = View.VISIBLE
            player.unlockButton.visibility = View.VISIBLE
            measureAndLayout(player.root)

            listOf(
                player.playPauseButton,
                player.seekBackButton,
                player.seekForwardButton,
                player.speedButton,
                player.resizeButton,
                player.rotateButton,
                player.lockButton,
                player.unlockButton,
                player.frameBackButton,
                player.frameForwardButton,
                player.abLoopButton,
            ).forEach { control ->
                assertTrue(control.measuredWidth > 0)
                assertTrue(control.measuredHeight > 0)
                assertTrue(control.contentDescription?.isNotBlank() == true)
            }
            assertTrue(player.playbackControls.measuredWidth >= player.playbackControlsScroll.measuredWidth)

            val settingsContext = ContextThemeWrapper(configured, R.style.AppTheme)
            val settings = ActivitySettingsBinding.inflate(LayoutInflater.from(settingsContext))
            measureAndLayout(settings.root)
            assertTrue(
                settings.continueAudioInBackgroundSetting.measuredHeight >=
                    settings.continueAudioInBackgroundSwitch.measuredHeight,
            )
            assertTrue(settings.continueAudioInBackgroundTitle.lineCount >= 1)
            assertTrue(settings.continueAudioInBackgroundSummary.lineCount >= 1)
        }
    }

    @Test
    fun playerExposesNamedGestureEquivalentsAndDeterministicForwardFocusOrder() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(
                instrumentation.targetContext,
                R.style.VideoPlayerTheme,
            )
            val player = ActivityVideoPlayerBinding.inflate(LayoutInflater.from(context))
            player.precisionControls.visibility = View.VISIBLE
            measureAndLayout(player.root)

            assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, player.gestureArea.importantForAccessibility)
            listOf(
                player.playPauseButton,
                player.seekBackButton,
                player.seekForwardButton,
                player.frameBackButton,
                player.frameForwardButton,
                player.abLoopButton,
                player.speedButton,
                player.resizeButton,
                player.rotateButton,
                player.lockButton,
                player.unlockButton,
            ).forEach { control ->
                assertTrue(control.contentDescription?.isNotBlank() == true)
            }
            listOf(
                player.cancelAutoAdvanceButton,
                player.retryButton,
                player.openWithOtherAppButton,
            ).forEach { control -> assertTrue(control.text?.isNotBlank() == true) }

            player.timeBar.setDuration(60_000L)
            player.timeBar.setPosition(10_000L)
            assertTrue(player.timeBar.isFocusable)
            assertTrue(
                player.timeBar.createAccessibilityNodeInfo()
                    .contentDescription
                    ?.isNotBlank() == true,
            )
            val focusOrder = listOf(
                player.timeBar to R.id.frame_back_button,
                player.frameBackButton to R.id.ab_loop_button,
                player.abLoopButton to R.id.frame_forward_button,
                player.frameForwardButton to R.id.play_pause_button,
                player.playPauseButton to R.id.seek_back_button,
                player.seekBackButton to R.id.seek_forward_button,
                player.seekForwardButton to R.id.speed_button,
                player.speedButton to R.id.resize_button,
                player.resizeButton to R.id.rotate_button,
                player.rotateButton to R.id.lock_button,
            )
            focusOrder.forEach { (control, nextId) ->
                assertEquals(nextId, control.nextFocusForwardId)
            }

            val menu = player.toolbar.menu
            (0 until menu.size()).forEach { index ->
                assertTrue(menu.getItem(index).title?.isNotBlank() == true)
            }
            listOf(
                R.id.action_brightness,
                R.id.action_volume,
                R.id.action_zoom,
                R.id.action_gesture_settings,
            ).forEach { menuId -> assertNotNull(menu.findItem(menuId)) }
        }
    }

    private fun measureAndLayout(root: View) {
        root.measure(
            View.MeasureSpec.makeMeasureSpec(TEST_WIDTH_PX, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(TEST_HEIGHT_PX, View.MeasureSpec.EXACTLY),
        )
        root.layout(0, 0, root.measuredWidth, root.measuredHeight)
    }

    private companion object {
        const val TEST_WIDTH_PX = 1_080
        const val TEST_HEIGHT_PX = 2_400
    }
}
