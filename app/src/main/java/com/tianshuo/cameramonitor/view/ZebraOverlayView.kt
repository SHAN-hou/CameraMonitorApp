package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class ZebraOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class ZebraMode(val label: String, val threshold: Int) {
        OFF("关闭", 0),
        IRE_80("80 IRE", 200),   // ~80% of 255
        IRE_100("100 IRE", 245)  // ~96% of 255
    }

    var mode: ZebraMode = ZebraMode.OFF
        set(value) {
            field = value
            invalidate()
        }

    var sourceImageView: ImageView? = null

    private val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        alpha = 150
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private var zebraBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mode == ZebraMode.OFF) return

        val imgView = sourceImageView ?: return
        val drawable = imgView.drawable ?: return
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            return
        }

        if (bitmap.isRecycled) return

        drawZebraOnBrightAreas(canvas, bitmap)
    }

    private fun drawZebraOnBrightAreas(canvas: Canvas, sourceBitmap: Bitmap) {
        val w = width
        val h = height
        if (w <= 0 || h <= 0) return

        val threshold = mode.threshold

        // Sample the source bitmap at a reduced resolution for performance
        val sampleW = minOf(w / 4, 480)
        val sampleH = minOf(h / 4, 270)
        val scaled = Bitmap.createScaledBitmap(sourceBitmap, sampleW, sampleH, true)

        val scaleX = w.toFloat() / sampleW
        val scaleY = h.toFloat() / sampleH
        val stripeSpacing = 8f

        for (sy in 0 until sampleH step 2) {
            for (sx in 0 until sampleW step 2) {
                val pixel = scaled.getPixel(sx, sy)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                val luminance = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

                if (luminance >= threshold) {
                    val cx = sx * scaleX
                    val cy = sy * scaleY
                    // Draw diagonal stripe pattern
                    canvas.drawLine(cx, cy, cx + stripeSpacing, cy + stripeSpacing, stripePaint)
                }
            }
        }

        if (!scaled.sameAs(sourceBitmap)) {
            scaled.recycle()
        }
    }
}
