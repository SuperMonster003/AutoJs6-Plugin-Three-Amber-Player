package io.github.supermonster003.autojs6.plugin.threeemberplayer

import java.nio.charset.Charset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubtitleEncodingDetectorTest {

    private val samples = linkedMapOf(
        SubtitleTextEncoding.GBK to listOf(
            "1\n00:00:01,000 --> 00:00:03,000\n这是一个视频字幕，现在开始播放。",
            "2\n00:00:04,000 --> 00:00:06,000\n我们从东门回来，听见电话。",
        ),
        SubtitleTextEncoding.BIG5 to listOf(
            "1\n00:00:01,000 --> 00:00:03,000\n這是一個影片字幕，現在開始播放。",
            "2\n00:00:04,000 --> 00:00:06,000\n我們從東門回來，聽見電話。",
        ),
        SubtitleTextEncoding.SHIFT_JIS to listOf(
            "1\n00:00:01,000 --> 00:00:03,000\nこれは日本語の字幕です。",
            "2\n00:00:04,000 --> 00:00:06,000\nビデオの再生を開始します。",
        ),
        SubtitleTextEncoding.EUC_KR to listOf(
            "1\n00:00:01,000 --> 00:00:03,000\n이것은 한국어 자막입니다.",
            "2\n00:00:04,000 --> 00:00:06,000\n동영상 재생을 시작합니다.",
        ),
        SubtitleTextEncoding.WINDOWS_1251 to listOf(
            "1\n00:00:01,000 --> 00:00:03,000\nЭто русские субтитры.",
            "2\n00:00:04,000 --> 00:00:06,000\nНачинается воспроизведение видео.",
        ),
        SubtitleTextEncoding.WINDOWS_1256 to listOf(
            "1\n00:00:01,000 --> 00:00:03,000\nهذه ترجمة عربية للفيديو.",
            "2\n00:00:04,000 --> 00:00:06,000\nيبدأ تشغيل المقطع الآن.",
        ),
    )

    @Test
    fun bom_hasPriorityAndIsRemovedFromText() {
        val body = "1\n00:00:01,000 --> 00:00:02,000\nHello"
        listOf(
            SubtitleTextEncoding.UTF_8,
            SubtitleTextEncoding.UTF_16_LE,
            SubtitleTextEncoding.UTF_16_BE,
        ).forEach { encoding ->
            val bytes = encoding.byteOrderMark + body.toByteArray(Charset.forName(encoding.charsetName))
            val result = SubtitleEncodingDetector.decode(bytes)
            assertEquals(encoding, result.encoding)
            assertEquals(body, result.text)
            assertTrue(result.confident)
        }
    }

    @Test
    fun eachLegacyEncoding_hasTwoPositiveSubtitleSamples() {
        samples.forEach { (encoding, texts) ->
            texts.forEach { text ->
                val result = SubtitleEncodingDetector.decode(
                    text.toByteArray(Charset.forName(encoding.charsetName)),
                )
                assertEquals("Failed to detect $encoding for $text", encoding, result.encoding)
                assertEquals(text, result.text)
                assertTrue(result.confident)
            }
        }
    }

    @Test
    fun eachLegacyEncoding_rejectsTwoOtherEncodedSamples() {
        val entries = samples.entries.toList()
        entries.forEachIndexed { index, (encoding, _) ->
            listOf(entries[(index + 1) % entries.size], entries[(index + 2) % entries.size])
                .forEach { (otherEncoding, texts) ->
                    val result = SubtitleEncodingDetector.decode(
                        texts.first().toByteArray(Charset.forName(otherEncoding.charsetName)),
                    )
                    assertNotEquals(
                        "$otherEncoding sample must not be detected as $encoding",
                        encoding,
                        result.encoding,
                    )
                }
        }
    }

    @Test
    fun uncertainInvalidInput_fallsBackToUtf8AndRequestsWarning() {
        val result = SubtitleEncodingDetector.decode(byteArrayOf(0x81.toByte(), 0x81.toByte(), 0x81.toByte()))
        assertEquals(SubtitleTextEncoding.UTF_8, result.encoding)
        assertFalse(result.confident)
    }

    @Test
    fun processor_outputsUtf8AndAppliesOffsetInMemory() {
        val source = samples.getValue(SubtitleTextEncoding.WINDOWS_1251).first()
        val result = SubtitleTextProcessor.process(
            source.toByteArray(Charset.forName("windows-1251")),
            "application/x-subrip",
            500L,
        )
        val output = result.utf8Bytes.toString(Charsets.UTF_8)
        assertTrue("00:00:01,500 --> 00:00:03,500" in output)
        assertTrue("Это русские субтитры" in output)
    }
}
