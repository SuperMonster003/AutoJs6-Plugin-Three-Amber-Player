package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ActivityAboutBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ActivityLauncherBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ActivityReleaseHistoryBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ActivitySettingsBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.BottomSheetVideoQueueBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ItemVideoQueueBinding
import org.junit.Assert.assertNotNull
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
            assertNotNull(settings.autoUpdateSwitch.thumbDrawable)
            assertNotNull(settings.autoUpdateSwitch.trackDrawable)
        }
    }

    private companion object {
        const val TEST_WIDTH_PX = 1_080
        const val TEST_HEIGHT_PX = 2_400
    }
}
