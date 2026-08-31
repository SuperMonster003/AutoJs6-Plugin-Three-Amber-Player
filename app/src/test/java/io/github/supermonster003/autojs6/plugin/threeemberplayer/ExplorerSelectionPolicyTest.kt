package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplorerSelectionPolicyTest {

    @Test
    fun targetCount_enforcesSingleAndOneThrough128SelectionBounds() {
        assertTrue(ExplorerSelectionPolicy.isValidTargetCount(1, multipleAction = false))
        assertFalse(ExplorerSelectionPolicy.isValidTargetCount(0, multipleAction = false))
        assertFalse(ExplorerSelectionPolicy.isValidTargetCount(2, multipleAction = false))

        assertTrue(ExplorerSelectionPolicy.isValidTargetCount(1, multipleAction = true))
        assertTrue(
            ExplorerSelectionPolicy.isValidTargetCount(
                ExplorerSelectionPolicy.MAX_TARGETS,
                multipleAction = true,
            ),
        )
        assertFalse(ExplorerSelectionPolicy.isValidTargetCount(0, multipleAction = true))
        assertFalse(
            ExplorerSelectionPolicy.isValidTargetCount(
                ExplorerSelectionPolicy.MAX_TARGETS + 1,
                multipleAction = true,
            ),
        )
    }

    @Test
    fun uniqueValues_rejectsBlankAndDuplicatesWithoutReorderingInput() {
        val ordered = listOf("third", "first", "second")
        assertTrue(ExplorerSelectionPolicy.hasUniqueNonBlankValues(ordered))
        assertTrue(ordered == listOf("third", "first", "second"))
        assertFalse(ExplorerSelectionPolicy.hasUniqueNonBlankValues(listOf("one", "one")))
        assertFalse(ExplorerSelectionPolicy.hasUniqueNonBlankValues(listOf("one", "")))
        assertFalse(ExplorerSelectionPolicy.hasUniqueNonBlankValues(listOf("one", " ")))
    }
}
