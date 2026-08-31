package io.github.supermonster003.autojs6.plugin.threeemberplayer.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import io.github.supermonster003.autojs6.plugin.threeemberplayer.BackgroundPlaybackCoordinator
import io.github.supermonster003.autojs6.plugin.threeemberplayer.BackgroundPlaybackPermissionPolicy
import io.github.supermonster003.autojs6.plugin.threeemberplayer.PlaybackPositionStore
import io.github.supermonster003.autojs6.plugin.threeemberplayer.PlayerSettingsStore
import io.github.supermonster003.autojs6.plugin.threeemberplayer.R
import io.github.supermonster003.autojs6.plugin.threeemberplayer.SubtitleBackgroundStyle
import io.github.supermonster003.autojs6.plugin.threeemberplayer.SubtitleBottomMargin
import io.github.supermonster003.autojs6.plugin.threeemberplayer.SubtitleForegroundColor
import io.github.supermonster003.autojs6.plugin.threeemberplayer.SubtitleStyleApplier
import io.github.supermonster003.autojs6.plugin.threeemberplayer.SubtitleStyleSettings
import io.github.supermonster003.autojs6.plugin.threeemberplayer.SubtitleTextScale
import io.github.supermonster003.autojs6.plugin.threeemberplayer.databinding.ActivitySettingsBinding
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.AutoJs6AppearanceClient
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.AutoJs6AppearanceResult
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.AutoJs6HostAvailability
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.ThemePreferenceStore
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.ThemePresetCatalog
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.ThemeSourceMode
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.VideoThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.VideoThemePicker
import io.github.supermonster003.autojs6.plugin.threeemberplayer.theme.VideoThemedActivity
import io.github.supermonster003.autojs6.plugin.threeemberplayer.update.AppUpdateCoordinator
import io.github.supermonster003.autojs6.plugin.threeemberplayer.update.AppUpdateStore
import java.util.Locale
import org.autojs.plugin.common.api.AutoJs6HostSettingsContract as HostContract

