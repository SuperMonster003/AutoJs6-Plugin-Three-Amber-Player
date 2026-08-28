package io.github.supermonster003.autojs6.plugin.videoplayer

import kotlin.math.max
import kotlin.math.min

/** Android-free detection policy for the narrowly scoped XVID-in-Matroska compatibility shim. */
internal object XvidFourCcPolicy {

    const val MAX_HEADER_SCAN_BYTES = 1024 * 1024

    private const val MAX_CODEC_ID_DISTANCE_BYTES = 64 * 1024
    private const val BITMAP_INFO_HEADER_MIN_SIZE = 40L
    private const val BITMAP_INFO_HEADER_MAX_SIZE = 256L
    private const val MAX_REASONABLE_DIMENSION = 32_768

    private val ebmlSignature = byteArrayOf(0x1A, 0x45, 0xDF.toByte(), 0xA3.toByte())
    private val fourCcCodecId = "V_MS/VFW/FOURCC".encodeToByteArray()
    private val xvidCompression = "XVID".encodeToByteArray()
    private val divxCompression = "DIVX".encodeToByteArray()

    fun hasMatroskaHeader(header: ByteArray, validLength: Int = header.size): Boolean {
        val limit = validLength.coerceIn(0, header.size)
        return matchesAt(header, ebmlSignature, 0, limit)
    }

    /**
     * Returns absolute offsets, relative to [header], of XVID compression fields in plausible
     * BITMAPINFOHEADER structures belonging to a Matroska VFW/FourCC track.
     */
    fun findXvidCompressionOffsets(
        header: ByteArray,
        validLength: Int = header.size,
    ): IntArray {
        val limit = validLength.coerceIn(0, header.size)
        if (!hasMatroskaHeader(header, limit)) return IntArray(0)

        val codecIdOffsets = findAll(header, fourCcCodecId, limit)
        if (codecIdOffsets.isEmpty()) return IntArray(0)

        val offsets = ArrayList<Int>()
        val lastHeaderStart = limit - BITMAP_INFO_HEADER_MIN_SIZE.toInt()
        for (headerStart in 0..lastHeaderStart.coerceAtLeast(-1)) {
            val compressionOffset = headerStart + 16
            if (!matchesAt(header, xvidCompression, compressionOffset, limit)) continue
            if (!isPlausibleBitmapInfoHeader(header, headerStart, limit)) continue
            if (codecIdOffsets.none { codecIdOffset ->
                    val codecIdEnd = codecIdOffset + fourCcCodecId.size
                    rangesAreNearby(codecIdOffset, codecIdEnd, headerStart, compressionOffset + 4)
                }
            ) {
                continue
            }
            offsets += compressionOffset
        }
        return offsets.distinct().sorted().toIntArray()
    }

    fun replacementByte(
        absolutePosition: Long,
        original: Byte,
        compressionOffsets: LongArray,
    ): Byte {
        compressionOffsets.forEach { compressionOffset ->
            val replacementIndex = absolutePosition - compressionOffset
            if (replacementIndex in 0 until xvidCompression.size.toLong()) {
                val index = replacementIndex.toInt()
                return if (original == xvidCompression[index]) divxCompression[index] else original
            }
        }
        return original
    }

    private fun isPlausibleBitmapInfoHeader(
        data: ByteArray,
        offset: Int,
        limit: Int,
    ): Boolean {
        if (offset < 0 || offset + BITMAP_INFO_HEADER_MIN_SIZE > limit) return false
        val headerSize = readUnsignedIntLittleEndian(data, offset)
        val width = readIntLittleEndian(data, offset + 4)
        val height = readIntLittleEndian(data, offset + 8)
        val planes = readUnsignedShortLittleEndian(data, offset + 12)
        val bitCount = readUnsignedShortLittleEndian(data, offset + 14)
        return headerSize in BITMAP_INFO_HEADER_MIN_SIZE..BITMAP_INFO_HEADER_MAX_SIZE &&
            width != 0 && width in -MAX_REASONABLE_DIMENSION..MAX_REASONABLE_DIMENSION &&
            height != 0 && height in -MAX_REASONABLE_DIMENSION..MAX_REASONABLE_DIMENSION &&
            planes == 1 && bitCount in 1..64
    }

    private fun rangesAreNearby(
        firstStart: Int,
        firstEnd: Int,
        secondStart: Int,
        secondEnd: Int,
    ): Boolean {
        val distance = max(firstStart, secondStart) - min(firstEnd, secondEnd)
        return distance.coerceAtLeast(0) <= MAX_CODEC_ID_DISTANCE_BYTES
    }

    private fun findAll(data: ByteArray, pattern: ByteArray, limit: Int): IntArray {
        if (pattern.isEmpty() || pattern.size > limit) return IntArray(0)
        val result = ArrayList<Int>()
        for (offset in 0..limit - pattern.size) {
            if (matchesAt(data, pattern, offset, limit)) result += offset
        }
        return result.toIntArray()
    }

    private fun matchesAt(
        data: ByteArray,
        pattern: ByteArray,
        offset: Int,
        limit: Int,
    ): Boolean {
        if (offset < 0 || offset + pattern.size > limit) return false
        return pattern.indices.all { index -> data[offset + index] == pattern[index] }
    }

    private fun readUnsignedIntLittleEndian(data: ByteArray, offset: Int): Long =
        readIntLittleEndian(data, offset).toLong() and 0xFFFF_FFFFL

    private fun readIntLittleEndian(data: ByteArray, offset: Int): Int =
        (data[offset].toInt() and 0xFF) or
            ((data[offset + 1].toInt() and 0xFF) shl 8) or
            ((data[offset + 2].toInt() and 0xFF) shl 16) or
            ((data[offset + 3].toInt() and 0xFF) shl 24)

    private fun readUnsignedShortLittleEndian(data: ByteArray, offset: Int): Int =
        (data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8)
}
