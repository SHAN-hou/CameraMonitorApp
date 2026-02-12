package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class HistogramView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var sourceImageView: ImageView? = null

    private val bgPaint = Paint().apply {
        color = Color.BLACK
        alpha = 180
    }

    private val redPaint = Paint().apply {
        color = Color.RED
        alpha = 120
        style = Paint.Style.FILL
    }

    private val greenPaint = Paint().apply {
        color = Color.GREEN
        alpha = 120
        style = Paint.Style.FILL
    }

    private val bluePaint = Paint().apply {
        color = Color.BLUE
        alpha = 120
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1f
        alpha = 100
    }

    fun updateHistogram() {
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // Draw background
        canvas.drawRect(0f, 0f, w, h, bgPaint)
        canvas.drawRect(0f, 0f, w, h, borderPaint)

        val imgView = sourceImageView ?: return
        val drawable = imgView.drawable ?: return
        val bitmap = if (drawable is BitmapDrawable) drawable.bitmap else return
        if (bitmap.isRecycled) return

        val histR = IntArray(256)
        val histG = IntArray(256)
        val histB = IntArray(256)

        val sampleW = minOf(bitmap.width, 200)
        val sampleH = minOf(bitmap.height, 200)
        val scaled = Bitmap.createScaledBitmap(bitmap, sampleW, sampleH, true)

        for (y in 0 until sampleH) {
            for (x in 0 until sampleW) {
                val pixel = scaled.getPixel(x, y)
                histR[Color.red(pixel)]++
                histG[Color.green(pixel)]++
                histB[Color.blue(pixel)]++
            }
        }

        if (!scaled.sameAs(bitmap)) {
            scaled.recycle()
        }

        val maxVal = maxOf(histR.max(), histG.max(), histB.max()).toFloat()
        if (maxVal == 0f) return

        val barWidth = w / 256f
        val padding = 2f

        for (i in 0 until 256) {
            val x = i * barWidth

            val rHeight = (histR[i] / maxVal) * (h - padding * 2)
            val gHeight = (histG[i] / maxVal) * (h - padding * 2)
            val bHeight = (histB[i] / maxVal) * (h - padding * 2)

            canvas.drawRect(x, h - padding - rHeight, x + barWidth, h - padding, redPaint)
            canvas.drawRect(x, h - padding - gHeight, x + barWidth, h - padding, greenPaint)
            canvas.drawRect(x, h - padding - bHeight, x + barWidth, h - padding, bluePaint)
        }
    }
}
