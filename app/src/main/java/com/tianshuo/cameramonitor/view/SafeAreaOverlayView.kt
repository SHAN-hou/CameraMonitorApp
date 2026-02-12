package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class SafeAreaOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var sourceImageView: ImageView? = null

    var showSafeArea: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val safeActionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        alpha = 160
        pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
    }

    private val safeTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        alpha = 160
        pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        textSize = 18f
        alpha = 160
    }

    private fun getImageBounds(): RectF {
        val iv = sourceImageView
        return if (iv != null) {
            ImageBoundsHelper.getImageBounds(iv)
        } else {
            RectF(0f, 0f, width.toFloat(), height.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!showSafeArea) return

        val b = getImageBounds()
        val w = b.width()
        val h = b.height()

        // Action safe area (90%)
        val actionMarginX = w * 0.05f
        val actionMarginY = h * 0.05f
        canvas.drawRect(
            b.left + actionMarginX, b.top + actionMarginY,
            b.right - actionMarginX, b.bottom - actionMarginY,
            safeActionPaint
        )
        canvas.drawText("Action Safe 90%", b.left + actionMarginX + 4f, b.top + actionMarginY + 16f, labelPaint)

        // Title safe area (80%)
        val titleMarginX = w * 0.1f
        val titleMarginY = h * 0.1f
        canvas.drawRect(
            b.left + titleMarginX, b.top + titleMarginY,
            b.right - titleMarginX, b.bottom - titleMarginY,
            safeTitlePaint
        )
        labelPaint.color = Color.YELLOW
        canvas.drawText("Title Safe 80%", b.left + titleMarginX + 4f, b.top + titleMarginY + 16f, labelPaint)
        labelPaint.color = Color.GREEN
    }
}
