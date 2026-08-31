package io.github.supermonster003.autojs6.plugin.threeemberplayer

import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

internal enum class SubtitleTextEncoding(
    val charsetName: String,
    val byteOrderMark: ByteArray = byteArrayOf(),
) {
    UTF_8("UTF-8", byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())),
    UTF_16_LE("UTF-16LE", byteArrayOf(0xFF.toByte(), 0xFE.toByte())),
    UTF_16_BE("UTF-16BE", byteArrayOf(0xFE.toByte(), 0xFF.toByte())),
    GBK("GBK"),
    BIG5("Big5"),
    SHIFT_JIS("Shift_JIS"),
    EUC_KR("EUC-KR"),
    WINDOWS_1251("windows-1251"),
    WINDOWS_1256("windows-1256"),
}

internal data class SubtitleDecodingResult(
    val text: String,
    val encoding: SubtitleTextEncoding,
    /** False means UTF-8 replacement decoding was used and the UI should warn about garbling. */
    val confident: Boolean,
)

/**
 * Small, deterministic subtitle-oriented detector. BOM wins, strict UTF-8 is preferred, and
 * legacy candidates are ranked by valid decoding plus the script expected from each code page.
 */
internal object SubtitleEncodingDetector {

    private val legacyEncodings = listOf(
        SubtitleTextEncoding.GBK,
        SubtitleTextEncoding.BIG5,
        SubtitleTextEncoding.SHIFT_JIS,
        SubtitleTextEncoding.EUC_KR,
        SubtitleTextEncoding.WINDOWS_1251,
        SubtitleTextEncoding.WINDOWS_1256,
    )

    private val simplifiedChinese = "这为个来时说国后对发经现当没还过从与样电视话见开们东车门体书万龙云里边听爱"
        .toSet()
    private val traditionalChinese = "這為個來時說國後對發經現當沒還過從與樣電視話見開們東車門體書萬龍雲裡邊聽愛"
        .toSet()
    private val commonRussian = "оеаинтср".toSet()
    private val commonArabic = "ايلمنوتر".toSet()
    private val russianBigrams = listOf(
        "ст", "но", "то", "на", "ен", "ов", "ни", "ра", "во", "ко", "пр", "ро", "го", "по", "ре",
    )
    private val arabicBigrams = listOf("ال", "في", "من", "تر", "ية", "تش", "دي", "يو", "بد", "عر")

    fun decode(bytes: ByteArray): SubtitleDecodingResult {
        bomEncoding(bytes)?.let { encoding ->
            val content = bytes.copyOfRange(encoding.byteOrderMark.size, bytes.size)
            return SubtitleDecodingResult(
                text = decodeStrict(content, encoding) ?: String(content, charset(encoding)),
                encoding = encoding,
                confident = true,
            )
        }

        decodeStrict(bytes, SubtitleTextEncoding.UTF_8)?.let { utf8 ->
            return SubtitleDecodingResult(utf8, SubtitleTextEncoding.UTF_8, confident = true)
        }

        val ranked = legacyEncodings.mapNotNull { encoding ->
            decodeStrict(bytes, encoding)?.let { decoded ->
                Candidate(
                    encoding,
                    decoded,
                    score(bytes, decoded, encoding),
                    expectedScriptCount(decoded, encoding),
                )
            }
        }.sortedByDescending(Candidate::score)
        val best = ranked.firstOrNull()
        val second = ranked.getOrNull(1)
        if (best != null && best.expectedScriptCount > 0 &&
            (second == null || best.score - second.score >= MIN_CONFIDENCE_MARGIN)
        ) {
            return SubtitleDecodingResult(best.text, best.encoding, confident = true)
        }

        // A wrong legacy guess is worse than a visible replacement character: keep fallback
        // deterministic and let the caller explain that the encoding could not be identified.
        return SubtitleDecodingResult(
            text = String(bytes, charset(SubtitleTextEncoding.UTF_8)),
            encoding = SubtitleTextEncoding.UTF_8,
            confident = false,
        )
    }

