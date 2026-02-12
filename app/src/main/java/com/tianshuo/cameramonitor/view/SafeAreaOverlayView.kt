package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SafeAreaOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!showSafeArea) return

        val w = width.toFloat()
        val h = height.toFloat()

        // Action safe area (90%)
        val actionMarginX = w * 0.05f
        val actionMarginY = h * 0.05f
        canvas.drawRect(
            actionMarginX, actionMarginY,
            w - actionMarginX, h - actionMarginY,
            safeActionPaint
        )
        canvas.drawText("Action Safe 90%", actionMarginX + 4f, actionMarginY + 16f, labelPaint)

        // Title safe area (80%)
        val titleMarginX = w * 0.1f
        val titleMarginY = h * 0.1f
        canvas.drawRect(
            titleMarginX, titleMarginY,
            w - titleMarginX, h - titleMarginY,
            safeTitlePaint
        )
        labelPaint.color = Color.YELLOW
        canvas.drawText("Title Safe 80%", titleMarginX + 4f, titleMarginY + 16f, labelPaint)
        labelPaint.color = Color.GREEN
    }
}
