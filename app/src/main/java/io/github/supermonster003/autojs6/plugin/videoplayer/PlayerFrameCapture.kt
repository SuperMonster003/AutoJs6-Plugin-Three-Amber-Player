package io.github.supermonster003.autojs6.plugin.videoplayer

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

internal sealed interface FrameCaptureResult {
    data class Success(val uri: Uri) : FrameCaptureResult
    data object VideoNotReady : FrameCaptureResult
    data object CopyFailed : FrameCaptureResult
    data object SaveFailed : FrameCaptureResult
}

@RequiresApi(29)
@OptIn(UnstableApi::class)
internal class PlayerFrameCapture(private val activity: Activity) : AutoCloseable {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val saveExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var closed = false

    fun capture(playerView: PlayerView, callback: (FrameCaptureResult) -> Unit) {
        if (closed) return
        val videoSurface = playerView.videoSurfaceView
        if (videoSurface == null || videoSurface.width <= 0 || videoSurface.height <= 0) {
            callback(FrameCaptureResult.VideoNotReady)
            return
        }
        when (videoSurface) {
            is SurfaceView -> captureSurfaceView(videoSurface, callback)
            is TextureView -> {
                val bitmap = runCatching {
                    videoSurface.getBitmap(videoSurface.width, videoSurface.height)
                }.getOrNull()
                if (bitmap == null) {
                    callback(FrameCaptureResult.CopyFailed)
                } else {
                    save(bitmap, callback)
                }
            }
            else -> callback(FrameCaptureResult.CopyFailed)
        }
    }

    private fun captureSurfaceView(
        surfaceView: SurfaceView,
        callback: (FrameCaptureResult) -> Unit,
    ) {
        if (!surfaceView.isAttachedToWindow || !surfaceView.holder.surface.isValid) {
            callback(FrameCaptureResult.VideoNotReady)
            return
        }
        val bitmap = runCatching {
            Bitmap.createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
        }.getOrElse {
            callback(FrameCaptureResult.CopyFailed)
            return
        }
        runCatching {
            PixelCopy.request(surfaceView, bitmap, { result ->
                if (result == PixelCopy.SUCCESS) {
                    save(bitmap, callback)
                } else {
                    bitmap.recycle()
                    dispatch(callback, FrameCaptureResult.CopyFailed)
                }
            }, mainHandler)
        }.onFailure {
            bitmap.recycle()
            callback(FrameCaptureResult.CopyFailed)
        }
    }

    private fun save(bitmap: Bitmap, callback: (FrameCaptureResult) -> Unit) {
        runCatching {
            saveExecutor.execute {
                val result = saveToMediaStore(bitmap)
                bitmap.recycle()
                dispatch(callback, result)
            }
        }.onFailure {
            bitmap.recycle()
            callback(FrameCaptureResult.SaveFailed)
        }
    }

    private fun saveToMediaStore(bitmap: Bitmap): FrameCaptureResult {
        val resolver = activity.contentResolver
        var outputUri: Uri? = null
        return try {
            val timestamp = SimpleDateFormat(FILE_TIMESTAMP_PATTERN, Locale.ROOT).format(Date())
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "video_frame_$timestamp.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/$ALBUM_NAME",
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            outputUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return FrameCaptureResult.SaveFailed
            val saved = resolver.openOutputStream(outputUri)?.use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, output)
            } == true
            if (!saved) throw IllegalStateException("Unable to encode frame")
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(outputUri, values, null, null)
            FrameCaptureResult.Success(outputUri)
        } catch (_: Exception) {
            outputUri?.let { runCatching { resolver.delete(it, null, null) } }
            FrameCaptureResult.SaveFailed
        }
    }

    private fun dispatch(callback: (FrameCaptureResult) -> Unit, result: FrameCaptureResult) {
        mainHandler.post {
            if (!closed) callback(result)
        }
    }

    override fun close() {
        closed = true
        saveExecutor.shutdown()
    }

    private companion object {
        const val ALBUM_NAME = "AutoJs6 Video Player"
        const val FILE_TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss_SSS"
        const val PNG_QUALITY = 100
    }
}
