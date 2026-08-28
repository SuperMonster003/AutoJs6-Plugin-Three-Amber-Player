package io.github.supermonster003.autojs6.plugin.videoplayer

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import java.util.Locale

internal data class MediaTrackSelectionKey(
    val groupIndex: Int,
    val trackIndex: Int,
)

/** Owns track discovery, labels and explicit Media3 selection overrides for one playback session. */
@androidx.annotation.OptIn(UnstableApi::class)
internal class PlayerTrackController(
    private val activity: AppCompatActivity,
    private val playerProvider: () -> ExoPlayer?,
    private val onAvailabilityChanged: (hasAudioChoices: Boolean, hasSubtitles: Boolean) -> Unit,
    private val onDialogDismissed: () -> Unit,
) {

    var selectedAudioKey: MediaTrackSelectionKey? = null
        private set

    var selectedSubtitleKey: MediaTrackSelectionKey? = null
        private set

    private var audioOptions = emptyList<TrackOption>()
    private var subtitleOptions = emptyList<TrackOption>()
    private var applyingSelection = false

    fun restoreSelections(
        audioGroupIndex: Int,
        audioTrackIndex: Int,
        subtitleGroupIndex: Int,
        subtitleTrackIndex: Int,
    ) {
        selectedAudioKey = selectionKeyOrNull(audioGroupIndex, audioTrackIndex)
        selectedSubtitleKey = selectionKeyOrNull(subtitleGroupIndex, subtitleTrackIndex)
    }

    /** Embedded subtitles are deliberately opt-in for every newly created player. */
    fun configureInitialParameters(player: ExoPlayer) {
        player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_TEXT)
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            .build()
    }

    fun onTracksChanged(tracks: Tracks) {
        audioOptions = trackOptions(tracks, C.TRACK_TYPE_AUDIO)
        subtitleOptions = trackOptions(tracks, C.TRACK_TYPE_TEXT)
        restoreExplicitSelectionsIfPossible()

        val supportedAudioCount = audioOptions.count(TrackOption::supported)
        val hasSupportedSubtitle = subtitleOptions.any(TrackOption::supported)
        onAvailabilityChanged(supportedAudioCount > 1, hasSupportedSubtitle)
    }

    fun clearAvailability() {
        audioOptions = emptyList()
        subtitleOptions = emptyList()
        onAvailabilityChanged(false, false)
    }

    fun resetSelectionsForMediaItem() {
        selectedAudioKey = null
        selectedSubtitleKey = null
        playerProvider()?.let { player ->
            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .clearOverridesOfType(C.TRACK_TYPE_AUDIO)
                .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                .build()
        }
        clearAvailability()
    }

    fun showAudioDialog() {
        if (audioOptions.count(TrackOption::supported) <= 1) return
        showTrackDialog(
            title = activity.getString(R.string.action_audio_track),
            options = audioOptions,
            checkedIndex = audioOptions.indexOfFirst(TrackOption::selected),
        ) { option ->
            selectedAudioKey = option.key
            applySelection(C.TRACK_TYPE_AUDIO, option)
        }
    }

    fun showSubtitleDialog() {
        if (subtitleOptions.none(TrackOption::supported)) return
        val options = listOf(
            DialogOption(
                label = activity.getString(R.string.subtitle_off),
                supported = true,
                trackOption = null,
            ),
        ) + subtitleOptions.map { option ->
            DialogOption(option.label, option.supported, option)
        }
        val selectedIndex = subtitleOptions.indexOfFirst(TrackOption::selected)
            .takeIf { it >= 0 }
            ?.plus(1)
            ?: 0
        showDialog(
            title = activity.getString(R.string.action_subtitle_track),
            options = options,
            checkedIndex = selectedIndex,
        ) { selected ->
            val option = selected.trackOption
            if (option == null) {
                selectedSubtitleKey = null
                disableSubtitles()
            } else {
                selectedSubtitleKey = option.key
                applySelection(C.TRACK_TYPE_TEXT, option)
            }
        }
    }

    private fun showTrackDialog(
        title: String,
        options: List<TrackOption>,
        checkedIndex: Int,
        onSelected: (TrackOption) -> Unit,
    ) {
        showDialog(
            title,
            options.map { option -> DialogOption(option.label, option.supported, option) },
            checkedIndex,
        ) { selected -> selected.trackOption?.let(onSelected) }
    }

    private fun showDialog(
        title: String,
        options: List<DialogOption>,
        checkedIndex: Int,
        onSelected: (DialogOption) -> Unit,
    ) {
        val adapter = EnabledSingleChoiceAdapter(activity, options)
        val dialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setSingleChoiceItems(adapter, checkedIndex) { activeDialog, which ->
                val option = options[which]
                if (!option.supported) {
                    Toast.makeText(activity, R.string.track_not_supported, Toast.LENGTH_SHORT).show()
                    return@setSingleChoiceItems
                }
                onSelected(option)
                activeDialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.setOnDismissListener { onDialogDismissed() }
        dialog.show()
    }

    private fun restoreExplicitSelectionsIfPossible() {
        if (applyingSelection) return
        selectedAudioKey?.let { key ->
            audioOptions.firstOrNull { it.key == key && it.supported && !it.selected }
                ?.let { applySelection(C.TRACK_TYPE_AUDIO, it) }
        }
        selectedSubtitleKey?.let { key ->
            subtitleOptions.firstOrNull { it.key == key && it.supported && !it.selected }
                ?.let { applySelection(C.TRACK_TYPE_TEXT, it) }
        }
    }

    private fun applySelection(trackType: Int, option: TrackOption) {
        val player = playerProvider() ?: return
        applyingSelection = true
        try {
            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .setTrackTypeDisabled(trackType, false)
                .setOverrideForType(
                    TrackSelectionOverride(option.group.mediaTrackGroup, option.key.trackIndex),
                )
                .build()
        } finally {
            applyingSelection = false
        }
    }

    private fun disableSubtitles() {
        val player = playerProvider() ?: return
        applyingSelection = true
        try {
            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                .build()
        } finally {
            applyingSelection = false
        }
    }

    private fun trackOptions(tracks: Tracks, trackType: Int): List<TrackOption> {
        var typeIndex = 0
        return buildList {
            tracks.groups.forEachIndexed { groupIndex, group ->
                if (group.type != trackType) return@forEachIndexed
                repeat(group.length) { trackIndex ->
                    typeIndex += 1
                    val format = group.getTrackFormat(trackIndex)
                    val supported = group.isTrackSupported(trackIndex, true)
                    val rawLabel = buildTrackLabel(format, trackType, typeIndex)
                    add(
                        TrackOption(
                            key = MediaTrackSelectionKey(groupIndex, trackIndex),
                            group = group,
                            label = if (supported) {
                                rawLabel
                            } else {
                                activity.getString(R.string.track_unsupported, rawLabel)
                            },
                            supported = supported,
                            selected = group.isTrackSelected(trackIndex),
                        ),
                    )
                }
            }
        }
    }

    private fun buildTrackLabel(format: Format, trackType: Int, typeIndex: Int): String {
        val fallback = activity.getString(
            if (trackType == C.TRACK_TYPE_AUDIO) R.string.audio_track_number else R.string.subtitle_track_number,
            typeIndex,
        )
        val language = displayLanguage(format.language)
        val primary = format.label?.trim()?.takeIf(String::isNotEmpty) ?: language ?: fallback
        val details = buildList {
            if (language != null && !primary.equals(language, ignoreCase = true)) add(language)
            if (trackType == C.TRACK_TYPE_AUDIO && format.channelCount > 0) {
                add(
                    when (format.channelCount) {
                        1 -> activity.getString(R.string.audio_channels_mono)
                        2 -> activity.getString(R.string.audio_channels_stereo)
                        else -> activity.getString(R.string.audio_channels_count, format.channelCount)
                    },
                )
            }
        }
        return if (details.isEmpty()) primary else "$primary · ${details.joinToString(" · ")}"
    }

    private fun displayLanguage(languageTag: String?): String? {
        val tag = languageTag?.trim()?.takeIf { it.isNotEmpty() && it != "und" } ?: return null
        val locale = Locale.forLanguageTag(tag.replace('_', '-'))
        val displayLocale = activity.resources.configuration.locales[0]
        return locale.getDisplayLanguage(displayLocale).takeIf(String::isNotBlank) ?: tag
    }

    private fun selectionKeyOrNull(groupIndex: Int, trackIndex: Int): MediaTrackSelectionKey? =
        if (groupIndex >= 0 && trackIndex >= 0) {
            MediaTrackSelectionKey(groupIndex, trackIndex)
        } else {
            null
        }

    private data class TrackOption(
        val key: MediaTrackSelectionKey,
        val group: Tracks.Group,
        val label: String,
        val supported: Boolean,
        val selected: Boolean,
    )

    private data class DialogOption(
        val label: String,
        val supported: Boolean,
        val trackOption: TrackOption?,
    )

    private class EnabledSingleChoiceAdapter(
        activity: AppCompatActivity,
        private val options: List<DialogOption>,
    ) : ArrayAdapter<String>(
        activity,
        android.R.layout.simple_list_item_single_choice,
        options.map(DialogOption::label),
    ) {

        override fun areAllItemsEnabled(): Boolean = false

        override fun isEnabled(position: Int): Boolean = options[position].supported

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            view.isEnabled = isEnabled(position)
            view.alpha = if (isEnabled(position)) 1f else 0.45f
            return view
        }
    }
}
