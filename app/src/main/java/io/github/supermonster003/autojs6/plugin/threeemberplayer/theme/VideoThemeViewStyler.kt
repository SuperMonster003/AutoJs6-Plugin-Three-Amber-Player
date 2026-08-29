package io.github.supermonster003.autojs6.plugin.threeemberplayer.theme

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.widget.CheckedTextViewCompat
import androidx.core.widget.CompoundButtonCompat
import com.google.android.material.button.MaterialButton

/** Runtime styling for widgets whose colors cannot represent an arbitrary custom source in XML. */
internal object VideoThemeViewStyler {

    fun styleDialog(dialog: AlertDialog, palette: VideoThemePalette) {
        val window = dialog.window ?: return
        window.navigationBarColor = palette.background
        window.decorView.background?.let { drawable ->
            DrawableCompat.setTint(DrawableCompat.wrap(drawable.mutate()), palette.surfaceContainerHigh)
        }
        dialog.findViewById<View>(androidx.appcompat.R.id.parentPanel)?.let { panel ->
            ViewCompat.setBackgroundTintList(panel, ColorStateList.valueOf(palette.surfaceContainerHigh))
        }

        dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(palette.onSurface)
        dialog.listView?.let { list ->
            list.setBackgroundColor(palette.surfaceContainerHigh)
            list.divider?.let { divider -> DrawableCompat.setTint(divider, palette.outlineVariant) }
        }
        styleTree(window.decorView, palette)
        listOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL,
        ).forEach { which -> dialog.getButton(which)?.setTextColor(palette.primary) }
    }

    fun tintMenu(menu: Menu, color: Int) {
        for (index in 0 until menu.size()) {
            val item = menu.getItem(index)
            item.icon = item.icon?.tinted(color)
            item.subMenu?.let { tintMenu(it, color) }
        }
    }

    private fun styleTree(view: View, palette: VideoThemePalette) {
        when (view) {
            is CheckedTextView -> {
                view.setTextColor(enabledTextColors(palette))
                CheckedTextViewCompat.setCheckMarkTintList(view, selectionTint(palette))
            }
            is CompoundButton -> {
                view.setTextColor(enabledTextColors(palette))
                CompoundButtonCompat.setButtonTintList(view, selectionTint(palette))
            }
            is MaterialButton -> Unit // Dialog buttons are handled separately after traversal.
            is TextView -> if (view.id !in DIALOG_BUTTON_IDS) {
                view.setTextColor(enabledTextColors(palette))
            }
            is ProgressBar -> {
                view.indeterminateTintList = ColorStateList.valueOf(palette.primary)
                view.progressTintList = ColorStateList.valueOf(palette.primary)
                view.progressBackgroundTintList = ColorStateList.valueOf(palette.outlineVariant)
            }
            is ListView -> view.setBackgroundColor(palette.surfaceContainerHigh)
        }
        if (view is ViewGroup) view.children.forEach { child -> styleTree(child, palette) }
    }

    private fun selectionTint(palette: VideoThemePalette): ColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(),
        ),
        intArrayOf(
            VideoThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, DISABLED_ALPHA),
            palette.primary,
            palette.outline,
        ),
    )

    private fun enabledTextColors(palette: VideoThemePalette): ColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(),
        ),
        intArrayOf(
            VideoThemePaletteGenerator.withAlpha(palette.onSurfaceVariant, DISABLED_ALPHA),
            palette.onSurface,
        ),
    )

    private fun Drawable.tinted(color: Int): Drawable = DrawableCompat.wrap(mutate()).also {
        DrawableCompat.setTint(it, color)
    }

    private const val DISABLED_ALPHA = 0x61
    private val DIALOG_BUTTON_IDS = setOf(android.R.id.button1, android.R.id.button2, android.R.id.button3)
}
