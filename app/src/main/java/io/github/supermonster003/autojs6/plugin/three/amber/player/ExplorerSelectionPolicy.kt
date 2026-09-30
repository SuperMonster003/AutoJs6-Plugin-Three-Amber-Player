package io.github.supermonster003.autojs6.plugin.three.amber.player

/** Android-free bounds and identity checks for ordered Explorer Action target groups. */
internal object ExplorerSelectionPolicy {

    const val MAX_TARGETS = 128

    fun isValidTargetCount(targetCount: Int, multipleAction: Boolean): Boolean =
        if (multipleAction) {
            targetCount in 1..MAX_TARGETS
        } else {
            targetCount == 1
        }

    fun hasUniqueNonBlankValues(values: List<String>): Boolean =
        values.all(String::isNotBlank) && values.toSet().size == values.size
}