/** Standalone-app settings. Host-following options remain visible when unavailable. */
@androidx.annotation.OptIn(UnstableApi::class)
class SettingsActivity : VideoThemedActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceStore: AppPreferenceStore
    private lateinit var playerSettingsStore: PlayerSettingsStore
    private var subtitleStyle = SubtitleStyleSettings()
    private var hostResult = AutoJs6AppearanceResult(AutoJs6HostAvailability.NOT_INSTALLED)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted && BackgroundPlaybackPermissionPolicy.canPostControls(this)) {
            setBackgroundAudioEnabled(true)
        } else {
            setBackgroundAudioEnabled(false)
            Toast.makeText(
                this,
                R.string.background_audio_notification_permission_required,
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = AppPreferenceStore(this)
        playerSettingsStore = PlayerSettingsStore(this)
        subtitleStyle = playerSettingsStore.readSubtitleStyle()
        hostResult = AutoJs6AppearanceClient.query(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        styleViews()
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        bindRows()
        renderValues()
    }

    override fun onResume() {
        super.onResume()
        if (!::binding.isInitialized) return
        if (playerSettingsStore.continueAudioInBackground &&
            !BackgroundPlaybackPermissionPolicy.canPostControls(this)
        ) {
            setBackgroundAudioEnabled(false)
        }
        hostResult = AutoJs6AppearanceClient.query(this)
        renderValues()
    }

    private fun bindRows() {
        bindAccessibleSwitch(binding.rememberPositionSetting, binding.rememberPositionSwitch)
        bindAccessibleSwitch(binding.rememberPlaybackModeSetting, binding.rememberPlaybackModeSwitch)
        bindAccessibleSwitch(
            binding.continueAudioInBackgroundSetting,
            binding.continueAudioInBackgroundSwitch,
        )
        bindAccessibleSwitch(
            binding.includeSubtitlesInScreenshotSetting,
            binding.includeSubtitlesInScreenshotSwitch,
        )
        bindAccessibleSwitch(binding.autoUpdateSetting, binding.autoUpdateSwitch)
        binding.languageSetting.setOnClickListener { showLanguageDialog() }
        binding.nightModeSetting.setOnClickListener { showNightModeDialog() }
        binding.themeColorSetting.setOnClickListener {
            VideoThemePicker(this) { recreate() }.show()
        }
        binding.rememberPositionSetting.setOnClickListener {
            val enabled = !binding.rememberPositionSwitch.isChecked
            preferenceStore.rememberPlaybackPosition = enabled
            binding.rememberPositionSwitch.isChecked = enabled
            if (!enabled) PlaybackPositionStore(this).clearAll()
            renderSwitchStates()
        }
        binding.rememberPlaybackModeSetting.setOnClickListener {
            val enabled = !binding.rememberPlaybackModeSwitch.isChecked
            playerSettingsStore.setRememberPlaybackMode(enabled)
            binding.rememberPlaybackModeSwitch.isChecked = enabled
            renderSwitchStates()
        }
        binding.continueAudioInBackgroundSetting.setOnClickListener {
            val enabled = !binding.continueAudioInBackgroundSwitch.isChecked
            if (enabled) requestBackgroundAudioEnablement() else setBackgroundAudioEnabled(false)
        }
        binding.includeSubtitlesInScreenshotSetting.setOnClickListener {
            val enabled = !binding.includeSubtitlesInScreenshotSwitch.isChecked
            playerSettingsStore.setIncludeSubtitlesInScreenshots(enabled)
            binding.includeSubtitlesInScreenshotSwitch.isChecked = enabled
            renderSwitchStates()
        }
        binding.subtitleTextSizeSetting.setOnClickListener { showSubtitleTextSizeDialog() }
        binding.subtitleForegroundSetting.setOnClickListener { showSubtitleForegroundDialog() }
        binding.subtitleBackgroundSetting.setOnClickListener { showSubtitleBackgroundDialog() }
        binding.subtitleBottomMarginSetting.setOnClickListener { showSubtitleBottomMarginDialog() }
        binding.checkUpdateSetting.setOnClickListener {
            AppUpdateCoordinator.checkManually(this)
        }
        binding.autoUpdateSetting.setOnClickListener {
            val enabled = !binding.autoUpdateSwitch.isChecked
            preferenceStore.autoCheckUpdates = enabled
            binding.autoUpdateSwitch.isChecked = enabled
            renderSwitchStates()
        }
        binding.ignoredUpdatesSetting.setOnClickListener {
            AppUpdateCoordinator.manageIgnoredUpdates(this) { renderValues() }
        }
        binding.releaseHistorySetting.setOnClickListener {
            startActivity(Intent(this, ReleaseHistoryActivity::class.java))
        }
        binding.aboutSetting.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun showLanguageDialog() {
        val labels = buildList {
            add(getString(R.string.follow_autojs6))
            add(getString(R.string.follow_system))
            AppPreferenceStore.SUPPORTED_LANGUAGE_TAGS.forEach { tag -> add(displayLanguageName(tag)) }
        }
        val preference = preferenceStore.language()
        val checked = when (preference.mode) {
            AppLanguageMode.AUTOJS6 -> 0
            AppLanguageMode.SYSTEM -> 1
            AppLanguageMode.SPECIFIC -> AppPreferenceStore.SUPPORTED_LANGUAGE_TAGS
                .indexOf(preference.languageTag)
                .takeIf { it >= 0 }
                ?.plus(2)
                ?: 1
        }
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.setting_language)
            .setSingleChoiceItems(
                DisabledChoiceAdapter(labels, disableFirst = !hostResult.available),
                checked,
            ) { selectedDialog, which ->
                val selected = when (which) {
                    0 -> AppLanguagePreference(AppLanguageMode.AUTOJS6)
                    1 -> AppLanguagePreference(AppLanguageMode.SYSTEM)
                    else -> AppPreferenceStore.SUPPORTED_LANGUAGE_TAGS.getOrNull(which - 2)
                        ?.let { tag -> AppLanguagePreference(AppLanguageMode.SPECIFIC, tag) }
                        ?: return@setSingleChoiceItems
                }
                if (preferenceStore.saveLanguage(selected)) {
                    AppAppearanceController.apply(this, hostResult)
                }
                selectedDialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        showThemedDialog(dialog)
    }

    private fun showNightModeDialog() {
        val labels = listOf(
            getString(R.string.follow_autojs6),
            getString(R.string.follow_system),
            getString(R.string.night_mode_light),
            getString(R.string.night_mode_dark),
        )
        val values = listOf(
            AppNightMode.AUTOJS6,
            AppNightMode.SYSTEM,
            AppNightMode.LIGHT,
            AppNightMode.DARK,
        )
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.setting_night_mode)
            .setSingleChoiceItems(
                DisabledChoiceAdapter(labels, disableFirst = !hostResult.available),
                values.indexOf(preferenceStore.nightMode()),
            ) { selectedDialog, which ->
                values.getOrNull(which)?.let { value ->
                    if (preferenceStore.saveNightMode(value)) {
                        AppAppearanceController.apply(this, hostResult)
                    }
                }
                selectedDialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        showThemedDialog(dialog)
    }

    private fun renderValues() {
        binding.languageSummary.text = languageSummary()
        binding.nightModeSummary.text = nightModeSummary()
        binding.themeColorSummary.text = themeSummary()
        binding.rememberPositionSwitch.isChecked = preferenceStore.rememberPlaybackPosition
        binding.rememberPlaybackModeSwitch.isChecked = playerSettingsStore.rememberPlaybackMode
        binding.continueAudioInBackgroundSwitch.isChecked =
            playerSettingsStore.continueAudioInBackground
        binding.includeSubtitlesInScreenshotSwitch.isChecked =
            playerSettingsStore.includeSubtitlesInScreenshots
        binding.autoUpdateSwitch.isChecked = preferenceStore.autoCheckUpdates
        renderSwitchStates()
        renderSubtitleStyle()
        val ignoredCount = AppUpdateStore(this).ignoredVersions().size
        binding.ignoredUpdatesSummary.text = resources.getQuantityString(
            R.plurals.ignored_update_count,
            ignoredCount,
            ignoredCount,
        )
    }

    private fun bindAccessibleSwitch(row: View, switch: SwitchMaterial) {
        ViewCompat.setAccessibilityDelegate(
            row,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat,
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.className = Switch::class.java.name
                    info.isCheckable = true
                    info.isChecked = switch.isChecked
                }
            },
        )
    }

    private fun requestBackgroundAudioEnablement() {
        if (BackgroundPlaybackPermissionPolicy.canPostControls(this)) {
            setBackgroundAudioEnabled(true)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        setBackgroundAudioEnabled(false)
        Toast.makeText(
            this,
            R.string.background_audio_notification_permission_required,
            Toast.LENGTH_LONG,
        ).show()
        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                data = "package:$packageName".toUri()
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            },
        )
    }

    private fun setBackgroundAudioEnabled(enabled: Boolean) {
        playerSettingsStore.setContinueAudioInBackground(enabled)
        binding.continueAudioInBackgroundSwitch.isChecked = enabled
        if (!enabled) BackgroundPlaybackCoordinator.stopForDisabledSetting()
        renderSwitchStates()
    }

    private fun renderSwitchStates() {
        listOf(
            binding.rememberPositionSetting to binding.rememberPositionSwitch,
            binding.rememberPlaybackModeSetting to binding.rememberPlaybackModeSwitch,
            binding.continueAudioInBackgroundSetting to binding.continueAudioInBackgroundSwitch,
            binding.includeSubtitlesInScreenshotSetting to binding.includeSubtitlesInScreenshotSwitch,
            binding.autoUpdateSetting to binding.autoUpdateSwitch,
        ).forEach { (row, switch) ->
            ViewCompat.setStateDescription(
                row,
                getString(if (switch.isChecked) R.string.accessibility_on else R.string.accessibility_off),
            )
        }
    }

    private fun showSubtitleTextSizeDialog() {
        val values = SubtitleTextScale.entries
        showSubtitleChoiceDialog(
            title = R.string.setting_subtitle_text_size,
            labels = values.map(::subtitleTextScaleLabel),
            checkedIndex = values.indexOf(subtitleStyle.textScale),
        ) { index -> subtitleStyle.copy(textScale = values[index], customized = true) }
    }

    private fun showSubtitleForegroundDialog() {
        val values = SubtitleForegroundColor.entries
        showSubtitleChoiceDialog(
            title = R.string.setting_subtitle_foreground,
            labels = values.map(::subtitleForegroundLabel),
            checkedIndex = values.indexOf(subtitleStyle.foregroundColor),
        ) { index -> subtitleStyle.copy(foregroundColor = values[index], customized = true) }
    }

    private fun showSubtitleBackgroundDialog() {
        val values = SubtitleBackgroundStyle.entries
        showSubtitleChoiceDialog(
            title = R.string.setting_subtitle_background,
            labels = values.map(::subtitleBackgroundLabel),
            checkedIndex = values.indexOf(subtitleStyle.backgroundStyle),
        ) { index -> subtitleStyle.copy(backgroundStyle = values[index], customized = true) }
    }

    private fun showSubtitleBottomMarginDialog() {
        val values = SubtitleBottomMargin.entries
        showSubtitleChoiceDialog(
            title = R.string.setting_subtitle_bottom_margin,
            labels = values.map(::subtitleBottomMarginLabel),
            checkedIndex = values.indexOf(subtitleStyle.bottomMargin),
        ) { index -> subtitleStyle.copy(bottomMargin = values[index], customized = true) }
    }

    private fun showSubtitleChoiceDialog(
        title: Int,
        labels: List<String>,
        checkedIndex: Int,
        selectedStyle: (Int) -> SubtitleStyleSettings,
    ) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setSingleChoiceItems(labels.toTypedArray(), checkedIndex) { selectedDialog, which ->
                subtitleStyle = selectedStyle(which)
                playerSettingsStore.writeSubtitleStyle(subtitleStyle)
                renderSubtitleStyle()
                selectedDialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        showThemedDialog(dialog)
    }

    private fun renderSubtitleStyle() {
        binding.subtitleTextSizeSummary.text = subtitleTextScaleLabel(subtitleStyle.textScale)
        binding.subtitleForegroundSummary.text = subtitleForegroundLabel(subtitleStyle.foregroundColor)
        binding.subtitleBackgroundSummary.text = subtitleBackgroundLabel(subtitleStyle.backgroundStyle)
        binding.subtitleBottomMarginSummary.text = subtitleBottomMarginLabel(subtitleStyle.bottomMargin)
        SubtitleStyleApplier.apply(binding.subtitlePreview, subtitleStyle, forcePreview = true)
        binding.subtitlePreview.setCues(
            listOf(Cue.Builder().setText(getString(R.string.subtitle_preview_text)).build()),
        )
    }

    private fun subtitleTextScaleLabel(value: SubtitleTextScale): String = getString(
        when (value) {
            SubtitleTextScale.PERCENT_75 -> R.string.subtitle_text_size_75
            SubtitleTextScale.PERCENT_100 -> R.string.subtitle_text_size_100
            SubtitleTextScale.PERCENT_125 -> R.string.subtitle_text_size_125
            SubtitleTextScale.PERCENT_150 -> R.string.subtitle_text_size_150
        },
    )

    private fun subtitleForegroundLabel(value: SubtitleForegroundColor): String = getString(
        when (value) {
            SubtitleForegroundColor.WHITE -> R.string.subtitle_color_white
            SubtitleForegroundColor.YELLOW -> R.string.subtitle_color_yellow
            SubtitleForegroundColor.CYAN -> R.string.subtitle_color_cyan
            SubtitleForegroundColor.GREEN -> R.string.subtitle_color_green
        },
    )

    private fun subtitleBackgroundLabel(value: SubtitleBackgroundStyle): String = getString(
        when (value) {
            SubtitleBackgroundStyle.OPAQUE -> R.string.subtitle_background_opaque
            SubtitleBackgroundStyle.TRANSLUCENT -> R.string.subtitle_background_translucent
            SubtitleBackgroundStyle.NONE -> R.string.subtitle_background_none
        },
    )

    private fun subtitleBottomMarginLabel(value: SubtitleBottomMargin): String = getString(
        when (value) {
            SubtitleBottomMargin.PERCENT_0 -> R.string.subtitle_bottom_margin_0
            SubtitleBottomMargin.PERCENT_4 -> R.string.subtitle_bottom_margin_4
            SubtitleBottomMargin.PERCENT_8 -> R.string.subtitle_bottom_margin_8
        },
    )

    private fun languageSummary(): String = when (val preference = preferenceStore.language()) {
        is AppLanguagePreference -> when (preference.mode) {
            AppLanguageMode.AUTOJS6 -> hostResult.snapshot?.resolvedLanguageTag?.let { tag ->
                getString(R.string.follow_autojs6_value, displayLanguageName(tag))
            } ?: unavailableFollowSummary(getString(R.string.follow_system))
            AppLanguageMode.SYSTEM -> getString(R.string.follow_system)
            AppLanguageMode.SPECIFIC -> displayLanguageName(preference.languageTag.orEmpty())
        }
    }

    private fun nightModeSummary(): String = when (preferenceStore.nightMode()) {
        AppNightMode.AUTOJS6 -> hostResult.snapshot?.let { snapshot ->
            val value = when (snapshot.darkModePolicy) {
                HostContract.DARK_MODE_FOLLOW_SYSTEM -> getString(R.string.follow_system)
                HostContract.DARK_MODE_DARK -> getString(R.string.night_mode_dark)
                HostContract.DARK_MODE_LIGHT -> getString(R.string.night_mode_light)
                else -> getString(
                    if (snapshot.darkModeActive) R.string.night_mode_dark else R.string.night_mode_light,
                )
            }
            getString(R.string.follow_autojs6_value, value)
        } ?: unavailableFollowSummary(getString(R.string.follow_system))
        AppNightMode.SYSTEM -> getString(R.string.follow_system)
        AppNightMode.LIGHT -> getString(R.string.night_mode_light)
        AppNightMode.DARK -> getString(R.string.night_mode_dark)
    }

    private fun themeSummary(): String {
        val preference = ThemePreferenceStore(this).load()
        return when (preference.mode) {
            ThemeSourceMode.AUTOJS6 -> {
                val color = hostResult.snapshot?.themeColorPrimary
                    ?: VideoThemePaletteGenerator.AUTOJS6_FALLBACK_SOURCE
                getString(
                    if (hostResult.available) {
                        R.string.theme_source_autojs6_available
                    } else {
                        R.string.theme_source_autojs6_unavailable
                    },
                    VideoThemePaletteGenerator.colorHex(color),
                )
            }
            ThemeSourceMode.PRESET -> {
                val index = ThemePresetCatalog.colors.indexOfFirst { preset ->
                    preset.key == preference.presetKey
                }
                resources.getStringArray(R.array.theme_preset_names).getOrNull(index)
                    ?: VideoThemePaletteGenerator.colorHex(videoPalette.source)
            }
            ThemeSourceMode.CUSTOM -> getString(
                R.string.theme_custom_value,
                VideoThemePaletteGenerator.colorHex(preference.customColor),
            )
        }
    }

    private fun unavailableFollowSummary(fallback: String): String = getString(
        R.string.follow_autojs6_unavailable_value,
        hostUnavailableReason(),
        fallback,
    )

    private fun hostUnavailableReason(): String = getString(
        when (hostResult.availability) {
            AutoJs6HostAvailability.NOT_INSTALLED -> R.string.autojs6_not_installed
            AutoJs6HostAvailability.DISABLED -> R.string.autojs6_disabled
            AutoJs6HostAvailability.CONTRACT_UNAVAILABLE -> R.string.autojs6_settings_unavailable
            AutoJs6HostAvailability.AVAILABLE -> R.string.autojs6_settings_available
        },
    )

    private fun displayLanguageName(tag: String): String {
        val normalized = AppPreferenceStore.normalizeSupportedLanguageTag(tag) ?: tag
        val locale = Locale.forLanguageTag(normalized)
        return locale.getDisplayName(locale).replaceFirstChar { character ->
            if (character.isLowerCase()) character.titlecase(locale) else character.toString()
        }
    }

    private fun styleViews() {
        val palette = videoPalette
        binding.settingsRoot.setBackgroundColor(palette.background)
        binding.settingsContent.setBackgroundColor(palette.background)
        binding.toolbar.setBackgroundColor(palette.appBar)
        binding.toolbar.setTitleTextColor(palette.onAppBar)
        binding.toolbar.navigationIcon = binding.toolbar.navigationIcon?.tinted(palette.onAppBar)
        listOf(
            binding.appearanceSection,
            binding.playbackSection,
            binding.subtitleStyleSection,
            binding.updatesSection,
            binding.aboutSection,
        ).forEach { view -> view.setTextColor(palette.primary) }
        listOf(
            binding.languageTitle,
            binding.nightModeTitle,
            binding.themeColorTitle,
            binding.rememberPositionTitle,
            binding.rememberPlaybackModeTitle,
            binding.continueAudioInBackgroundTitle,
            binding.includeSubtitlesInScreenshotTitle,
            binding.subtitleTextSizeTitle,
            binding.subtitleForegroundTitle,
            binding.subtitleBackgroundTitle,
            binding.subtitleBottomMarginTitle,
            binding.checkUpdateTitle,
            binding.autoUpdateTitle,
            binding.ignoredUpdatesTitle,
            binding.releaseHistoryTitle,
            binding.aboutTitle,
        ).forEach { view -> view.setTextColor(palette.onSurface) }
        listOf(
            binding.languageSummary,
            binding.nightModeSummary,
            binding.themeColorSummary,
            binding.rememberPositionSummary,
            binding.rememberPlaybackModeSummary,
            binding.continueAudioInBackgroundSummary,
            binding.includeSubtitlesInScreenshotSummary,
            binding.subtitleTextSizeSummary,
            binding.subtitleForegroundSummary,
            binding.subtitleBackgroundSummary,
            binding.subtitleBottomMarginSummary,
            binding.checkUpdateSummary,
            binding.autoUpdateSummary,
            binding.ignoredUpdatesSummary,
            binding.releaseHistorySummary,
            binding.aboutSummary,
        ).forEach { view -> view.setTextColor(palette.onSurfaceVariant) }
        styleSwitch(binding.rememberPositionSwitch)
        styleSwitch(binding.rememberPlaybackModeSwitch)
        styleSwitch(binding.continueAudioInBackgroundSwitch)
        styleSwitch(binding.includeSubtitlesInScreenshotSwitch)
        styleSwitch(binding.autoUpdateSwitch)
    }

    private fun styleSwitch(view: SwitchMaterial) {
        view.thumbTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(videoPalette.primary, videoPalette.outline),
        )
        view.trackTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(videoPalette.primaryContainer, videoPalette.surfaceContainerHighest),
        )
    }

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
    }

    private inner class DisabledChoiceAdapter(
        values: List<String>,
        private val disableFirst: Boolean,
    ) : ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, values) {
        override fun areAllItemsEnabled(): Boolean = !disableFirst

        override fun isEnabled(position: Int): Boolean = !(disableFirst && position == 0)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
            super.getView(position, convertView, parent).also { row ->
                row.isEnabled = isEnabled(position)
                row.alpha = if (isEnabled(position)) 1f else DISABLED_ALPHA
                (row as? TextView)?.setTextColor(
                    if (isEnabled(position)) videoPalette.onSurface else videoPalette.onSurfaceVariant,
                )
            }
    }

    private companion object {
        const val DISABLED_ALPHA = 0.5f
    }
}
