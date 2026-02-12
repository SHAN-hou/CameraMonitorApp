package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class FocusPeakingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var peakingEnabled: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var sourceImageView: ImageView? = null

    private val peakPaint = Paint().apply {
        color = Color.RED
        alpha = 200
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!peakingEnabled) return

        val imgView = sourceImageView ?: return
        val drawable = imgView.drawable ?: return
        val bitmap = if (drawable is BitmapDrawable) drawable.bitmap else return
        if (bitmap.isRecycled) return

        drawFocusPeaking(canvas, bitmap)
    }

    private fun drawFocusPeaking(canvas: Canvas, sourceBitmap: Bitmap) {
        val w = width
        val h = height
        if (w <= 0 || h <= 0) return

        val sampleW = minOf(w / 6, 320)
        val sampleH = minOf(h / 6, 180)
        val scaled = Bitmap.createScaledBitmap(sourceBitmap, sampleW, sampleH, true)

        val scaleX = w.toFloat() / sampleW
        val scaleY = h.toFloat() / sampleH
        val edgeThreshold = 60

        for (sy in 1 until sampleH - 1) {
            for (sx in 1 until sampleW - 1) {
                val center = luminance(scaled.getPixel(sx, sy))
                val left = luminance(scaled.getPixel(sx - 1, sy))
                val right = luminance(scaled.getPixel(sx + 1, sy))
                val top = luminance(scaled.getPixel(sx, sy - 1))
                val bottom = luminance(scaled.getPixel(sx, sy + 1))

                val edgeStrength = Math.abs(center - left) + Math.abs(center - right) +
                        Math.abs(center - top) + Math.abs(center - bottom)

                if (edgeStrength > edgeThreshold) {
                    val cx = sx * scaleX
                    val cy = sy * scaleY
                    canvas.drawRect(cx, cy, cx + scaleX, cy + scaleY, peakPaint)
                }
            }
        }

        if (!scaled.sameAs(sourceBitmap)) {
            scaled.recycle()
        }
    }

    private fun luminance(pixel: Int): Int {
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)
        return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
    }
}
