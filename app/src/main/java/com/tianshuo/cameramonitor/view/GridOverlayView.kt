package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class GridType(val label: String) {
        NONE("关闭"),
        THIRDS("三分线"),
        CROSSHAIR("十字线")
    }

    var gridType: GridType = GridType.NONE
        set(value) {
            field = value
            invalidate()
        }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1f
        alpha = 140
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (gridType) {
            GridType.NONE -> return
            GridType.THIRDS -> drawThirds(canvas)
            GridType.CROSSHAIR -> drawCrosshair(canvas)
        }
    }

    private fun drawThirds(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        // Vertical lines at 1/3 and 2/3
        canvas.drawLine(w / 3f, 0f, w / 3f, h, gridPaint)
        canvas.drawLine(2f * w / 3f, 0f, 2f * w / 3f, h, gridPaint)

        // Horizontal lines at 1/3 and 2/3
        canvas.drawLine(0f, h / 3f, w, h / 3f, gridPaint)
        canvas.drawLine(0f, 2f * h / 3f, w, 2f * h / 3f, gridPaint)
    }

    private fun drawCrosshair(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f

        canvas.drawLine(cx, 0f, cx, h, gridPaint)
        canvas.drawLine(0f, cy, w, cy, gridPaint)
    }
}
