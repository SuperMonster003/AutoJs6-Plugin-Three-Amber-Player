package io.github.supermonster003.autojs6.plugin.videoplayer

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import java.util.concurrent.Executors

internal class ScrubThumbnailProvider(
    private val context: Context,
    private val uri: Uri,
    private val targetWidth: Int,
    private val targetHeight: Int,
    private val descriptorOpener: (() -> ParcelFileDescriptor)? = null,
) : AutoCloseable {

    private data class Request(
        val positionMs: Long,
        val callback: (Long, Bitmap?) -> Unit,
    )

    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val lock = Any()
    private var retriever: MediaMetadataRetriever? = null
    private var retrieverDescriptor: ParcelFileDescriptor? = null
    private var latestRequest: Request? = null
    private var workerRunning = false
    private var disabled = false
    private var closed = false

    fun request(positionMs: Long, callback: (Long, Bitmap?) -> Unit) {
        synchronized(lock) {
            if (closed || disabled) return
            latestRequest = Request(positionMs.coerceAtLeast(0L), callback)
            if (workerRunning) return
            workerRunning = true
        }
        executor.execute(::drainRequests)
    }

    private fun drainRequests() {
        while (true) {
            val request = synchronized(lock) {
                latestRequest.also { latestRequest = null }
                    ?: run {
                        workerRunning = false
                        return
                    }
            }
            val bitmap = runCatching { extractFrame(request.positionMs) }
                .onFailure {
                    synchronized(lock) {
                        disabled = true
                        latestRequest = null
                    }
                }
                .getOrNull()
            mainHandler.post {
                val shouldDeliver = synchronized(lock) {
                    !closed
                }
                if (shouldDeliver) {
                    request.callback(request.positionMs, bitmap)
                } else {
                    bitmap?.recycle()
                }
            }
        }
    }

    private fun extractFrame(positionMs: Long): Bitmap? {
        val mediaRetriever = retriever ?: MediaMetadataRetriever().also {
            val descriptor = descriptorOpener?.invoke()
            if (descriptor == null) {
                it.setDataSource(context, uri)
            } else {
                retrieverDescriptor = descriptor
                it.setDataSource(descriptor.fileDescriptor)
            }
            retriever = it
        }
        val timeUs = positionMs.coerceAtLeast(0L) * 1_000L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return mediaRetriever.getScaledFrameAtTime(
                timeUs,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                targetWidth,
                targetHeight,
            )
        }
        val original = mediaRetriever.getFrameAtTime(
            timeUs,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
        ) ?: return null
        val scaled = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true)
        if (scaled !== original) original.recycle()
        return scaled
    }

    override fun close() {
        synchronized(lock) {
            if (closed) return
            closed = true
            latestRequest = null
        }
        runCatching {
            executor.execute {
                retriever?.release()
                retriever = null
                retrieverDescriptor?.close()
                retrieverDescriptor = null
            }
        }
        executor.shutdown()
    }
}
