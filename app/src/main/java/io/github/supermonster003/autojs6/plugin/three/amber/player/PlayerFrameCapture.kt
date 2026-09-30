package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import androidx.core.graphics.createBitmap
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.abs

internal data class FrameCaptureOptions(
    val includeSubtitles: Boolean,
    val renderTransform: VideoRenderTransform,
)

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

    fun capture(
        playerView: PlayerView,
        videoSurface: View? = playerView.videoSurfaceView,
        options: FrameCaptureOptions,
        callback: (FrameCaptureResult) -> Unit,
    ) {
        if (closed) return
        if (videoSurface == null || videoSurface.width <= 0 || videoSurface.height <= 0) {
            callback(FrameCaptureResult.VideoNotReady)
            return
        }
        when (videoSurface) {
            is SurfaceView -> captureSurfaceView(playerView, videoSurface, options, callback)
            is TextureView -> {
                val bitmap = runCatching {
                    videoSurface.getBitmap(videoSurface.width, videoSurface.height)
                }.getOrNull()
                if (bitmap == null) {
                    callback(FrameCaptureResult.CopyFailed)
                } else {
                    finishCopy(playerView, videoSurface, bitmap, options, callback)
                }
            }
            else -> callback(FrameCaptureResult.CopyFailed)
        }
    }

    private fun captureSurfaceView(
        playerView: PlayerView,
        surfaceView: SurfaceView,
        options: FrameCaptureOptions,
        callback: (FrameCaptureResult) -> Unit,
    ) {
        if (!surfaceView.isAttachedToWindow || !surfaceView.holder.surface.isValid) {
            callback(FrameCaptureResult.VideoNotReady)
            return
        }
        val bitmap = runCatching {
            createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
        }.getOrElse {
            callback(FrameCaptureResult.CopyFailed)
            return
        }
        runCatching {
            PixelCopy.request(surfaceView, bitmap, { result ->
                if (result == PixelCopy.SUCCESS) {
                    finishCopy(playerView, surfaceView, bitmap, options, callback)
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

    private fun finishCopy(
        playerView: PlayerView,
        videoSurface: View,
        copiedBitmap: Bitmap,
        options: FrameCaptureOptions,
        callback: (FrameCaptureResult) -> Unit,
    ) {
        var transformedBitmap: Bitmap? = null
        val output = runCatching {
            val transformed = applyRenderTransform(copiedBitmap, options.renderTransform)
                .also { transformedBitmap = it }
            if (options.includeSubtitles) {
                // A mirrored TextureView reports its transformed origin. Use PlayerView's
                // untransformed surface slot as the coordinate anchor so subtitles remain in
                // their normal screen position in the composed output.
                drawSubtitles(
                    playerView,
                    playerView.videoSurfaceView ?: videoSurface,
                    transformed,
                )
            }
            transformed
        }.getOrElse {
            transformedBitmap
                ?.takeIf { bitmap -> bitmap !== copiedBitmap && !bitmap.isRecycled }
                ?.recycle()
            if (!copiedBitmap.isRecycled) copiedBitmap.recycle()
            callback(FrameCaptureResult.CopyFailed)
            return
        }
        save(output, callback)
    }

    private fun applyRenderTransform(
        source: Bitmap,
        transform: VideoRenderTransform,
    ): Bitmap {
        if (abs(transform.scaleX - 1f) < TRANSFORM_EPSILON &&
            abs(transform.scaleY - 1f) < TRANSFORM_EPSILON
        ) {
            return source
        }
        val output = createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        try {
            Canvas(output).apply {
                drawColor(Color.BLACK)
                val pivotX = output.width * transform.pivotXFraction
                val pivotY = output.height * transform.pivotYFraction
                translate(pivotX, pivotY)
                scale(transform.scaleX, transform.scaleY)
                translate(-pivotX, -pivotY)
                drawBitmap(source, 0f, 0f, null)
            }
        } catch (error: Throwable) {
            output.recycle()
            throw error
        }
        source.recycle()
        return output
    }

    private fun drawSubtitles(playerView: PlayerView, videoSurface: View, bitmap: Bitmap) {
        val subtitleView = playerView.subtitleView ?: return
        if (!subtitleView.isShown || subtitleView.width <= 0 || subtitleView.height <= 0) return
        val surfaceLocation = IntArray(2)
        val subtitleLocation = IntArray(2)
        videoSurface.getLocationOnScreen(surfaceLocation)
        subtitleView.getLocationOnScreen(subtitleLocation)
        val scaleX = bitmap.width.toFloat() / videoSurface.width.coerceAtLeast(1)
        val scaleY = bitmap.height.toFloat() / videoSurface.height.coerceAtLeast(1)
        Canvas(bitmap).apply {
            val saveCount = save()
            try {
                scale(scaleX, scaleY)
                translate(
                    (subtitleLocation[0] - surfaceLocation[0]).toFloat(),
                    (subtitleLocation[1] - surfaceLocation[1]).toFloat(),
                )
                subtitleView.draw(this)
            } finally {
                restoreToCount(saveCount)
            }
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
        const val ALBUM_NAME = "3-Amber Player"
        const val FILE_TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss_SSS"
        const val PNG_QUALITY = 100
        const val TRANSFORM_EPSILON = 0.0001f
    }
}
