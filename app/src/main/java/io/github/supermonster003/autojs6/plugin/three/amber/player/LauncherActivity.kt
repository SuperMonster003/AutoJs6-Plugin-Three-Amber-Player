package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistActivity
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistLoader
import io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistParser
import io.github.supermonster003.autojs6.plugin.three.amber.player.databinding.ActivityLauncherBinding
import io.github.supermonster003.autojs6.plugin.three.amber.player.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.three.amber.player.theme.VideoThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.three.amber.player.theme.VideoThemedActivity
import io.github.supermonster003.autojs6.plugin.three.amber.player.update.AppUpdateCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Launcher entry that turns the plugin package into a focused standalone video player. */
class LauncherActivity : VideoThemedActivity() {

    private lateinit var binding: ActivityLauncherBinding
    private var resolvingFile = false

    private val documentPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let(::resolveAndPlay)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        styleViews()
        binding.openVideoButton.setOnClickListener {
            if (!resolvingFile) documentPicker.launch(VIDEO_DOCUMENT_MIME_TYPES)
        }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        AppUpdateCoordinator.maybeCheckAutomatically(this)
    }

    private fun resolveAndPlay(uri: Uri) {
        resolvingFile = true
        renderLoading()
        lifecycleScope.launch {
            val spec = withContext(Dispatchers.IO) { resolve(uri) }
            resolvingFile = false
            renderLoading()
            if (spec == null) {
                Toast.makeText(
                    this@LauncherActivity,
                    R.string.error_no_supported_video,
                    Toast.LENGTH_LONG,
                ).show()
                return@launch
            }
            retainReadPermission(uri)
            runCatching { startActivity(spec) }
                .onFailure {
                    Toast.makeText(
                        this@LauncherActivity,
                        R.string.error_invalid_request,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
        }
    }

    private fun resolve(uri: Uri): Intent? {
        val source = PlaylistLoader.source(this, uri)
        if (PlaylistParser.format(source.displayName, source.mimeType) != null) return PlaylistActivity.intent(this, source)
        val rawMimeType = contentResolver.getType(uri)
        val mimeType = if (rawMimeType == null) {
            "video/*"
        } else {
            VideoRequestPolicy.normalizeVideoMimeType(rawMimeType) ?: return null
        }
        val validated = VideoRequestPolicy.validateExternal(
            ExternalRequestEnvelope(
                action = VideoRequestPolicy.EXTERNAL_VIEW_ACTION,
                targetUri = uri.toString(),
                grants = GrantEnvelope(
                    read = true,
                    write = false,
                    persistable = false,
                    prefix = false,
                ),
                mimeType = mimeType,
            ),
        ) ?: return null
        val readable = runCatching {
            contentResolver.openFileDescriptor(uri, "r")?.use { true } == true
        }.getOrDefault(false)
        if (!readable) return null
        val metadata = ContentPlaybackMetadataResolver.resolve(this, uri)
        return PlaybackForwardingPolicy.fromExternal(
            validated,
            metadata.displayName,
            metadata.declaredSize,
        ).let { VideoIntentFactory.createInternal(this, it) }
    }

    private fun retainReadPermission(uri: Uri) {
        runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun renderLoading() {
        binding.fileLoadingIndicator.visibility = if (resolvingFile) View.VISIBLE else View.GONE
        binding.openVideoButton.isEnabled = !resolvingFile
        binding.settingsButton.isEnabled = !resolvingFile
    }

    private fun styleViews() {
        val palette = videoPalette
        binding.launcherRoot.setBackgroundColor(palette.background)
        binding.launcherContent.setBackgroundColor(palette.background)
        binding.toolbar.setBackgroundColor(palette.appBar)
        binding.toolbar.setTitleTextColor(palette.onAppBar)
        binding.welcomeTitle.setTextColor(palette.onBackground)
        binding.welcomeDescription.setTextColor(palette.onSurfaceVariant)
        binding.fileLoadingIndicator.indeterminateTintList = ColorStateList.valueOf(palette.primary)
        binding.openVideoButton.backgroundTintList = ColorStateList.valueOf(palette.primary)
        binding.openVideoButton.setTextColor(palette.onPrimary)
        binding.openVideoButton.rippleColor = ColorStateList.valueOf(
            VideoThemePaletteGenerator.withAlpha(palette.onPrimary, RIPPLE_ALPHA),
        )
        binding.settingsButton.setTextColor(palette.primary)
        binding.settingsButton.strokeColor = ColorStateList.valueOf(palette.outline)
        binding.settingsButton.rippleColor = ColorStateList.valueOf(
            VideoThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
        )
    }

    private companion object {
        val VIDEO_DOCUMENT_MIME_TYPES = arrayOf("video/*", "text/plain", "application/octet-stream") + PlaylistParser.mimeTypes
        const val RIPPLE_ALPHA = 0x24
    }
}
