@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import org.autojs.plugin.explorer.api.IExplorerActionHostSession
import java.io.EOFException
import java.io.IOException

/** Read-only Media3 bridge for opaque direct-sibling routes backed by one Host Session. */
internal class ExplorerSessionDataSource private constructor(
    private val session: IExplorerActionHostSession,
    private val routesByUri: Map<String, HostFileRoute>,
) : BaseDataSource(false) {

    private var openedUri: Uri? = null
    private var input: ParcelFileDescriptor.AutoCloseInputStream? = null
    private var bytesRemaining = C.LENGTH_UNSET.toLong()
    private var transferOpen = false

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        check(input == null) { "Explorer session data source is already open" }
        transferInitializing(dataSpec)
        val route = routesByUri[dataSpec.uri.toString()]
            ?: throw IOException("Explorer session media route is unknown")
        val descriptor = try {
            session.openFile(route.targetId, route.relativePath)
        } catch (error: Exception) {
            throw IOException("Explorer session could not open media", error)
        }
        try {
            val stream = ParcelFileDescriptor.AutoCloseInputStream(descriptor)
            val size = descriptor.statSize
            if (size >= 0L && dataSpec.position > size) throw EOFException("Read position exceeds media size")
            stream.channel.position(dataSpec.position)
            input = stream
            openedUri = dataSpec.uri
            bytesRemaining = when {
                dataSpec.length != C.LENGTH_UNSET.toLong() -> dataSpec.length
                size >= 0L -> (size - dataSpec.position).coerceAtLeast(0L)
                else -> C.LENGTH_UNSET.toLong()
            }
            transferStarted(dataSpec)
            transferOpen = true
            return bytesRemaining
        } catch (error: Throwable) {
            runCatching { descriptor.close() }
            throw error
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (bytesRemaining == 0L) return C.RESULT_END_OF_INPUT
        val readLength = if (bytesRemaining == C.LENGTH_UNSET.toLong()) {
            length
        } else {
            length.coerceAtMost(bytesRemaining.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        }
        val bytesRead = input?.read(buffer, offset, readLength) ?: C.RESULT_END_OF_INPUT
        if (bytesRead < 0) return C.RESULT_END_OF_INPUT
        if (bytesRemaining != C.LENGTH_UNSET.toLong()) bytesRemaining -= bytesRead
        bytesTransferred(bytesRead)
        return bytesRead
    }

    override fun getUri(): Uri? = openedUri

    override fun close() {
        try {
            input?.close()
        } finally {
            input = null
            openedUri = null
            bytesRemaining = C.LENGTH_UNSET.toLong()
            if (transferOpen) {
                transferOpen = false
                transferEnded()
            }
        }
    }

    class Factory(
        private val session: IExplorerActionHostSession,
        private val routesByUri: Map<String, HostFileRoute>,
    ) : DataSource.Factory {

        private var transferListener: TransferListener? = null

        override fun createDataSource(): DataSource = ExplorerSessionDataSource(
            session,
            routesByUri,
        ).also { dataSource -> transferListener?.let(dataSource::addTransferListener) }

        fun setTransferListener(listener: TransferListener?): Factory = apply {
            transferListener = listener
        }
    }
}

internal data class HostFileRoute(
    val targetId: String,
    val relativePath: String,
)

internal fun AndroidPlaybackRequest.hostFileRoutesByUri(): Map<String, HostFileRoute> = buildMap {
    items.forEach { item ->
        val targetId = item.hostTargetId ?: return@forEach
        if (item.sourceUri.scheme == VideoIntentFactory.HOST_SOURCE_SCHEME) {
            put(
                item.sourceUri.toString(),
                HostFileRoute(targetId, requireNotNull(item.relativePath)),
            )
        }
        item.subtitles.forEach { subtitle ->
            put(subtitle.sourceUri.toString(), HostFileRoute(targetId, subtitle.relativePath))
        }
    }
}
