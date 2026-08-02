package io.github.supermonster003.autojs6.plugin.videoplayer

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Locale

internal data class GrantEnvelope(
    val read: Boolean,
    val write: Boolean,
    val persistable: Boolean,
    val prefix: Boolean,
)

internal data class ClipItemEnvelope(
    val uri: String?,
    val hasText: Boolean = false,
    val hasHtmlText: Boolean = false,
    val hasIntent: Boolean = false,
)

internal data class ExplorerRequestEnvelope(
    val action: String?,
    val actionId: String?,
    val protocolVersion: Int?,
    val sourceSurface: String?,
    val hostVersionCode: Long?,
    val targetUri: String?,
    val parentUri: String?,
    val clipItems: List<ClipItemEnvelope>,
    val grants: GrantEnvelope,
    val displayName: String?,
    val declaredSize: Long?,
    val mimeType: String?,
)

internal data class ExternalRequestEnvelope(
    val action: String?,
    val targetUri: String?,
    val grants: GrantEnvelope,
    val mimeType: String?,
)

internal data class InternalRequestEnvelope(
    val action: String?,
    val targetUri: String?,
    val clipItems: List<ClipItemEnvelope>,
    val grants: GrantEnvelope,
    val displayName: String?,
    val mimeType: String?,
    val extraKeys: Set<String>,
)

