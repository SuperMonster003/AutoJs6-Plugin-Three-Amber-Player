package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.LocaleManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppAppearanceController
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.AutoJs6AppearanceClient
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.AutoJs6HostAvailability
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HostAppearanceInstrumentationTest {

    @Test
    fun installedCompatibleAutoJs6PublishesAppearanceToTheOfficialPlugin() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val result = AutoJs6AppearanceClient.query(context)
        assumeTrue(
            "A compatible enabled AutoJs6 settings provider is not available on this device",
            result.availability == AutoJs6HostAvailability.AVAILABLE,
        )

        val snapshot = requireNotNull(result.snapshot)
        assertEquals(true, snapshot.resolvedLanguageTag.isNotBlank())
        val resolution = AppAppearanceController.apply(context, result)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            assertEquals(
                resolution.languageTag.orEmpty(),
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales
                    .toLanguageTags(),
            )
        }
    }
}
