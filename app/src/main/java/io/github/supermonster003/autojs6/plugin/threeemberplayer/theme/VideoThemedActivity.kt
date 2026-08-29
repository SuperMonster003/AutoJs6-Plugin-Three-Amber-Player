package io.github.supermonster003.autojs6.plugin.threeemberplayer.theme

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppAppearanceController
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppLanguageMode
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppNightMode
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppPreferenceStore

/** Activity base that follows local settings and live AutoJs6 appearance changes. */
abstract class VideoThemedActivity : AppCompatActivity() {

    internal lateinit var resolvedVideoTheme: ResolvedVideoTheme
        private set

    internal val videoPalette: VideoThemePalette
        get() = resolvedVideoTheme.palette

    private var appliedThemeRevision = Long.MIN_VALUE
    private var appliedAppearanceRevision = Long.MIN_VALUE
    private var appliedHostSignature: Int? = null
    private var recreationRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Resolve again at the Activity boundary: the host provider may not have been ready when a
        // cold Application started, and locale/night mode must precede resource inflation.
        AppAppearanceController.apply(this)
        super.onCreate(savedInstanceState)
        resolvedVideoTheme = VideoThemeResolver.resolve(this)
        appliedThemeRevision = ThemePreferenceStore(this).revision()
        val appStore = AppPreferenceStore(this)
        appliedAppearanceRevision = appStore.appearanceRevision()
        appliedHostSignature = resolvedVideoTheme.hostResult?.hashCode()
            ?: hostResultIfFollowed(appStore)?.hashCode()
        applyWindowPalette(videoPalette)
    }

    override fun onResume() {
        super.onResume()
        if (recreationRequested) return
        val themeStore = ThemePreferenceStore(this)
        val themePreference = themeStore.load()
        val appStore = AppPreferenceStore(this)
        val hostResult = if (followsHost(themePreference, appStore)) {
            AutoJs6AppearanceClient.query(this)
        } else {
            null
        }
        val hostSignature = hostResult?.hashCode()
        if (
            themeStore.revision() != appliedThemeRevision ||
            appStore.appearanceRevision() != appliedAppearanceRevision ||
            hostSignature != appliedHostSignature
        ) {
            recreationRequested = true
            hostResult?.let { result -> AppAppearanceController.apply(this, result) }
            recreate()
        }
    }

    @Suppress("DEPRECATION") // Required below API 35; all layouts still handle current insets.
    internal fun applyWindowPalette(palette: VideoThemePalette) {
        window.statusBarColor = palette.appBar
        window.navigationBarColor = palette.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = palette.outlineVariant
        }
        val lightStatusBar = VideoThemePaletteGenerator.bestMonochromeForeground(palette.appBar) ==
            OPAQUE_BLACK
        val lightNavigationBar = VideoThemePaletteGenerator.bestMonochromeForeground(palette.background) ==
            OPAQUE_BLACK
        val decorView = window.decorView
        decorView.post {
            if (isFinishing || isDestroyed) return@post
            applySystemBarIconAppearance(decorView, lightStatusBar, lightNavigationBar)
        }
    }

    internal fun showThemedDialog(
        dialog: AlertDialog,
        afterShown: (AlertDialog) -> Unit = {},
    ) {
        dialog.setOnShowListener {
            VideoThemeViewStyler.styleDialog(dialog, videoPalette)
            afterShown(dialog)
        }
        dialog.show()
    }

    internal fun restyleDialog(dialog: AlertDialog) {
        VideoThemeViewStyler.styleDialog(dialog, videoPalette)
    }

    internal fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun hostResultIfFollowed(store: AppPreferenceStore): AutoJs6AppearanceResult? =
        if (
            store.language().mode == AppLanguageMode.AUTOJS6 ||
            store.nightMode() == AppNightMode.AUTOJS6
        ) {
            AutoJs6AppearanceClient.query(this)
        } else {
            null
        }

    private fun followsHost(
        themePreference: ThemeSourcePreference,
        store: AppPreferenceStore,
    ): Boolean = themePreference.mode == ThemeSourceMode.AUTOJS6 ||
        store.language().mode == AppLanguageMode.AUTOJS6 ||
        store.nightMode() == AppNightMode.AUTOJS6

    private fun applySystemBarIconAppearance(
        decorView: View,
        lightStatusBar: Boolean,
        lightNavigationBar: Boolean,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            var appearance = 0
            var mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            if (lightStatusBar) {
                appearance = appearance or WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            }
            mask = mask or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            if (lightNavigationBar) {
                appearance = appearance or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            }
            decorView.windowInsetsController?.setSystemBarsAppearance(appearance, mask)
            return
        }

        @Suppress("DEPRECATION")
        var visibility = decorView.systemUiVisibility
        @Suppress("DEPRECATION")
        visibility = if (lightStatusBar) {
            visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            visibility = if (lightNavigationBar) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
        @Suppress("DEPRECATION")
        run { decorView.systemUiVisibility = visibility }
    }

    private companion object {
        const val OPAQUE_BLACK = -0x1000000
    }
}
