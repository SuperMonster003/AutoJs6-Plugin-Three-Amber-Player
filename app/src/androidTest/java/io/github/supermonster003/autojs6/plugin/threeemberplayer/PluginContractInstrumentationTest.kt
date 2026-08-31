@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.content.ComponentName
import android.content.Intent
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

@RunWith(AndroidJUnit4::class)
class PluginContractInstrumentationTest {

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
        val catalog = threeEmberPlayerActionCatalog()
        val actions = requireNotNull(
            catalog.getParcelableArrayList<android.os.Bundle>(ExplorerActionCatalogKeys.ACTIONS),
        )
        assertEquals(ThreeEmberPlayerPlugin.PROTOCOL_VERSION, catalog.getInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION))
        assertEquals(2, actions.size)
        val action = actions.first()
        assertEquals(ThreeEmberPlayerPlugin.ACTION_ID, action.getString(ExplorerActionCatalogKeys.ID))
        assertEquals(ExplorerActionValues.CARDINALITY_SINGLE, action.getInt(ExplorerActionCatalogKeys.CARDINALITY))
        assertEquals(listOf("video/*"), action.getStringArrayList(ExplorerActionCatalogKeys.MIME_TYPES))
        assertTrue(action.getBoolean(ExplorerActionCatalogKeys.READ_SIBLINGS))
        assertTrue(action.getBoolean(ExplorerActionCatalogKeys.PLAYBACK_PROGRESS))

        val selection = actions.last()
        assertEquals(
            ThreeEmberPlayerPlugin.ACTION_SELECTION_ID,
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
        assertEquals("3-Ember Player", context.getString(R.string.app_name))
        assertEquals(
            LauncherActivity::class.java.name,
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.component?.className,
        )
        assertEquals(
            ThreeEmberPlayerApplication::class.java.name,
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

    private companion object {
        const val STABLE_APPLICATION_ID = "io.github.supermonster003.autojs6.plugin.videoplayer"
    }
}
