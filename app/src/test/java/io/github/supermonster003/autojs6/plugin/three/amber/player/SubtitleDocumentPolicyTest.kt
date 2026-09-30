package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SubtitleDocumentPolicyTest {

    @Test
    fun acceptsOnlySrtAndAssAtOrBelowFourMiB() {
        assertEquals(
            "application/x-subrip",
            SubtitleDocumentPolicy.validate("movie.zh.srt", SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES)?.mimeType,
        )
        assertEquals(
            "text/x-ssa",
            SubtitleDocumentPolicy.validate("movie.ASS", 1L)?.mimeType,
        )
        assertNull(
            SubtitleDocumentPolicy.validate("movie.srt", SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES + 1L),
        )
        assertNull(SubtitleDocumentPolicy.validate("movie.vtt", 1L))
        assertNull(SubtitleDocumentPolicy.validate("../movie.srt", 1L))
        assertNull(SubtitleDocumentPolicy.validate("movie.srt", -1L))
    }
}
