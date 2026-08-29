package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.Activity
import android.content.ClipData
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

internal object ExternalPlaybackLauncher {

    fun open(activity: Activity, item: AndroidPlaybackItem): Boolean = runCatching {
        val externalUri = item.externalUri ?: return false
        val baseIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(externalUri, item.mimeType)
            clipData = ClipData.newRawUri(item.displayName, externalUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        val components = queryActivities(activity.packageManager, baseIntent)
            .mapNotNull { resolveInfo ->
                resolveInfo.activityInfo?.let { ComponentName(it.packageName, it.name) }
            }
            .filterNot { it.packageName == activity.packageName }
            .distinct()
        if (components.isEmpty()) return false

        val explicitIntents = components.map { component ->
            Intent(baseIntent).setComponent(component)
        }
        val chooser = Intent.createChooser(
            explicitIntents.first(),
            activity.getString(R.string.action_open_with_other_app),
        ).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, explicitIntents.drop(1).toTypedArray())
            putExtra(
                Intent.EXTRA_EXCLUDE_COMPONENTS,
                arrayOf(ComponentName(activity, ExternalViewActivity::class.java)),
            )
        }
        activity.startActivity(chooser)
        true
    }.getOrDefault(false)

    private fun queryActivities(packageManager: PackageManager, intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
}
