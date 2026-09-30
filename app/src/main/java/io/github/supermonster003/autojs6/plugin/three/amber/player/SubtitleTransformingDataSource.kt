@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

internal data class SubtitleSourceRoute(
    val virtualUri: Uri,
    val sourceUri: Uri,
    val mimeType: String,
    val offsetMs: Long,
    val stableId: String,
)

/** Request-scoped registry: routes exist only while the private playback activity exists. */
internal class SubtitleSourceRegistry {
    private val routes = ConcurrentHashMap<String, SubtitleSourceRoute>()

    fun replace(values: Collection<SubtitleSourceRoute>) {
        routes.clear()
        values.forEach { route -> routes[route.virtualUri.toString()] = route }
    }

    fun resolve(uri: Uri): SubtitleSourceRoute? = routes[uri.toString()]

    fun clear() = routes.clear()
}

/**
 * Delegates ordinary media unchanged. Registered subtitle routes are bounded-read, decoded,
 * shifted, re-encoded to UTF-8, and served from memory without a temporary file.
 */
internal class SubtitleTransformingDataSource private constructor(
    private val upstreamFactory: DataSource.Factory,
    private val registry: SubtitleSourceRegistry,
    private val onUncertainEncoding: (SubtitleSourceRoute) -> Unit,
) : BaseDataSource(false) {

    private var delegate: DataSource? = null
    private var memory: ByteArray? = null
    private var memoryPosition = 0
    private var memoryLimit = 0
    private var openedUri: Uri? = null
    private var transferOpen = false

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        check(delegate == null && memory == null) { "Subtitle data source is already open" }
        transferInitializing(dataSpec)
        openedUri = dataSpec.uri
        val route = registry.resolve(dataSpec.uri)
        val length = if (route == null) {
            upstreamFactory.createDataSource().also { delegate = it }.open(dataSpec)
        } else {
            openTransformed(dataSpec, route)
        }
        transferStarted(dataSpec)
        transferOpen = true
        return length
    }

    private fun openTransformed(dataSpec: DataSpec, route: SubtitleSourceRoute): Long {
        val raw = readBounded(route.sourceUri)
        val processed = SubtitleTextProcessor.process(raw, route.mimeType, route.offsetMs)
        if (!processed.encodingConfident) onUncertainEncoding(route)
        val bytes = processed.utf8Bytes
        if (dataSpec.position > bytes.size) throw EOFException("Read position exceeds subtitle size")
        val start = dataSpec.position.toInt()
        val requested = if (dataSpec.length == C.LENGTH_UNSET.toLong()) {
            bytes.size - start
        } else {
            dataSpec.length.coerceAtMost((bytes.size - start).toLong()).toInt()
        }
        memory = bytes
        memoryPosition = start
        memoryLimit = start + requested
        return requested.toLong()
    }

    private fun readBounded(uri: Uri): ByteArray {
        val source = upstreamFactory.createDataSource()
        return try {
            val declaredLength = source.open(
                DataSpec.Builder()
                    .setUri(uri)
                    .setPosition(0L)
                    .setLength(C.LENGTH_UNSET.toLong())
                    .build(),
            )
            if (declaredLength > SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES) {
                throw IOException("Subtitle exceeds the in-memory size limit")
            }
            val output = ByteArrayOutputStream(
                declaredLength.takeIf { it in 1..SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES }
                    ?.toInt()
                    ?: DEFAULT_BUFFER_CAPACITY,
            )
            val buffer = ByteArray(READ_BUFFER_SIZE)
            while (true) {
                val read = source.read(buffer, 0, buffer.size)
                if (read == C.RESULT_END_OF_INPUT) break
                if (read <= 0) continue
                if (output.size().toLong() + read > SubtitleDocumentPolicy.MAX_SUBTITLE_BYTES) {
                    throw IOException("Subtitle exceeds the in-memory size limit")
                }
                output.write(buffer, 0, read)
            }
            output.toByteArray()
        } finally {
            source.close()
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        val bytes = memory
        val read = if (bytes == null) {
            delegate?.read(buffer, offset, length) ?: C.RESULT_END_OF_INPUT
        } else {
            if (memoryPosition >= memoryLimit) return C.RESULT_END_OF_INPUT
            val count = length.coerceAtMost(memoryLimit - memoryPosition)
            bytes.copyInto(buffer, offset, memoryPosition, memoryPosition + count)
            memoryPosition += count
            count
        }
        if (read > 0) bytesTransferred(read)
        return read
    }

    override fun getUri(): Uri? = openedUri

    override fun close() {
        try {
            delegate?.close()
        } finally {
            delegate = null
            memory = null
            memoryPosition = 0
            memoryLimit = 0
            openedUri = null
            if (transferOpen) {
                transferOpen = false
                transferEnded()
            }
        }
    }

    class Factory(
        private val upstreamFactory: DataSource.Factory,
        private val registry: SubtitleSourceRegistry,
        private val onUncertainEncoding: (SubtitleSourceRoute) -> Unit,
    ) : DataSource.Factory {
        override fun createDataSource(): DataSource = SubtitleTransformingDataSource(
            upstreamFactory,
            registry,
            onUncertainEncoding,
        )
    }

    private companion object {
        const val READ_BUFFER_SIZE = 16 * 1024
        const val DEFAULT_BUFFER_CAPACITY = 32 * 1024
    }
}
