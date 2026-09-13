package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.common.api.IPluginInfoProvider

/** Exposes common plugin metadata separately from the Explorer Action Binder endpoint. */
class PluginInfoService : Service() {

    private val binder = object : IPluginInfoProvider.Stub() {
        override fun getInfo() = threeEmberPlayerPluginInfo().apply { supportedAbis = emptyArray() }
    }

    override fun onBind(intent: Intent?): IBinder = binder
}
