@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.autojs.plugin.common.api.IPluginInfoProvider
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionPlugin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PluginContractInstrumentationTest {

    @Test
    fun renamedInfoServiceReturnsNewIdentityThroughARealBinding() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val ready = CountDownLatch(1)
        var remote: IBinder? = null
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                remote = binder
                ready.countDown()
            }
            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }
        assertTrue(context.bindService(Intent(context, PluginInfoService::class.java), connection, Context.BIND_AUTO_CREATE))
        try {
            assertTrue(ready.await(5, TimeUnit.SECONDS))
            val info = IPluginInfoProvider.Stub.asInterface(remote).info
            assertEquals("three-amber-player", info.id)
            assertEquals("explorer-action", info.engine)
            assertEquals("3-Amber Player", info.name)
            assertEquals("4.0.0", info.versionName)
            assertEquals(context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong(), info.versionCode)
            assertTrue(info.supportedAbis.isNullOrEmpty())
        } finally {
            context.unbindService(connection)
        }
    }

    @Test
    fun explorerServiceBindsAnExplicitComponentWithoutAnAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent().setComponent(ComponentName(context, ExplorerActionService::class.java))
        assertEquals(
            IExplorerActionPlugin::class.java.name,
            ExplorerActionService().onBind(intent).interfaceDescriptor,
        )
    }

    @Test
    fun catalogRetainsStableIdentityAndHostCompatibleCapabilities() {
        val catalog = threeAmberPlayerActionCatalog()
        val actions = requireNotNull(
            catalog.getParcelableArrayList<android.os.Bundle>(ExplorerActionCatalogKeys.ACTIONS),
        )
        assertEquals(ThreeAmberPlayerPlugin.PROTOCOL_VERSION, catalog.getInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION))
        assertEquals(2, actions.size)
        val action = actions.first()
        assertEquals(ThreeAmberPlayerPlugin.ACTION_ID, action.getString(ExplorerActionCatalogKeys.ID))
        assertEquals(ExplorerActionValues.CARDINALITY_SINGLE, action.getInt(ExplorerActionCatalogKeys.CARDINALITY))
        assertEquals(listOf("video/*") + io.github.supermonster003.autojs6.plugin.three.amber.player.playlist.PlaylistParser.mimeTypes.asList(), action.getStringArrayList(ExplorerActionCatalogKeys.MIME_TYPES))
        assertTrue(action.getBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS))
        assertTrue(action.getBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS))

        val selection = actions.last()
        assertEquals(
            ThreeAmberPlayerPlugin.ACTION_SELECTION_ID,
            selection.getString(ExplorerActionCatalogKeys.ID),
        )
        assertEquals(
            ExplorerActionValues.CARDINALITY_MULTIPLE,
            selection.getInt(ExplorerActionCatalogKeys.CARDINALITY),
        )
        assertEquals(
            ExplorerActionValues.PLACEMENT_SELECTION_TOOLBAR,
            selection.getInt(ExplorerActionCatalogKeys.PLACEMENT),
        )
        assertEquals(false, selection.getBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS))
        assertFalse(selection.getBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS))
    }

    @Test
    fun manifestExposesStandaloneLauncherAndProtectedPluginInfoEntry() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals(STABLE_APPLICATION_ID, context.packageName)
        assertEquals("3-Amber Player", context.getString(R.string.app_name))
        val launcher = requireNotNull(context.packageManager.getLaunchIntentForPackage(context.packageName)?.component)
        assertEquals(
            io.github.supermonster003.autojs6.plugin.three.amber.player.settings.LauncherIcons.current(context).component(context),
            launcher,
        )
        assertEquals(LauncherActivity::class.java.name, context.packageManager.getActivityInfo(launcher, 0).targetActivity)
        assertEquals(
            ThreeAmberPlayerApplication::class.java.name,
            context.applicationInfo.className,
        )

        val services = context.packageManager.queryIntentServices(
            Intent("org.autojs.plugin.INFO").setPackage(context.packageName),
            0,
        )
        val service = services.singleOrNull {
            it.serviceInfo.name == PluginInfoService::class.java.name
        }?.serviceInfo
        assertNotNull(service)
        assertEquals("org.autojs.permission.PLUGIN", service?.permission)
        assertTrue(service?.exported == true)
        assertEquals(
            IPluginInfoProvider::class.java.name,
            PluginInfoService().onBind(Intent()).interfaceDescriptor,
        )
    }

    @Test
    fun backgroundPlaybackDeclaresOnlyItsReviewedOrdinaryPermissionsAndPrivateMediaService() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS or PackageManager.GET_SERVICES,
        )
        val permissions = packageInfo.requestedPermissions.orEmpty().toSet()
        assertTrue(Manifest.permission.FOREGROUND_SERVICE in permissions)
        assertTrue(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK in permissions)
        assertTrue(Manifest.permission.POST_NOTIFICATIONS in permissions)

        val service = packageInfo.services.orEmpty().single {
            it.name == BackgroundPlaybackService::class.java.name
        }
        assertFalse(service.exported)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            assertEquals(
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
                service.foregroundServiceType,
            )
        }
    }

    private companion object {
        const val STABLE_APPLICATION_ID = "io.github.supermonster003.autojs6.plugin.three.amber.player"
    }
}
