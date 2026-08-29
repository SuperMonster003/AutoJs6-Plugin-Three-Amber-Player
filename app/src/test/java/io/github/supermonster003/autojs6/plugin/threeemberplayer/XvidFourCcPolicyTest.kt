package io.github.supermonster003.autojs6.plugin.threeemberplayer

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class XvidFourCcPolicyTest {

    @Test
    fun findOffsets_acceptsPlausibleXvidFourCcInsideMatroskaHeader() {
        val header = xvidHeader()

        assertArrayEquals(
            intArrayOf(COMPRESSION_OFFSET),
            XvidFourCcPolicy.findXvidCompressionOffsets(header),
        )
    }

    @Test
    fun findOffsets_rejectsNonMatroskaAndUnrelatedXvidText() {
        val nonMatroska = xvidHeader().also { it[0] = 0 }
        val unrelated = xvidHeader().also {
            "V_MS/VFW/FOURCC".encodeToByteArray().indices.forEach { index ->
                it[CODEC_ID_OFFSET + index] = 0
            }
        }

        assertArrayEquals(IntArray(0), XvidFourCcPolicy.findXvidCompressionOffsets(nonMatroska))
        assertArrayEquals(IntArray(0), XvidFourCcPolicy.findXvidCompressionOffsets(unrelated))
    }

    @Test
    fun findOffsets_rejectsMalformedBitmapInfoHeader() {
        val invalidPlanes = xvidHeader().also { putLittleEndianShort(it, BITMAP_HEADER_OFFSET + 12, 2) }
        val invalidDimensions = xvidHeader().also { putLittleEndianInt(it, BITMAP_HEADER_OFFSET + 4, 0) }

        assertArrayEquals(IntArray(0), XvidFourCcPolicy.findXvidCompressionOffsets(invalidPlanes))
        assertArrayEquals(IntArray(0), XvidFourCcPolicy.findXvidCompressionOffsets(invalidDimensions))
    }

    @Test
    fun findOffsets_doesNotTreatDivxAsXvid() {
        val header = xvidHeader().also {
            "DIVX".encodeToByteArray().copyInto(it, COMPRESSION_OFFSET)
        }

        assertArrayEquals(IntArray(0), XvidFourCcPolicy.findXvidCompressionOffsets(header))
    }

    @Test
    fun replacementByte_changesOnlyValidatedCompressionBytes() {
        val offsets = longArrayOf(100L)
        val source = "XVID".encodeToByteArray()
        val replacement = ByteArray(source.size) { index ->
            XvidFourCcPolicy.replacementByte(100L + index, source[index], offsets)
        }

        assertEquals("DIVX", replacement.decodeToString())
        assertEquals('X'.code.toByte(), XvidFourCcPolicy.replacementByte(99L, 'X'.code.toByte(), offsets))
        assertEquals('Q'.code.toByte(), XvidFourCcPolicy.replacementByte(100L, 'Q'.code.toByte(), offsets))
    }

    private fun xvidHeader(): ByteArray = ByteArray(512).also { header ->
        byteArrayOf(0x1A, 0x45, 0xDF.toByte(), 0xA3.toByte()).copyInto(header)
        "V_MS/VFW/FOURCC".encodeToByteArray().copyInto(header, CODEC_ID_OFFSET)
        putLittleEndianInt(header, BITMAP_HEADER_OFFSET, 40)
        putLittleEndianInt(header, BITMAP_HEADER_OFFSET + 4, 688)
        putLittleEndianInt(header, BITMAP_HEADER_OFFSET + 8, 384)
        putLittleEndianShort(header, BITMAP_HEADER_OFFSET + 12, 1)
        putLittleEndianShort(header, BITMAP_HEADER_OFFSET + 14, 24)
        "XVID".encodeToByteArray().copyInto(header, COMPRESSION_OFFSET)
    }

    private fun putLittleEndianInt(target: ByteArray, offset: Int, value: Int) {
        repeat(4) { index -> target[offset + index] = (value ushr (index * 8)).toByte() }
    }

    private fun putLittleEndianShort(target: ByteArray, offset: Int, value: Int) {
        repeat(2) { index -> target[offset + index] = (value ushr (index * 8)).toByte() }
    }

    private companion object {
        const val CODEC_ID_OFFSET = 48
        const val BITMAP_HEADER_OFFSET = 96
        const val COMPRESSION_OFFSET = BITMAP_HEADER_OFFSET + 16
    }
}
