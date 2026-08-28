package io.github.supermonster003.autojs6.plugin.videoplayer

import java.util.Locale

internal data class ExplorerSiblingItem(
    val relativePath: String,
    val displayName: String,
    val kind: Int,
    val mimeType: String,
    val size: Long,
    val lastModified: Long,
    val readable: Boolean,
    val symbolicLink: Boolean,
)

internal data class DiscoveredSubtitle(
    val relativePath: String,
    val displayName: String,
    val mimeType: String,
    val languageTag: String?,
)

internal data class DiscoveredVideo(
    val relativePath: String,
    val displayName: String,
    val mimeType: String,
    val size: Long,
    val subtitles: List<DiscoveredSubtitle>,
)

internal data class DiscoveredVideoQueue(
    val items: List<DiscoveredVideo>,
    val startIndex: Int,
)

/** Pure direct-sibling filtering, natural ordering and sidecar-subtitle matching. */
internal object HostMediaDiscoveryPolicy {

    const val MAX_QUEUE_ENTRIES = 128
    const val MAX_SUBTITLES_PER_VIDEO = 8
    const val MAX_TOTAL_SUBTITLE_ATTACHMENTS = 128
    private const val TARGET_KIND_FILE = 1

    fun discover(
        selectedDisplayName: String,
        selectedMimeType: String,
        selectedSize: Long,
        siblings: List<ExplorerSiblingItem>,
    ): DiscoveredVideoQueue {
        require(isSafeOneSegment(selectedDisplayName))
        require(VideoRequestPolicy.normalizeVideoMimeType(selectedMimeType) != null)

        val safeFiles = siblings.asSequence()
            .filter { it.kind == TARGET_KIND_FILE && it.readable && !it.symbolicLink }
            .filter { it.relativePath == it.displayName && isSafeOneSegment(it.displayName) }
            .distinctBy(ExplorerSiblingItem::relativePath)
            .toList()
        val subtitleFiles = safeFiles.filter { extension(it.displayName) in SUBTITLE_MIME_TYPES }
        val videosByName = safeFiles
            .filter { item ->
                VideoRequestPolicy.normalizeVideoMimeType(item.mimeType) != null ||
                    extension(item.displayName) in VideoPlayerPlugin.EXTENSIONS
            }
            .associateBy(ExplorerSiblingItem::displayName)
            .toMutableMap()
        videosByName.putIfAbsent(
            selectedDisplayName,
            ExplorerSiblingItem(
                relativePath = selectedDisplayName,
                displayName = selectedDisplayName,
                kind = TARGET_KIND_FILE,
                mimeType = selectedMimeType,
                size = selectedSize,
                lastModified = -1L,
                readable = true,
                symbolicLink = false,
            ),
        )

        val sorted = videosByName.values.sortedWith { first, second ->
            compareNaturally(first.displayName, second.displayName)
        }
        val selectedSortedIndex = sorted.indexOfFirst { it.displayName == selectedDisplayName }
        check(selectedSortedIndex >= 0)
        val windowStart = (selectedSortedIndex - MAX_QUEUE_ENTRIES / 2)
            .coerceIn(0, (sorted.size - MAX_QUEUE_ENTRIES).coerceAtLeast(0))
        val bounded = sorted.drop(windowStart).take(MAX_QUEUE_ENTRIES)
        var remainingSubtitleAttachments = MAX_TOTAL_SUBTITLE_ATTACHMENTS
        val videos = bounded.map { video ->
            val videoStem = stem(video.displayName)
            val subtitles = subtitleFiles.asSequence()
                .filter { subtitle -> isSidecarFor(videoStem, subtitle.displayName) }
                .sortedWith { first, second -> compareNaturally(first.displayName, second.displayName) }
                .take(MAX_SUBTITLES_PER_VIDEO.coerceAtMost(remainingSubtitleAttachments))
                .map { subtitle ->
                    val subtitleStem = stem(subtitle.displayName)
                    DiscoveredSubtitle(
                        relativePath = subtitle.relativePath,
                        displayName = subtitle.displayName,
                        mimeType = requireNotNull(SUBTITLE_MIME_TYPES[extension(subtitle.displayName)]),
                        languageTag = languageSuffix(videoStem, subtitleStem),
                    )
                }
                .toList()
            remainingSubtitleAttachments -= subtitles.size
            DiscoveredVideo(
                relativePath = if (video.displayName == selectedDisplayName) "" else video.relativePath,
                displayName = video.displayName,
                mimeType = VideoRequestPolicy.normalizeVideoMimeType(video.mimeType) ?: "video/*",
                size = video.size.coerceAtLeast(VideoRequestPolicy.UNKNOWN_DECLARED_SIZE),
                subtitles = subtitles,
            )
        }
        return DiscoveredVideoQueue(
            items = videos,
            startIndex = videos.indexOfFirst { it.displayName == selectedDisplayName }.also {
                check(it >= 0)
            },
        )
    }

