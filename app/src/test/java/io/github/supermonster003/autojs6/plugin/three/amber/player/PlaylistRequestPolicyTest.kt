package io.github.supermonster003.autojs6.plugin.three.amber.player

import org.junit.Assert.*
import org.junit.Test

class PlaylistRequestPolicyTest {
    @Test fun externalPlaylistAcceptsOpaqueSafDocumentIdsWithReadOnlyGrants() {
        val uri = "content://com.android.externalstorage.documents/document/primary%3AMusic%2Flist.m3u"
        val envelope = ExternalRequestEnvelope(VideoRequestPolicy.EXTERNAL_VIEW_ACTION, uri, GrantEnvelope(true, false, false, false), "audio/x-mpegurl")
        assertEquals(uri, VideoRequestPolicy.validateExternal(envelope)?.targetUri)
        assertNotNull(VideoRequestPolicy.validateExternal(envelope.copy(mimeType = "video/*")))
        assertNull(VideoRequestPolicy.validateExternal(envelope.copy(grants = GrantEnvelope(true, true, false, false))))
        assertNull(VideoRequestPolicy.validateExternal(envelope.copy(grants = GrantEnvelope(true, false, false, true))))
        assertNull(VideoRequestPolicy.validateExternal(envelope.copy(targetUri = "file:///Music/list.m3u")))
    }

    @Test fun playlistTypesDoNotEnterThePrivateSingleVideoContract() {
        assertNotNull(VideoRequestPolicy.normalizeInputMimeType("application/xspf+xml"))
        assertNull(VideoRequestPolicy.normalizeVideoMimeType("application/xspf+xml"))
        assertNull(VideoRequestPolicy.normalizeInputMimeType(" application/xspf+xml"))
        assertNull(VideoRequestPolicy.normalizeInputMimeType("APPLICATION/XSPF+XML"))
    }
}
