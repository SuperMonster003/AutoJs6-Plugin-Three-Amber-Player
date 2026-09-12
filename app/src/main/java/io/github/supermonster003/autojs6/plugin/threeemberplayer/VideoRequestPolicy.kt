package io.github.supermonster003.autojs6.plugin.threeemberplayer

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistParser
import io.github.supermonster003.autojs6.plugin.threeemberplayer.playlist.PlaylistLocationPolicy
import java.util.Locale
import java.util.UUID

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
    val requestId: String?,
    val dataUri: String?,
    val parentUri: String?,
    val parentDisplayPath: String?,
    val clipItems: List<ClipItemEnvelope>,
    val grants: GrantEnvelope,
    val targets: List<ExplorerTargetEnvelope>,
    val envelopeDisplayName: String?,
    val envelopeDeclaredSize: Long?,
    val envelopeMimeType: String?,
    val hostSessionPresent: Boolean,
)

internal data class ExplorerTargetEnvelope(
    val targetId: String?,
    val targetUri: String?,
    val targetKind: Int?,
    val displayName: String?,
    val declaredSize: Long?,
    val lastModified: Long?,
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
    val declaredSize: Long?,
    val mimeType: String?,
    val extraKeys: Set<String>,
)

internal data class ValidatedExplorerRequest(
    val targetId: String,
    val targetUri: String,
    val parentUri: String,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

internal data class ValidatedExplorerRequestGroup(
    val actionId: String,
    val parentUri: String,
    val targets: List<ValidatedExplorerRequest>,
) {
    val isSelection: Boolean
        get() = actionId == ThreeEmberPlayerPlugin.ACTION_SELECTION_ID

    val primaryTarget: ValidatedExplorerRequest
        get() = targets.first()
}

internal data class ValidatedPlaybackRequest(
    val targetUri: String,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

internal data class ValidatedExternalRequest(
    val targetUri: String,
    val mimeType: String,
)

/** Pure validation for every trust boundary, kept Android-free so it is locally testable. */
internal object VideoRequestPolicy {

    fun normalizeInputMimeType(value: String?, displayName: String = ""): String? {
        val raw = value ?: return null
        if (raw != raw.trim() || raw != raw.lowercase(Locale.ROOT)) return null
        return if (PlaylistParser.format(displayName, raw) != null) {
            raw.takeIf { it == "*/*" || Regex("[a-z0-9][a-z0-9!#$&^_.+-]*/(?:[a-z0-9][a-z0-9!#$&^_.+-]*|\\*)").matches(it) }
        } else normalizeVideoMimeType(raw)
    }

    const val EXPLORER_EXECUTE_ACTION = "org.autojs.plugin.EXPLORER_ACTION_EXECUTE"
    const val EXTERNAL_VIEW_ACTION = "android.intent.action.VIEW"
    const val INTERNAL_PLAY_ACTION =
        "io.github.supermonster003.autojs6.plugin.threeemberplayer.action.PLAY_VALIDATED"
    const val SOURCE_SURFACE_MAIN = "main"
    const val PROTOCOL_VERSION = 12
    const val DISPLAY_NAME_EXTRA =
        "io.github.supermonster003.autojs6.plugin.threeemberplayer.extra.DISPLAY_NAME"
    const val DECLARED_SIZE_EXTRA =
        "io.github.supermonster003.autojs6.plugin.threeemberplayer.extra.DECLARED_SIZE"
    const val UNKNOWN_DECLARED_SIZE = -1L
    const val MAX_DISPLAY_NAME_LENGTH = 255
    private const val MAX_REQUEST_ID_LENGTH = 36
    private const val TARGET_KIND_FILE = 1

    private const val MAX_DECLARED_SIZE = 8L * 1024L * 1024L * 1024L * 1024L
    private val mimeTokenPattern = Regex("[a-z0-9][a-z0-9!#$&^_.+-]*")

    fun validateExplorer(envelope: ExplorerRequestEnvelope): ValidatedExplorerRequestGroup? {
        if (envelope.action != EXPLORER_EXECUTE_ACTION) return null
        val actionId = envelope.actionId ?: return null
        val multipleAction = when (actionId) {
            ThreeEmberPlayerPlugin.ACTION_ID -> false
            ThreeEmberPlayerPlugin.ACTION_SELECTION_ID -> true
            else -> return null
        }
        if (envelope.protocolVersion != PROTOCOL_VERSION) return null
        if (envelope.sourceSurface != SOURCE_SURFACE_MAIN) return null
        if ((envelope.hostVersionCode ?: return null) < ThreeEmberPlayerPlugin.REQUIRED_HOST_VERSION) return null
        if (!envelope.grants.isExplorerReadOnly()) return null
        if (canonicalRequestId(envelope.requestId) == null) return null
        if (validateParentDisplayPath(envelope.parentDisplayPath) == null) return null
        val parent = parsePlainContentUri(envelope.parentUri) ?: return null
        if (!ExplorerSelectionPolicy.isValidTargetCount(envelope.targets.size, multipleAction)) return null
        if (envelope.clipItems.size != envelope.targets.size) return null
        // The Host creates a multi-target session only when more than one item is selected.
        // A one-item invocation still belongs to the multiple-cardinality action, but can
        // safely use the exact URI grant without a session.
        if (multipleAction && envelope.targets.size > 1 && !envelope.hostSessionPresent) return null

        val validatedTargets = envelope.targets.mapIndexed { index, targetEnvelope ->
            if (targetEnvelope.targetKind != TARGET_KIND_FILE) return null
            val targetId = validateOpaqueId(targetEnvelope.targetId) ?: return null
            val target = parsePlainContentUri(targetEnvelope.targetUri) ?: return null
            if (!target.isDirectChildOf(parent)) return null
            if (!envelope.clipItems[index].isExactUri(target.original)) return null
            val displayName = validateExactDisplayName(targetEnvelope.displayName, target) ?: return null
            val declaredSize = targetEnvelope.declaredSize
                ?.takeIf { it in 0L..MAX_DECLARED_SIZE }
                ?: return null
            if ((targetEnvelope.lastModified ?: return null) < UNKNOWN_DECLARED_SIZE) return null
            val mimeType = normalizeInputMimeType(targetEnvelope.mimeType, displayName) ?: return null
            ValidatedExplorerRequest(
                targetId = targetId,
                targetUri = target.original,
                parentUri = parent.original,
                displayName = displayName,
                declaredSize = declaredSize,
                mimeType = mimeType,
            )
        }
        if (!ExplorerSelectionPolicy.hasUniqueNonBlankValues(validatedTargets.map { it.targetId })) return null
        if (!ExplorerSelectionPolicy.hasUniqueNonBlankValues(validatedTargets.map { it.targetUri })) return null
        if (!ExplorerSelectionPolicy.hasUniqueNonBlankValues(validatedTargets.map { it.displayName })) return null

        val primary = validatedTargets.first()
        if (envelope.dataUri != primary.targetUri) return null
        if (envelope.envelopeDisplayName != primary.displayName) return null
        if (envelope.envelopeDeclaredSize != primary.declaredSize) return null
        val validEnvelopeMime = if (multipleAction) {
            envelope.envelopeMimeType == MULTIPLE_TARGET_MIME_TYPE
        } else {
            normalizeInputMimeType(envelope.envelopeMimeType, primary.displayName) == primary.mimeType
        }
        if (!validEnvelopeMime) return null

        return ValidatedExplorerRequestGroup(
            actionId = actionId,
            parentUri = parent.original,
            targets = validatedTargets,
        )
    }

    fun validateExternal(envelope: ExternalRequestEnvelope): ValidatedExternalRequest? {
        if (envelope.action != EXTERNAL_VIEW_ACTION) return null
        if (!envelope.grants.isExactReadOnly()) return null
        val source = envelope.targetUri ?: return null
        val mimeType = normalizeInputMimeType(envelope.mimeType, source) ?: return null
        // SAF document IDs may contain encoded slashes; playlist documents are resolved through
        // their granted URI, never by treating those IDs as filesystem paths.
        val target = if (PlaylistParser.format(source, mimeType) != null) {
            PlaylistLocationPolicy.contentUri(source)
        } else parsePlainContentUri(source)?.original
        return ValidatedExternalRequest(target ?: return null, mimeType)
    }

    fun validateInternal(envelope: InternalRequestEnvelope): ValidatedPlaybackRequest? {
        if (envelope.action != INTERNAL_PLAY_ACTION) return null
        if (!envelope.grants.isExactReadOnly()) return null
        val target = parsePlainContentUri(envelope.targetUri) ?: return null
        if (envelope.clipItems.size != 1 || !envelope.clipItems[0].isExactUri(target.original)) {
            return null
        }
        if (envelope.extraKeys != setOf(DISPLAY_NAME_EXTRA, DECLARED_SIZE_EXTRA)) return null
        val displayName = validateSafeDisplayName(envelope.displayName) ?: return null
        val declaredSize = envelope.declaredSize?.takeIf { it >= UNKNOWN_DECLARED_SIZE } ?: return null
        val mimeType = normalizeVideoMimeType(envelope.mimeType) ?: return null
        return ValidatedPlaybackRequest(target.original, displayName, declaredSize, mimeType)
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

    private fun validateOpaqueId(value: String?): String? {
        val id = value ?: return null
        if (id.length !in 1..128) return null
        return id.takeIf { candidate ->
            candidate.none { it.isWhitespace() || isUnsafeUnicodeCharacter(it) }
        }
    }

    private fun canonicalRequestId(value: String?): String? {
        val requestId = value?.takeIf { it.length <= MAX_REQUEST_ID_LENGTH } ?: return null
        val parsed = runCatching { UUID.fromString(requestId) }.getOrNull() ?: return null
        return requestId.takeIf { parsed.toString().equals(requestId, ignoreCase = true) }
    }

    private fun validateParentDisplayPath(value: String?): String? {
        val path = value ?: return null
        if (path.length !in 1..16_384) return null
        return path.takeIf { candidate -> candidate.none(::isUnsafeUnicodeCharacter) }
    }

    private fun parsePlainContentUri(value: String?): ParsedContentUri? {
        return try {
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
    }

    private fun ParsedContentUri.isDirectChildOf(parent: ParsedContentUri): Boolean =
        authority == parent.authority &&
            segments.size == parent.segments.size + 1 &&
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

    private const val MULTIPLE_TARGET_MIME_TYPE = "*/*"
}

internal data class ForwardedPlaybackSpec(
    val action: String,
    val targetUri: String,
    val mimeType: String,
    val displayName: String,
    val declaredSize: Long,
    val clipUris: List<String>,
    val grantRead: Boolean,
    val grantWrite: Boolean,
    val grantPersistable: Boolean,
    val grantPrefix: Boolean,
    val extras: Map<String, Any>,
)

internal object PlaybackForwardingPolicy {

    fun fromExplorer(request: ValidatedExplorerRequest): ForwardedPlaybackSpec = build(
        targetUri = request.targetUri,
        mimeType = request.mimeType,
        displayName = request.displayName,
        declaredSize = request.declaredSize,
    )

    fun fromExternal(
        request: ValidatedExternalRequest,
        displayName: String,
        declaredSize: Long = VideoRequestPolicy.UNKNOWN_DECLARED_SIZE,
    ): ForwardedPlaybackSpec = build(
        targetUri = request.targetUri,
        mimeType = request.mimeType,
        displayName = VideoRequestPolicy.sanitizeExternalDisplayName(displayName, null),
        declaredSize = declaredSize.takeIf { it >= 0L } ?: VideoRequestPolicy.UNKNOWN_DECLARED_SIZE,
    )

    private fun build(
        targetUri: String,
        mimeType: String,
        displayName: String,
        declaredSize: Long,
    ) = ForwardedPlaybackSpec(
        action = VideoRequestPolicy.INTERNAL_PLAY_ACTION,
        targetUri = targetUri,
        mimeType = mimeType,
        displayName = displayName,
        declaredSize = declaredSize,
        clipUris = listOf(targetUri),
        grantRead = true,
        grantWrite = false,
        grantPersistable = false,
        grantPrefix = false,
        extras = mapOf(
            VideoRequestPolicy.DISPLAY_NAME_EXTRA to displayName,
            VideoRequestPolicy.DECLARED_SIZE_EXTRA to declaredSize,
        ),
    )
}