    fun compareNaturally(first: String, second: String): Int {
        var firstIndex = 0
        var secondIndex = 0
        var stableCaseComparison = 0
        while (firstIndex < first.length && secondIndex < second.length) {
            val firstCharacter = first[firstIndex]
            val secondCharacter = second[secondIndex]
            if (firstCharacter.isDigit() && secondCharacter.isDigit()) {
                val firstEnd = first.consumeDigits(firstIndex)
                val secondEnd = second.consumeDigits(secondIndex)
                val firstDigits = first.substring(firstIndex, firstEnd)
                val secondDigits = second.substring(secondIndex, secondEnd)
                val firstSignificant = firstDigits.trimStart('0').ifEmpty { "0" }
                val secondSignificant = secondDigits.trimStart('0').ifEmpty { "0" }
                val numericComparison = firstSignificant.length.compareTo(secondSignificant.length)
                    .takeIf { it != 0 }
                    ?: firstSignificant.compareTo(secondSignificant).takeIf { it != 0 }
                    ?: firstDigits.length.compareTo(secondDigits.length)
                if (numericComparison != 0) return numericComparison
                firstIndex = firstEnd
                secondIndex = secondEnd
                continue
            }
            val foldedComparison = firstCharacter.lowercaseChar().compareTo(secondCharacter.lowercaseChar())
            if (foldedComparison != 0) return foldedComparison
            if (stableCaseComparison == 0) {
                stableCaseComparison = firstCharacter.compareTo(secondCharacter)
            }
            firstIndex += 1
            secondIndex += 1
        }
        return first.length.compareTo(second.length).takeIf { it != 0 } ?: stableCaseComparison
    }

    private fun String.consumeDigits(start: Int): Int {
        var index = start
        while (index < length && this[index].isDigit()) index += 1
        return index
    }

    private fun isSidecarFor(videoStem: String, subtitleName: String): Boolean {
        val subtitleStem = stem(subtitleName)
        return subtitleStem == videoStem || languageSuffix(videoStem, subtitleStem) != null
    }

    private fun languageSuffix(videoStem: String, subtitleStem: String): String? {
        if (!subtitleStem.startsWith("$videoStem.")) return null
        val suffix = subtitleStem.substring(videoStem.length + 1)
        val normalized = suffix.replace('_', '-')
        return normalized.takeIf(LANGUAGE_TAG::matches)
    }

    private fun stem(name: String): String = name.substringBeforeLast('.', name)

    private fun extension(name: String): String =
        name.substringAfterLast('.', "").lowercase(Locale.ROOT)

    private fun isSafeOneSegment(value: String): Boolean =
        value.length in 1..VideoRequestPolicy.MAX_DISPLAY_NAME_LENGTH && value.isNotBlank() &&
            value != "." && value != ".." &&
            '/' !in value && '\\' !in value && value.none { character ->
                character.isISOControl() || Character.getType(character) == Character.FORMAT.toInt()
            }

    private val LANGUAGE_TAG = Regex("[A-Za-z]{2,3}(?:-[A-Za-z0-9]{2,8})*")
    private val SUBTITLE_MIME_TYPES = mapOf(
        "srt" to "application/x-subrip",
        "ass" to "text/x-ssa",
    )
}
