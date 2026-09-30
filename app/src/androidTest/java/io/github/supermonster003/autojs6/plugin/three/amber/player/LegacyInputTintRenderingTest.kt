package io.github.supermonster003.autojs6.plugin.three.amber.player

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.textfield.TextInputEditText
import io.github.supermonster003.autojs6.plugin.three.amber.player.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.three.amber.player.theme.*
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/** Opt-in evidence of real framework cursor/PopupWindow handle drawing, without reflection. */
class LegacyInputTintRenderingTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun frameworkCursorAndSelectionHandlesRenderTheRequestedAccent() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("recordInputTint") == "true")
        AutoJs6AppearanceClient.query(context)
        val deadline = SystemClock.uptimeMillis() + 10_000
        while (!AutoJs6AppearanceClient.hasSnapshot && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(10)
        assertTrue(AutoJs6AppearanceClient.hasSnapshot)
        val activity = instrumentation.startActivitySync(Intent(context, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as SettingsActivity
        val palette = VideoThemePaletteGenerator.generate(0xFFFF00FF.toInt(), false)
        val evidence = File(context.cacheDir, "input-tint-evidence").apply { mkdirs() }
        var dialog: AlertDialog? = null
        lateinit var input: TextInputEditText
        try {
            instrumentation.runOnMainSync {
                activity.onUserInteraction()
                val shown = ThemeColorChooser.show(activity, null, 0xFFFFDEAD.toInt(),
                    ThemeColorChooser.Palette(palette.primary, palette.surface, palette.onSurface, palette.onSurfaceVariant, palette.outline),
                    ThemeColorChooser.Labels("Theme", "AutoJs6", "Presets", "Custom", "HEX / RGB", "Invalid", "Preview"),
                ) { error("A rendering audit must never confirm a preference") }
                dialog = shown
                activity.trackAppearanceDialog(shown)
                input = shown.window!!.decorView.findViewWithTag("theme-color-input")
                input.setOnTouchListener { _, event ->
                    File(evidence, "touches.txt").appendText("action=${event.action} raw=${event.rawX},${event.rawY} local=${event.x},${event.y} focus=${input.hasFocus()}\n")
                    false
                }
                input.setText("rgb(255, 128, 240)")
                input.requestFocus()
                input.setSelection(7)
                (activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager)
                    .showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                (activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager)
                    .showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
            waitForStableInput(input)
            capture(evidence, "ime-visible.png").recycle()
            instrumentation.runOnMainSync {
                val visible = Rect()
                val isVisible = input.getGlobalVisibleRect(visible)
                val text = StringBuilder("inputVisible=$isVisible rect=$visible lineHeight=${input.lineHeight} window=${dialog!!.window!!.attributes}\n")
                var view: android.view.View? = input
                while (view != null) {
                    text.append(view.javaClass.simpleName).append(" height=").append(view.height).append(" scrollY=").append(view.scrollY).append('\n')
                    view = view.parent as? android.view.View
                }
                File(evidence, "ime-geometry.txt").writeText(text.toString())
                assertTrue("Input must remain visibly editable above the keyboard", isVisible && visible.height() >= input.lineHeight)
            }
            var caretPixels = 0
            repeat(6) {
                if (caretPixels > 8) return@repeat
                instrumentation.runOnMainSync { input.isCursorVisible = false; input.isCursorVisible = true; input.setSelection(7) }
                SystemClock.sleep(70)
                val bitmap = capture(evidence, "caret.png")
                var bounds = Rect()
                instrumentation.runOnMainSync { bounds = cursorBounds(input, 7, handles = false) }
                caretPixels = count(bitmap, bounds, palette.primary)
                bitmap.recycle()
            }
            assertTrue("Real caret did not use accent ${palette.primary.toUInt().toString(16)}: $caretPixels pixels", caretPixels > 8)
            val withoutHandles = capture(evidence, "baseline-before-handles.png")

            // A real pointer tap activates the insertion handle; all coordinates come from layout.
            waitForStableInput(input)
            var tap = Rect()
            instrumentation.runOnMainSync { tap = cursorBounds(input, 7, handles = false) }
            File(evidence, "touches.txt").appendText("tap=$tap\n")
            val now = SystemClock.uptimeMillis()
            pointer(now, now, MotionEvent.ACTION_DOWN, tap.centerX().toFloat(), tap.centerY().toFloat())
            SystemClock.sleep(50)
            pointer(now, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, tap.centerX().toFloat(), tap.centerY().toFloat())
            SystemClock.sleep(220)
            var centerBounds = Rect()
            instrumentation.runOnMainSync { centerBounds = cursorBounds(input, input.selectionStart, handles = true) }
            val center = capture(evidence, "insertion-handle.png")
            val centerPixels = count(center, centerBounds, palette.primary, withoutHandles)
            center.recycle()

            waitForStableInput(input)
            instrumentation.runOnMainSync { tap = cursorBounds(input, 7, handles = false) }
            val longPress = SystemClock.uptimeMillis()
            pointer(longPress, longPress, MotionEvent.ACTION_DOWN, tap.centerX().toFloat(), tap.centerY().toFloat())
            SystemClock.sleep(android.view.ViewConfiguration.getLongPressTimeout().toLong() + 200)
            pointer(longPress, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, tap.centerX().toFloat(), tap.centerY().toFloat())
            instrumentation.runOnMainSync { input.setSelection(4, 17) }
            SystemClock.sleep(300)
            var leftBounds = Rect()
            var rightBounds = Rect()
            instrumentation.runOnMainSync {
                leftBounds = cursorBounds(input, input.selectionStart, handles = true)
                rightBounds = cursorBounds(input, input.selectionEnd, handles = true)
                val visible = Rect().also { dialog!!.window!!.decorView.getWindowVisibleDisplayFrame(it) }
                val activityVisible = Rect().also { activity.window.decorView.getWindowVisibleDisplayFrame(it) }
                val details = StringBuilder("dialogVisible=$visible\nactivityVisible=$activityVisible\nmetrics=${activity.resources.displayMetrics}\nwindow=${dialog!!.window!!.attributes}\n")
                fun describe(view: android.view.View, level: Int) {
                    details.append(" ".repeat(level)).append(view.javaClass.simpleName).append(" ")
                        .append(view.width).append("x").append(view.height).append(" lp=")
                        .append(view.layoutParams?.height).append(" y=").append(view.y).append('\n')
                    if (view is android.view.ViewGroup) for (i in 0 until view.childCount) describe(view.getChildAt(i), level + 1)
                }
                describe(dialog!!.window!!.decorView, 0)
                File(evidence, "geometry.txt").writeText(details.toString())
            }
            val selection = capture(evidence, "selection-handles.png")
            val leftPixels = count(selection, leftBounds, palette.primary, withoutHandles)
            val rightPixels = count(selection, rightBounds, palette.primary, withoutHandles)
            selection.recycle()
            withoutHandles.recycle()
            File(evidence, "result.txt").writeText("api=${android.os.Build.VERSION.SDK_INT}\naccent=${palette.primary.toUInt().toString(16)}\ncaret=$caretPixels\ncenter=$centerPixels\nleft=$leftPixels\nright=$rightPixels\ncenterBounds=$centerBounds\nleftBounds=$leftBounds\nrightBounds=$rightBounds\n")
            assertTrue("Insertion handle did not use the accent: $centerPixels pixels", centerPixels > 15)
            assertTrue("Selection left handle did not use the accent: $leftPixels pixels", leftPixels > 15)
            assertTrue("Selection right handle did not use the accent: $rightPixels pixels", rightPixels > 15)
        } finally {
            instrumentation.runOnMainSync { dialog?.dismiss(); activity.finish() }
        }
    }

    private fun pointer(down: Long, time: Long, action: Int, x: Float, y: Float) {
        val event = MotionEvent.obtain(down, time, action, x, y, 0)
        event.source = android.view.InputDevice.SOURCE_TOUCHSCREEN
        try { instrumentation.sendPointerSync(event) } finally { event.recycle() }
    }

    private fun waitForStableInput(input: TextInputEditText) {
        var previous = Rect()
        var stable = 0
        val deadline = SystemClock.uptimeMillis() + 5_000
        while (stable < 4 && SystemClock.uptimeMillis() < deadline) {
            instrumentation.waitForIdleSync()
            val bounds = Rect()
            var visible = false
            instrumentation.runOnMainSync {
                visible = input.isAttachedToWindow && input.getGlobalVisibleRect(bounds) && bounds.height() >= input.lineHeight
                val location = IntArray(2).also(input::getLocationOnScreen)
                bounds.set(location[0], location[1], location[0] + input.width, location[1] + input.height)
            }
            stable = if (visible && bounds == previous) stable + 1 else 0
            previous = bounds
            SystemClock.sleep(100)
        }
        assertTrue("Input window must finish resizing before injecting a real touch", stable >= 4)
    }

    private fun cursorBounds(input: TextInputEditText, offset: Int, handles: Boolean): Rect {
        val location = IntArray(2).also(input::getLocationOnScreen)
        val layout = input.layout
        val density = input.resources.displayMetrics.density
        val x = location[0] + input.totalPaddingLeft + layout.getPrimaryHorizontal(offset).toInt() - input.scrollX
        val top = location[1] + input.totalPaddingTop + layout.getLineTop(0) - input.scrollY
        val bottom = location[1] + input.totalPaddingTop + layout.getLineBottom(0) - input.scrollY
        return if (handles) Rect(x - (24 * density).toInt(), bottom + (3 * density).toInt(), x + (24 * density).toInt(), bottom + (58 * density).toInt())
        else Rect(x - (5 * density).toInt(), top, x + (5 * density).toInt(), bottom)
    }

    private fun capture(directory: File, name: String): Bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot()).also { bitmap ->
        File(directory, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun count(bitmap: Bitmap, bounds: Rect, color: Int, baseline: Bitmap? = null): Int {
        var count = 0
        for (y in bounds.top.coerceAtLeast(0) until bounds.bottom.coerceAtMost(bitmap.height)) {
            for (x in bounds.left.coerceAtLeast(0) until bounds.right.coerceAtMost(bitmap.width)) {
                // Ignore unchanged field borders: only genuinely new handle pixels count.
                if ((bitmap.getPixel(x, y) and 0xffffff) == (color and 0xffffff) &&
                    (baseline == null || (baseline.getPixel(x, y) and 0xffffff) != (color and 0xffffff))) count++
            }
        }
        return count
    }
}
