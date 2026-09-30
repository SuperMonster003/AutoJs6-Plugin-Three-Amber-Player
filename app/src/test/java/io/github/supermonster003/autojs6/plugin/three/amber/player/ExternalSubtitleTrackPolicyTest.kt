package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExternalSubtitleTrackPolicyTest {

    @Test
    fun encodedLabelRecoversIdentityWhenMedia3DropsFormatId() {
        val stableId = "${EXTERNAL_SUBTITLE_ID_PREFIX}0:manual"
        val label = ExternalSubtitleTrackPolicy.encodedLabel(stableId, "movie.zh|final.srt")

        assertEquals(
            ExternalSubtitleTrackIdentity(stableId, "movie.zh|final.srt"),
            ExternalSubtitleTrackPolicy.identify(formatId = null, formatLabel = label),
        )
    }

    @Test
    fun formatIdWinsAndOrdinaryEmbeddedTrackIsNotExternal() {
        val stableId = "${EXTERNAL_SUBTITLE_ID_PREFIX}2:sidecar-0"
        assertEquals(
            ExternalSubtitleTrackIdentity(stableId, "movie.ass"),
            ExternalSubtitleTrackPolicy.identify(
                formatId = stableId,
                formatLabel = ExternalSubtitleTrackPolicy.encodedLabel(stableId, "movie.ass"),
            ),
        )
        assertNull(ExternalSubtitleTrackPolicy.identify("embedded-1", "English"))
    }
}
