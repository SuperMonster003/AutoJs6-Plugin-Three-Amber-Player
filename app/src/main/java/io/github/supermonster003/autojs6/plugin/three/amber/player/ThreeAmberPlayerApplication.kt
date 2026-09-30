package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.app.Application
import io.github.supermonster003.autojs6.plugin.three.amber.player.settings.AppAppearanceController

class ThreeAmberPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAppearanceController.apply(this)
    }
}
