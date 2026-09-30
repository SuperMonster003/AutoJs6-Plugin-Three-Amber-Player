package io.github.supermonster003.autojs6.plugin.three.amber.player

/** User-facing subtitle text sizes, expressed relative to Media3's default cue size. */
internal enum class SubtitleTextScale(val multiplier: Float) {
    PERCENT_75(0.75f),
    PERCENT_100(1f),
    PERCENT_125(1.25f),
    PERCENT_150(1.5f),
}

/** Deliberately small, high-contrast foreground palette for video captions. */
internal enum class SubtitleForegroundColor(val argb: Int) {
    WHITE(0xFFFFFFFF.toInt()),
    YELLOW(0xFFFFFF00.toInt()),
    CYAN(0xFF00FFFF.toInt()),
    GREEN(0xFF00FF66.toInt()),
}

internal enum class SubtitleBackgroundStyle(val argb: Int) {
    OPAQUE(0xFF000000.toInt()),
    TRANSLUCENT(0x99000000.toInt()),
    NONE(0x00000000),
}

internal enum class SubtitleBottomMargin(val fraction: Float) {
    PERCENT_0(0f),
    PERCENT_4(0.04f),
    PERCENT_8(0.08f),
}

internal data class SubtitleStyleSettings(
    val textScale: SubtitleTextScale = SubtitleTextScale.PERCENT_100,
    val foregroundColor: SubtitleForegroundColor = SubtitleForegroundColor.WHITE,
    val backgroundStyle: SubtitleBackgroundStyle = SubtitleBackgroundStyle.OPAQUE,
    // Media3's current default is 8%, so the default remains visually unchanged.
    val bottomMargin: SubtitleBottomMargin = SubtitleBottomMargin.PERCENT_8,
    /** False preserves Media3/system caption preferences until the user changes this group. */
    val customized: Boolean = false,
)

internal data class SubtitleStyleSpec(
    val fractionalTextSize: Float,
    val foregroundArgb: Int,
    val backgroundArgb: Int,
    val bottomPaddingFraction: Float,
)

/** Android-free mapping from persisted choices to Media3-compatible primitive values. */
internal object SubtitleStylePolicy {

    // Mirrors SubtitleView.DEFAULT_TEXT_SIZE_FRACTION without importing Android UI classes.
    const val MEDIA3_DEFAULT_TEXT_SIZE_FRACTION = 0.0533f

    fun resolve(settings: SubtitleStyleSettings): SubtitleStyleSpec = SubtitleStyleSpec(
        fractionalTextSize = MEDIA3_DEFAULT_TEXT_SIZE_FRACTION * settings.textScale.multiplier,
        foregroundArgb = settings.foregroundColor.argb,
        backgroundArgb = settings.backgroundStyle.argb,
        bottomPaddingFraction = settings.bottomMargin.fraction,
    )

    inline fun <reified T : Enum<T>> enumOrDefault(raw: String?, default: T): T =
        raw?.let { stored -> enumValues<T>().firstOrNull { it.name == stored } } ?: default
}
