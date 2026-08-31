package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.content.Context
import android.os.Build
import android.os.Bundle
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionValues

internal object ThreeEmberPlayerPlugin {
    const val ID = "video-player"
    const val ACTION_ID = "play-video"
    const val ACTION_SELECTION_ID = "play-video-selection"
    const val VARIANT = "default"
    const val PROTOCOL_VERSION = 12
    const val REQUIRED_HOST_VERSION = 5276L
    const val LABEL_RESOURCE_NAME = "action_play_video"
    const val LABEL_FALLBACK = "Play video"
    const val SELECTION_LABEL_RESOURCE_NAME = "action_play_selected"
    const val SELECTION_LABEL_FALLBACK = "Play selected"
    const val ACTIVITY_CLASS_NAME =
        "io.github.supermonster003.autojs6.plugin.threeemberplayer.ExplorerActionActivity"
    const val ACTION_PRIORITY = 20

    val MIME_TYPES = arrayOf("video/*")

    val EXTENSIONS = arrayOf(
        "mp4",
        "mpeg4",
        "mpg4",
        "avi",
        "mkv",
        "mov",
        "flv",
        "webm",
        "m4v",
        "3gp",
        "mpeg",
        "3g2",
        "3gp2",
        "3gpp",
        "f4v",
        "m2t",
        "m2ts",
        "mts",
        "ts",
        "mpg",
        "mpe",
        "vob",
        "qt",
    )
}

internal fun Context.threeEmberPlayerPluginInfo(): PluginInfo {
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    return PluginInfo().apply {
        name = getString(R.string.app_name)
        description = getString(R.string.plugin_description)
        instruction = null
        author = getString(R.string.plugin_author)
        collaborators = null
        versionName = packageInfo.versionName.orEmpty()
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        versionDate = getString(R.string.plugin_version_date)
        id = ThreeEmberPlayerPlugin.ID
        engine = ExplorerActionPluginIds.ENGINE
        variant = ThreeEmberPlayerPlugin.VARIANT
        supportedAbis = emptyArray()
        capabilities = Bundle().apply {
            putLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION, ThreeEmberPlayerPlugin.REQUIRED_HOST_VERSION)
            putInt(ExplorerActionCapabilityKeys.PROTOCOL_VERSION, ThreeEmberPlayerPlugin.PROTOCOL_VERSION)
        }
    }
}

internal fun threeEmberPlayerActionCatalog(): Bundle {
    fun action(
        id: String,
        labelResourceName: String,
        labelFallback: String,
        cardinality: Int,
        placement: Int,
        readSiblings: Boolean,
        playbackProgress: Boolean,
    ) = Bundle().apply {
        putString(ExplorerActionCatalogKeys.ID, id)
        putString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME, labelResourceName)
        putString(ExplorerActionCatalogKeys.LABEL_FALLBACK, labelFallback)
        putString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME, ThreeEmberPlayerPlugin.ACTIVITY_CLASS_NAME)
        putInt(ExplorerActionCatalogKeys.PRIORITY, ThreeEmberPlayerPlugin.ACTION_PRIORITY)
        putInt(ExplorerActionCatalogKeys.TARGET_KIND, ExplorerActionValues.TARGET_FILE)
        putInt(ExplorerActionCatalogKeys.CARDINALITY, cardinality)
        putInt(ExplorerActionCatalogKeys.ACCESS_MODE, ExplorerActionValues.ACCESS_READ_ONLY)
        putInt(ExplorerActionCatalogKeys.PLACEMENT, placement)
        putBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS, readSiblings)
        putBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS, playbackProgress)
        putStringArrayList(
            ExplorerActionCatalogKeys.MIME_TYPES,
            ArrayList(ThreeEmberPlayerPlugin.MIME_TYPES.asList()),
        )
        putStringArrayList(
            ExplorerActionCatalogKeys.EXTENSIONS,
            ArrayList(ThreeEmberPlayerPlugin.EXTENSIONS.asList()),
        )
    }
    return Bundle().apply {
        putInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION, ThreeEmberPlayerPlugin.PROTOCOL_VERSION)
        putParcelableArrayList(
            ExplorerActionCatalogKeys.ACTIONS,
            arrayListOf(
                action(
                    id = ThreeEmberPlayerPlugin.ACTION_ID,
                    labelResourceName = ThreeEmberPlayerPlugin.LABEL_RESOURCE_NAME,
                    labelFallback = ThreeEmberPlayerPlugin.LABEL_FALLBACK,
                    cardinality = ExplorerActionValues.CARDINALITY_SINGLE,
                    placement = ExplorerActionValues.PLACEMENT_PRIMARY,
                    readSiblings = true,
                    playbackProgress = true,
                ),
                action(
                    id = ThreeEmberPlayerPlugin.ACTION_SELECTION_ID,
                    labelResourceName = ThreeEmberPlayerPlugin.SELECTION_LABEL_RESOURCE_NAME,
                    labelFallback = ThreeEmberPlayerPlugin.SELECTION_LABEL_FALLBACK,
                    cardinality = ExplorerActionValues.CARDINALITY_MULTIPLE,
                    placement = ExplorerActionValues.PLACEMENT_SELECTION_TOOLBAR,
                    readSiblings = false,
                    playbackProgress = false,
                ),
            ),
        )
    }
}
