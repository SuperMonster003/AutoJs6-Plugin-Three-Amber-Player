package io.github.supermonster003.autojs6.plugin.threeemberplayer

internal enum class VideoMirrorMode {
    NONE,
    HORIZONTAL,
    VERTICAL,
    BOTH,
}

internal data class VideoRenderTransform(
    val scaleX: Float,
    val scaleY: Float,
    val pivotXFraction: Float,
    val pivotYFraction: Float,
)

/** Composes zoom magnitude and screen-relative mirror axes without Android view dependencies. */
internal object VideoTransformPolicy {

    fun resolve(
        zoomScale: Float,
        pivotXFraction: Float,
        pivotYFraction: Float,
        mirrorMode: VideoMirrorMode,
    ): VideoRenderTransform {
        val magnitude = zoomScale.takeIf(Float::isFinite)
            ?.coerceIn(PlayerGesturePolicy.MIN_ZOOM_SCALE, PlayerGesturePolicy.MAX_ZOOM_SCALE)
            ?: PlayerGesturePolicy.DEFAULT_ZOOM_SCALE
        val horizontalSign = if (
            mirrorMode == VideoMirrorMode.HORIZONTAL || mirrorMode == VideoMirrorMode.BOTH
        ) {
            -1f
        } else {
            1f
        }
        val verticalSign = if (
            mirrorMode == VideoMirrorMode.VERTICAL || mirrorMode == VideoMirrorMode.BOTH
        ) {
            -1f
        } else {
            1f
        }
        return VideoRenderTransform(
            scaleX = magnitude * horizontalSign,
            scaleY = magnitude * verticalSign,
            pivotXFraction = pivotXFraction.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.5f,
            pivotYFraction = pivotYFraction.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.5f,
        )
    }
}