internal data class ValidatedExplorerRequest(
    val targetUri: String,
    val parentUri: String,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

internal data class ValidatedPlaybackRequest(
    val targetUri: String,
    val displayName: String,
    val mimeType: String,
)

internal data class ValidatedExternalRequest(
    val targetUri: String,
    val mimeType: String,
)

/** Pure validation for every trust boundary, kept Android-free so it is locally testable. */
internal object VideoRequestPolicy {

    const val EXPLORER_EXECUTE_ACTION = "org.autojs.plugin.EXPLORER_ACTION_EXECUTE"
    const val EXTERNAL_VIEW_ACTION = "android.intent.action.VIEW"
    const val INTERNAL_PLAY_ACTION =
        "io.github.supermonster003.autojs6.plugin.videoplayer.action.PLAY_VALIDATED"
    const val SOURCE_SURFACE_MAIN = "main"
    const val PROTOCOL_VERSION = 2
    const val DISPLAY_NAME_EXTRA =
        "io.github.supermonster003.autojs6.plugin.videoplayer.extra.DISPLAY_NAME"
    const val MAX_DISPLAY_NAME_LENGTH = 255

    private val mimeTokenPattern = Regex("[a-z0-9][a-z0-9!#$&^_.+-]*")
    private val supportedExtensions = VideoPlayerPlugin.EXTENSIONS.toSet()

    fun validateExplorer(envelope: ExplorerRequestEnvelope): ValidatedExplorerRequest? {
        if (envelope.action != EXPLORER_EXECUTE_ACTION) return null
        if (envelope.actionId != VideoPlayerPlugin.ACTION_ID) return null
        if (envelope.protocolVersion != PROTOCOL_VERSION) return null
        if (envelope.sourceSurface != SOURCE_SURFACE_MAIN) return null
        if ((envelope.hostVersionCode ?: return null) < VideoPlayerPlugin.REQUIRED_HOST_VERSION) return null
        if (!envelope.grants.isExplorerReadOnly()) return null

        val target = parsePlainContentUri(envelope.targetUri) ?: return null
        val parent = parsePlainContentUri(envelope.parentUri) ?: return null
        if (!target.isStrictDescendantOf(parent)) return null

        if (envelope.clipItems.size != 2) return null
        if (!envelope.clipItems[0].isExactUri(target.original)) return null
        if (!envelope.clipItems[1].isExactUri(parent.original)) return null

        val displayName = validateExactDisplayName(envelope.displayName, target) ?: return null
        if (displayName.substringAfterLast('.', "").lowercase(Locale.ROOT) !in supportedExtensions) {
            return null
        }
        val declaredSize = envelope.declaredSize?.takeIf { it >= 0L } ?: return null
        val mimeType = normalizeVideoMimeType(envelope.mimeType) ?: return null

        return ValidatedExplorerRequest(
            targetUri = target.original,
            parentUri = parent.original,
            displayName = displayName,
            declaredSize = declaredSize,
            mimeType = mimeType,
        )
    }

    fun validateExternal(envelope: ExternalRequestEnvelope): ValidatedExternalRequest? {
        if (envelope.action != EXTERNAL_VIEW_ACTION) return null
        if (!envelope.grants.isExactReadOnly()) return null
        val target = parsePlainContentUri(envelope.targetUri) ?: return null
        val mimeType = normalizeVideoMimeType(envelope.mimeType) ?: return null
        return ValidatedExternalRequest(target.original, mimeType)
    }

    fun validateInternal(envelope: InternalRequestEnvelope): ValidatedPlaybackRequest? {
        if (envelope.action != INTERNAL_PLAY_ACTION) return null
        if (!envelope.grants.isExactReadOnly()) return null
        val target = parsePlainContentUri(envelope.targetUri) ?: return null
        if (envelope.clipItems.size != 1 || !envelope.clipItems[0].isExactUri(target.original)) {
            return null
        }
        if (envelope.extraKeys != setOf(DISPLAY_NAME_EXTRA)) return null
        val displayName = validateSafeDisplayName(envelope.displayName) ?: return null
        val mimeType = normalizeVideoMimeType(envelope.mimeType) ?: return null
        return ValidatedPlaybackRequest(target.original, displayName, mimeType)
    }

    fun sanitizeExternalDisplayName(candidate: String?, fallback: String?): String {
        return sequenceOf(candidate, fallback)
            .mapNotNull { value ->
                value
                    ?.filterNot(::isUnsafeDisplayCharacter)
                    ?.replace('/', '_')
                    ?.replace('\\', '_')
                    ?.trim()
                    ?.take(MAX_DISPLAY_NAME_LENGTH)
                    ?.takeIf { it.isNotBlank() && it != "." && it != ".." }
            }
            .firstOrNull()
            ?: "video"
    }

    fun normalizeVideoMimeType(value: String?): String? {
        val raw = value ?: return null
        if (raw.isEmpty() || raw != raw.trim()) return null
        val normalized = raw.lowercase(Locale.ROOT)
        if (raw != normalized) return null

        val parts = normalized.split('/')
        if (parts.size != 2 || parts[0] != "video") return null
        val subtype = parts[1]
        if (subtype != "*" && !mimeTokenPattern.matches(subtype)) return null
        return normalized
    }

    private fun GrantEnvelope.isExplorerReadOnly(): Boolean =
        read && !write && !persistable && prefix

    private fun GrantEnvelope.isExactReadOnly(): Boolean =
        read && !write && !persistable && !prefix

    private fun ClipItemEnvelope.isExactUri(expected: String): Boolean =
        uri == expected && !hasText && !hasHtmlText && !hasIntent

    private fun validateExactDisplayName(value: String?, target: ParsedContentUri): String? {
        val name = validateSafeDisplayName(value) ?: return null
        return name.takeIf { target.segments.lastOrNull() == it }
    }

    private fun validateSafeDisplayName(value: String?): String? {
        val name = value ?: return null
        if (name.length !in 1..MAX_DISPLAY_NAME_LENGTH || name.isBlank()) return null
        if (name == "." || name == "..") return null
        if (name.any(::isUnsafeDisplayCharacter)) return null
        return name
    }

    private fun parsePlainContentUri(value: String?): ParsedContentUri? = try {
        val original = value ?: return null
        val uri = URI(original)
        if (uri.scheme != "content" || uri.isOpaque) return null
        val authority = uri.rawAuthority ?: return null
        if (authority.isBlank() || '@' in authority || ':' in authority || '%' in authority) return null
        if (uri.rawQuery != null || uri.rawFragment != null) return null
        val rawPath = uri.rawPath ?: return null
        if (!rawPath.startsWith('/') || rawPath.length <= 1 || rawPath.endsWith('/')) return null
        val rawSegments = rawPath.removePrefix("/").split('/')
        if (rawSegments.any(String::isEmpty)) return null
        val decodedSegments = rawSegments.map { rawSegment ->
            URLDecoder.decode(rawSegment.replace("+", "%2B"), StandardCharsets.UTF_8.name())
        }
        if (decodedSegments.any { segment ->
                segment.isEmpty() || segment == "." || segment == ".." ||
                    segment.any(::isUnsafeUriCharacter) || '\uFFFD' in segment
            }
        ) {
            return null
        }
        ParsedContentUri(original, authority, decodedSegments)
    } catch (_: IllegalArgumentException) {
        null
    }

    private fun ParsedContentUri.isStrictDescendantOf(parent: ParsedContentUri): Boolean =
        authority == parent.authority &&
            segments.size > parent.segments.size &&
            segments.take(parent.segments.size) == parent.segments

    private fun isUnsafeDisplayCharacter(character: Char): Boolean =
        character == '/' || character == '\\' || isUnsafeUnicodeCharacter(character)

    private fun isUnsafeUriCharacter(character: Char): Boolean =
        character == '/' || character == '\\' || isUnsafeUnicodeCharacter(character)

    private fun isUnsafeUnicodeCharacter(character: Char): Boolean =
        character.isISOControl() || Character.getType(character) == Character.FORMAT.toInt()

    private data class ParsedContentUri(
        val original: String,
        val authority: String,
        val segments: List<String>,
    )
}

internal data class ForwardedPlaybackSpec(
    val action: String,
    val targetUri: String,
    val mimeType: String,
    val displayName: String,
    val clipUris: List<String>,
    val grantRead: Boolean,
    val grantWrite: Boolean,
    val grantPersistable: Boolean,
    val grantPrefix: Boolean,
    val extras: Map<String, String>,
)

internal object PlaybackForwardingPolicy {

    fun fromExplorer(request: ValidatedExplorerRequest): ForwardedPlaybackSpec = build(
        targetUri = request.targetUri,
        mimeType = request.mimeType,
        displayName = request.displayName,
    )

    fun fromExternal(
        request: ValidatedExternalRequest,
        displayName: String,
    ): ForwardedPlaybackSpec = build(
        targetUri = request.targetUri,
        mimeType = request.mimeType,
        displayName = VideoRequestPolicy.sanitizeExternalDisplayName(displayName, null),
    )

    private fun build(
        targetUri: String,
        mimeType: String,
        displayName: String,
    ) = ForwardedPlaybackSpec(
        action = VideoRequestPolicy.INTERNAL_PLAY_ACTION,
        targetUri = targetUri,
        mimeType = mimeType,
        displayName = displayName,
        clipUris = listOf(targetUri),
        grantRead = true,
        grantWrite = false,
        grantPersistable = false,
        grantPrefix = false,
        extras = mapOf(VideoRequestPolicy.DISPLAY_NAME_EXTRA to displayName),
    )
}
