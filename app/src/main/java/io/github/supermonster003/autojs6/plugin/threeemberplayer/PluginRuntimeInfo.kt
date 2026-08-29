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
    const val VARIANT = "default"
    const val PROTOCOL_VERSION = 12
    const val REQUIRED_HOST_VERSION = 5276L
    const val LABEL_RESOURCE_NAME = "action_play_video"
    const val LABEL_FALLBACK = "Play video"
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
    val action = Bundle().apply {
        putString(ExplorerActionCatalogKeys.ID, ThreeEmberPlayerPlugin.ACTION_ID)
        putString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME, ThreeEmberPlayerPlugin.LABEL_RESOURCE_NAME)
        putString(ExplorerActionCatalogKeys.LABEL_FALLBACK, ThreeEmberPlayerPlugin.LABEL_FALLBACK)
        putString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME, ThreeEmberPlayerPlugin.ACTIVITY_CLASS_NAME)
        putInt(ExplorerActionCatalogKeys.PRIORITY, ThreeEmberPlayerPlugin.ACTION_PRIORITY)
        putInt(ExplorerActionCatalogKeys.TARGET_KIND, ExplorerActionValues.TARGET_FILE)
        putInt(ExplorerActionCatalogKeys.CARDINALITY, ExplorerActionValues.CARDINALITY_SINGLE)
        putInt(ExplorerActionCatalogKeys.ACCESS_MODE, ExplorerActionValues.ACCESS_READ_ONLY)
        putInt(ExplorerActionCatalogKeys.PLACEMENT, ExplorerActionValues.PLACEMENT_PRIMARY)
        putBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS, true)
        putBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS, true)
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
        putParcelableArrayList(ExplorerActionCatalogKeys.ACTIONS, arrayListOf(action))
    }
}
