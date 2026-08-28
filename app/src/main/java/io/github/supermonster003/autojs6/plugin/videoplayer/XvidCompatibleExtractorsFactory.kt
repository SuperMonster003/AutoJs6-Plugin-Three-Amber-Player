package io.github.supermonster003.autojs6.plugin.videoplayer

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.Extractor
import androidx.media3.extractor.ExtractorInput
import androidx.media3.extractor.ExtractorOutput
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.ForwardingExtractor
import androidx.media3.extractor.ForwardingExtractorInput
import androidx.media3.extractor.ForwardingExtractorOutput
import androidx.media3.extractor.ForwardingTrackOutput
import androidx.media3.extractor.PositionHolder
import androidx.media3.extractor.TrackOutput
import androidx.media3.extractor.mkv.MatroskaExtractor
import androidx.media3.extractor.text.DefaultSubtitleParserFactory

/**
 * Prepends a Matroska extractor that exposes a validated XVID FourCC track as standard MPEG-4
 * Part 2, allowing Media3 to discover the device's built-in `video/mp4v-es` decoder.
 */
@androidx.annotation.OptIn(UnstableApi::class)
internal class XvidCompatibleExtractorsFactory(
    private val onCompatibilityApplied: () -> Unit = {},
    private val delegate: ExtractorsFactory = DefaultExtractorsFactory(),
) : ExtractorsFactory {

    override fun createExtractors(): Array<Extractor> =
        withCompatibleMatroskaExtractor(delegate.createExtractors())

    override fun createExtractors(
        uri: Uri,
        responseHeaders: Map<String, List<String>>,
    ): Array<Extractor> = withCompatibleMatroskaExtractor(
        delegate.createExtractors(uri, responseHeaders),
    )

    private fun withCompatibleMatroskaExtractor(extractors: Array<Extractor>): Array<Extractor> =
        arrayOf(
            XvidCompatibleMatroskaExtractor(onCompatibilityApplied),
            *extractors.filterNot { it is MatroskaExtractor }.toTypedArray(),
        )
}

@androidx.annotation.OptIn(UnstableApi::class)
private class XvidCompatibleMatroskaExtractor(
    private val onCompatibilityApplied: () -> Unit,
) : ForwardingExtractor(MatroskaExtractor(DefaultSubtitleParserFactory())) {

    private var xvidCompressionOffsets = LongArray(0)

    override fun sniff(input: ExtractorInput): Boolean {
        val inputStart = input.position
        val localOffsets = readXvidCompressionOffsets(input)
        val isMatroska = super.sniff(input)
        if (!isMatroska || localOffsets.isEmpty()) {
            xvidCompressionOffsets = LongArray(0)
            return isMatroska
        }
        xvidCompressionOffsets = LongArray(localOffsets.size) { index ->
            inputStart + localOffsets[index]
        }
        onCompatibilityApplied()
        return true
    }

    override fun read(input: ExtractorInput, seekPosition: PositionHolder): Int {
        val compatibleInput = if (xvidCompressionOffsets.isEmpty()) {
            input
        } else {
            XvidFourCcSubstitutingInput(input, xvidCompressionOffsets)
        }
        return super.read(compatibleInput, seekPosition)
    }

    override fun init(output: ExtractorOutput) {
        super.init(
            object : ForwardingExtractorOutput(output) {
                override fun track(id: Int, type: Int): TrackOutput {
                    val trackOutput = super.track(id, type)
                    return object : ForwardingTrackOutput(trackOutput) {
                        override fun format(format: Format) {
                            val compatibleFormat = if (
                                xvidCompressionOffsets.isNotEmpty() &&
                                format.sampleMimeType == MimeTypes.VIDEO_DIVX
                            ) {
                                format.buildUpon()
                                    .setSampleMimeType(MimeTypes.VIDEO_MP4V)
                                    .build()
                            } else {
                                format
                            }
                            super.format(compatibleFormat)
                        }
                    }
                }
            },
        )
    }

    private fun readXvidCompressionOffsets(input: ExtractorInput): IntArray {
        val signature = ByteArray(4)
        var signatureLength = 0
        try {
            while (signatureLength < signature.size) {
                val count = input.peek(
                    signature,
                    signatureLength,
                    signature.size - signatureLength,
                )
                if (count == C.RESULT_END_OF_INPUT || count == 0) break
                signatureLength += count
            }
        } finally {
            input.resetPeekPosition()
        }
        if (!XvidFourCcPolicy.hasMatroskaHeader(signature, signatureLength)) return IntArray(0)

        val inputLength = input.length
        val scanLength = when {
            inputLength == C.LENGTH_UNSET.toLong() -> XvidFourCcPolicy.MAX_HEADER_SCAN_BYTES
            else -> inputLength.coerceAtMost(XvidFourCcPolicy.MAX_HEADER_SCAN_BYTES.toLong()).toInt()
        }
        if (scanLength < signature.size) return IntArray(0)

        val header = ByteArray(scanLength)
        var bytesRead = 0
        try {
            while (bytesRead < scanLength) {
                val count = input.peek(header, bytesRead, scanLength - bytesRead)
                if (count == C.RESULT_END_OF_INPUT || count == 0) break
                bytesRead += count
            }
        } finally {
            input.resetPeekPosition()
        }
        return XvidFourCcPolicy.findXvidCompressionOffsets(header, bytesRead)
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
private class XvidFourCcSubstitutingInput(
    input: ExtractorInput,
    private val compressionOffsets: LongArray,
) : ForwardingExtractorInput(input) {

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val absoluteStart = position
        return super.read(buffer, offset, length).also { count ->
            if (count > 0) substitute(buffer, offset, count, absoluteStart)
        }
    }

    override fun readFully(
        target: ByteArray,
        offset: Int,
        length: Int,
        allowEndOfInput: Boolean,
    ): Boolean {
        val absoluteStart = position
        return super.readFully(target, offset, length, allowEndOfInput).also { completed ->
            if (completed) substitute(target, offset, length, absoluteStart)
        }
    }

    override fun readFully(target: ByteArray, offset: Int, length: Int) {
        val absoluteStart = position
        super.readFully(target, offset, length)
        substitute(target, offset, length, absoluteStart)
    }

    override fun peek(target: ByteArray, offset: Int, length: Int): Int {
        val absoluteStart = peekPosition
        return super.peek(target, offset, length).also { count ->
            if (count > 0) substitute(target, offset, count, absoluteStart)
        }
    }

    override fun peekFully(
        target: ByteArray,
        offset: Int,
        length: Int,
        allowEndOfInput: Boolean,
    ): Boolean {
        val absoluteStart = peekPosition
        return super.peekFully(target, offset, length, allowEndOfInput).also { completed ->
            if (completed) substitute(target, offset, length, absoluteStart)
        }
    }

    override fun peekFully(target: ByteArray, offset: Int, length: Int) {
        val absoluteStart = peekPosition
        super.peekFully(target, offset, length)
        substitute(target, offset, length, absoluteStart)
    }

    private fun substitute(
        target: ByteArray,
        offset: Int,
        length: Int,
        absoluteStart: Long,
    ) {
        repeat(length) { index ->
            val targetIndex = offset + index
            target[targetIndex] = XvidFourCcPolicy.replacementByte(
                absolutePosition = absoluteStart + index,
                original = target[targetIndex],
                compressionOffsets = compressionOffsets,
            )
        }
    }
}
