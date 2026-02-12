package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CenterMarkerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var showMarker: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
        alpha = 200
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!showMarker) return

        val cx = width / 2f
        val cy = height / 2f
        val size = 20f

        // Draw crosshair at center
        canvas.drawLine(cx - size, cy, cx + size, cy, markerPaint)
        canvas.drawLine(cx, cy - size, cx, cy + size, markerPaint)
        canvas.drawCircle(cx, cy, size * 0.7f, markerPaint)
    }
}
