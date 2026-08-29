package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.app.Application
import io.github.supermonster003.autojs6.plugin.threeemberplayer.settings.AppAppearanceController

class ThreeEmberPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAppearanceController.apply(this)
    }
}