    private fun bomEncoding(bytes: ByteArray): SubtitleTextEncoding? =
        listOf(
            SubtitleTextEncoding.UTF_8,
            SubtitleTextEncoding.UTF_16_LE,
            SubtitleTextEncoding.UTF_16_BE,
        ).firstOrNull { encoding -> bytes.startsWith(encoding.byteOrderMark) }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean =
        size >= prefix.size && prefix.indices.all { index -> this[index] == prefix[index] }

    private fun decodeStrict(bytes: ByteArray, encoding: SubtitleTextEncoding): String? = try {
        charset(encoding).newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes))
            .toString()
    } catch (_: CharacterCodingException) {
        null
    }

    private fun score(bytes: ByteArray, text: String, encoding: SubtitleTextEncoding): Int {
        val expected = expectedScriptCount(text, encoding)
        val common = when (encoding) {
            SubtitleTextEncoding.GBK -> text.count(simplifiedChinese::contains)
            SubtitleTextEncoding.BIG5 -> text.count(traditionalChinese::contains)
            SubtitleTextEncoding.WINDOWS_1251 -> text.lowercase().count(commonRussian::contains)
            SubtitleTextEncoding.WINDOWS_1256 -> text.count(commonArabic::contains)
            else -> 0
        }
        val printable = text.count { character ->
            character == '\n' || character == '\r' || character == '\t' || !character.isISOControl()
        }
        val controls = text.length - printable
        val subtitleStructure = TIMESTAMP_MARKERS.count { marker -> marker in text }
        val unexpectedNonAscii = text.count { character ->
            character.code >= 0x80 && !isExpectedScript(character, encoding) &&
                Character.getType(character) !in ACCEPTED_NON_ASCII_CATEGORIES
        }
        return expected * scriptWeight(encoding) + common * commonLanguageWeight(encoding) +
            subtitleStructure * STRUCTURE_WEIGHT + byteShapeScore(bytes, encoding) +
            languageSequenceScore(text, encoding) -
            controls * CONTROL_PENALTY - unexpectedNonAscii * UNEXPECTED_CHARACTER_PENALTY
    }

    private fun expectedScriptCount(text: String, encoding: SubtitleTextEncoding): Int = when (encoding) {
        SubtitleTextEncoding.GBK,
        SubtitleTextEncoding.BIG5,
        -> text.count(::isHan)
        SubtitleTextEncoding.SHIFT_JIS -> text.count { it in '\u3040'..'\u30ff' }
        SubtitleTextEncoding.EUC_KR -> text.count { it in '\uac00'..'\ud7af' || it in '\u1100'..'\u11ff' }
        SubtitleTextEncoding.WINDOWS_1251 -> text.count { it in '\u0400'..'\u052f' }
        SubtitleTextEncoding.WINDOWS_1256 -> text.count { it in '\u0600'..'\u06ff' || it in '\u0750'..'\u077f' }
        else -> 0
    }

    private fun scriptWeight(encoding: SubtitleTextEncoding): Int = when (encoding) {
        SubtitleTextEncoding.GBK,
        SubtitleTextEncoding.BIG5,
        -> HAN_SCRIPT_WEIGHT
        SubtitleTextEncoding.SHIFT_JIS,
        SubtitleTextEncoding.EUC_KR,
        -> DISTINCT_SCRIPT_WEIGHT
        else -> SINGLE_BYTE_SCRIPT_WEIGHT
    }

    private fun commonLanguageWeight(encoding: SubtitleTextEncoding): Int = when (encoding) {
        SubtitleTextEncoding.GBK,
        SubtitleTextEncoding.BIG5,
        -> CJK_COMMON_LANGUAGE_WEIGHT
        else -> SINGLE_BYTE_COMMON_LANGUAGE_WEIGHT
    }

    private fun languageSequenceScore(text: String, encoding: SubtitleTextEncoding): Int {
        val normalized = text.lowercase()
        val sequences = when (encoding) {
            SubtitleTextEncoding.WINDOWS_1251 -> russianBigrams
            SubtitleTextEncoding.WINDOWS_1256 -> arabicBigrams
            else -> return 0
        }
        return sequences.sumOf { sequence ->
            var matches = 0
            var start = normalized.indexOf(sequence)
            while (start >= 0) {
                matches += 1
                start = normalized.indexOf(sequence, start + sequence.length)
            }
            matches
        } * LANGUAGE_SEQUENCE_WEIGHT
    }

    private fun isHan(character: Char): Boolean =
        Character.UnicodeScript.of(character.code) == Character.UnicodeScript.HAN

    private fun isExpectedScript(character: Char, encoding: SubtitleTextEncoding): Boolean = when (encoding) {
        SubtitleTextEncoding.GBK,
        SubtitleTextEncoding.BIG5,
        -> isHan(character)
        SubtitleTextEncoding.SHIFT_JIS -> character in '\u3040'..'\u30ff' || isHan(character)
        SubtitleTextEncoding.EUC_KR ->
            character in '\uac00'..'\ud7af' || character in '\u1100'..'\u11ff' || isHan(character)
        SubtitleTextEncoding.WINDOWS_1251 -> character in '\u0400'..'\u052f'
        SubtitleTextEncoding.WINDOWS_1256 ->
            character in '\u0600'..'\u06ff' || character in '\u0750'..'\u077f'
        else -> false
    }

    private fun byteShapeScore(bytes: ByteArray, encoding: SubtitleTextEncoding): Int = when (encoding) {
        SubtitleTextEncoding.WINDOWS_1251 -> bytes.sumOf { byte ->
            when (byte.toInt() and 0xFF) {
                in 0xC0..0xFF, 0xA8, 0xB8 -> 2
                in 0x80..0xBF -> -2
                else -> 0
            }
        }
        SubtitleTextEncoding.WINDOWS_1256 -> bytes.sumOf { byte ->
            when (byte.toInt() and 0xFF) {
                in 0xC1..0xDA, in 0xE0..0xF2 -> 2
                in 0x80..0xC0, in 0xDB..0xDF, in 0xF3..0xFF -> -2
                else -> 0
            }
        }
        else -> 0
    }

    private fun charset(encoding: SubtitleTextEncoding): Charset = Charset.forName(encoding.charsetName)

    private data class Candidate(
        val encoding: SubtitleTextEncoding,
        val text: String,
        val score: Int,
        val expectedScriptCount: Int,
    )

    private val TIMESTAMP_MARKERS = listOf("-->", "Dialogue:", "[Events]")
    private const val HAN_SCRIPT_WEIGHT = 5
    private const val DISTINCT_SCRIPT_WEIGHT = 12
    private const val SINGLE_BYTE_SCRIPT_WEIGHT = 4
    private val ACCEPTED_NON_ASCII_CATEGORIES = setOf(
        Character.SPACE_SEPARATOR.toInt(),
        Character.DASH_PUNCTUATION.toInt(),
        Character.START_PUNCTUATION.toInt(),
        Character.END_PUNCTUATION.toInt(),
        Character.OTHER_PUNCTUATION.toInt(),
        Character.INITIAL_QUOTE_PUNCTUATION.toInt(),
        Character.FINAL_QUOTE_PUNCTUATION.toInt(),
    )
    private const val CJK_COMMON_LANGUAGE_WEIGHT = 20
    private const val SINGLE_BYTE_COMMON_LANGUAGE_WEIGHT = 4
    private const val LANGUAGE_SEQUENCE_WEIGHT = 24
    private const val STRUCTURE_WEIGHT = 4
    private const val CONTROL_PENALTY = 20
    private const val UNEXPECTED_CHARACTER_PENALTY = 7
    private const val MIN_CONFIDENCE_MARGIN = 3
}

internal data class ProcessedSubtitleText(
    val utf8Bytes: ByteArray,
    val detectedEncoding: SubtitleTextEncoding,
    val encodingConfident: Boolean,
)

internal object SubtitleTextProcessor {
    fun process(bytes: ByteArray, mimeType: String, offsetMs: Long): ProcessedSubtitleText {
        val decoded = SubtitleEncodingDetector.decode(bytes)
        val shifted = SubtitleTimingPolicy.shift(decoded.text, mimeType, offsetMs)
        return ProcessedSubtitleText(
            utf8Bytes = shifted.toByteArray(Charsets.UTF_8),
            detectedEncoding = decoded.encoding,
            encodingConfident = decoded.confident,
        )
    }
}
