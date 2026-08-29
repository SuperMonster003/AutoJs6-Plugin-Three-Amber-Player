package io.github.supermonster003.autojs6.plugin.threeemberplayer

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 * Turns raw touch input on the playback surface into semantic playback gestures.
 * All geometry-to-value math is delegated to [PlayerGesturePolicy].
 */
internal class PlayerGestureController(
    private val view: View,
    private val host: Host,
) : View.OnTouchListener {

    interface Host {
        /** Blocks every gesture, e.g. while the error panel is visible or no player exists. */
        fun isGestureBlocked(): Boolean

        /** Locked mode still delivers single taps so the unlock affordance can be revealed. */
        fun isLocked(): Boolean

        fun onLockedSingleTap()

        fun onSingleTap()

        fun onDoubleTap(zone: DoubleTapZone)

        /** Returns false when seeking is impossible, e.g. while the duration is unknown. */
        fun onSeekDragStart(): Boolean

        fun onSeekDragUpdate(totalDragPx: Float, viewWidthPx: Int)

        fun onSeekDragEnd(commit: Boolean)

        fun onVerticalDragStart(zone: VerticalDragZone)

        fun onVerticalDragUpdate(zone: VerticalDragZone, totalDragUpPx: Float, viewHeightPx: Int)

        fun onVerticalDragEnd()

        /** Returns false when the temporary speed boost is unavailable, e.g. while paused. */
        fun onLongPressStart(): Boolean

        fun onLongPressEnd()

        /** Returns false when pinch zoom is unavailable for the current state. */
        fun onScaleStart(): Boolean

        fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)

        fun onScaleEnd()
    }

    private enum class DragMode { NONE, SEEK, VERTICAL }

    private val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
    private var dragMode = DragMode.NONE
    private var verticalZone = VerticalDragZone.BRIGHTNESS
    private var longPressActive = false
    private var scaleActive = false

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean = true

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            view.performClick()
            if (host.isLocked()) host.onLockedSingleTap() else host.onSingleTap()
            return true
        }

        override fun onDoubleTap(event: MotionEvent): Boolean {
            if (host.isLocked() || host.isGestureBlocked()) return true
            host.onDoubleTap(PlayerGesturePolicy.doubleTapZone(event.x, view.width))
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            if (host.isLocked() || host.isGestureBlocked() || dragMode != DragMode.NONE) return
            longPressActive = host.onLongPressStart()
        }

        override fun onScroll(
            downEvent: MotionEvent?,
            event: MotionEvent,
            distanceX: Float,
            distanceY: Float,
        ): Boolean {
            val down = downEvent ?: return false
            if (host.isLocked() || host.isGestureBlocked() || longPressActive ||
                scaleActive || event.pointerCount > 1
            ) {
                return false
            }
            val totalX = event.x - down.x
            val totalY = event.y - down.y
            if (dragMode == DragMode.NONE) {
                if (abs(totalX) < touchSlop && abs(totalY) < touchSlop) return false
                dragMode = if (abs(totalX) >= abs(totalY)) {
                    if (!host.onSeekDragStart()) return false
                    DragMode.SEEK
                } else {
                    verticalZone = PlayerGesturePolicy.verticalDragZone(down.x, view.width)
                    host.onVerticalDragStart(verticalZone)
                    DragMode.VERTICAL
                }
            }
            when (dragMode) {
                DragMode.SEEK -> host.onSeekDragUpdate(totalX, view.width)
                DragMode.VERTICAL -> host.onVerticalDragUpdate(verticalZone, -totalY, view.height)
                DragMode.NONE -> Unit
            }
            return true
        }
    }

    private val detector = GestureDetector(view.context, gestureListener)
    private val scaleDetector = ScaleGestureDetector(
        view.context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                if (host.isLocked() || host.isGestureBlocked()) return false
                finishDrag(commit = false)
                if (longPressActive) {
                    longPressActive = false
                    host.onLongPressEnd()
                }
                scaleActive = host.onScaleStart()
                return scaleActive
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (!scaleActive) return false
                host.onScale(detector.scaleFactor, detector.focusX, detector.focusY)
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                if (!scaleActive) return
                scaleActive = false
                host.onScaleEnd()
            }
        },
    )

    init {
        @SuppressLint("ClickableViewAccessibility")
        view.setOnTouchListener(this)
    }

    override fun onTouch(touchedView: View, event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        detector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_UP -> finishGesture(commit = true)
            MotionEvent.ACTION_CANCEL -> finishGesture(commit = false)
        }
        return true
    }

    private fun finishGesture(commit: Boolean) {
        finishDrag(commit)
        if (longPressActive) {
            longPressActive = false
            host.onLongPressEnd()
        }
    }

    private fun finishDrag(commit: Boolean) {
        when (dragMode) {
            DragMode.SEEK -> host.onSeekDragEnd(commit)
            DragMode.VERTICAL -> host.onVerticalDragEnd()
            DragMode.NONE -> Unit
        }
        dragMode = DragMode.NONE
    }
}
