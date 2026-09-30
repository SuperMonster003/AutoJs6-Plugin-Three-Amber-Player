@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.graphics.Color
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.SubtitleView

/** Android adapter kept separate from the Android-free mapping policy. */
internal object SubtitleStyleApplier {

    fun apply(view: SubtitleView?, settings: SubtitleStyleSettings, forcePreview: Boolean = false) {
        view ?: return
        if (!settings.customized && !forcePreview) {
            view.setApplyEmbeddedStyles(true)
            view.setApplyEmbeddedFontSizes(true)
            view.setUserDefaultStyle()
            view.setUserDefaultTextSize()
            view.setBottomPaddingFraction(SubtitleView.DEFAULT_BOTTOM_PADDING_FRACTION)
            return
        }

        val spec = SubtitleStylePolicy.resolve(settings)
        // A user-selected style is authoritative for both embedded and sidecar cues.
        view.setApplyEmbeddedStyles(false)
        view.setFractionalTextSize(spec.fractionalTextSize)
        view.setBottomPaddingFraction(spec.bottomPaddingFraction)
        view.setStyle(
            CaptionStyleCompat(
                spec.foregroundArgb,
                spec.backgroundArgb,
                Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_NONE,
                Color.BLACK,
                null,
            ),
        )
    }
}
