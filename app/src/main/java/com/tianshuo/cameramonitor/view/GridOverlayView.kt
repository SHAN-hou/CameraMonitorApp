package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

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

    var sourceImageView: ImageView? = null

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
        when (gridType) {
            GridType.NONE -> return
            GridType.THIRDS -> drawThirds(canvas)
            GridType.CROSSHAIR -> drawCrosshair(canvas)
        }
    }

    private fun drawThirds(canvas: Canvas) {
        val b = getImageBounds()

        // Vertical lines at 1/3 and 2/3
        val x1 = b.left + b.width() / 3f
        val x2 = b.left + 2f * b.width() / 3f
        canvas.drawLine(x1, b.top, x1, b.bottom, gridPaint)
        canvas.drawLine(x2, b.top, x2, b.bottom, gridPaint)

        // Horizontal lines at 1/3 and 2/3
        val y1 = b.top + b.height() / 3f
        val y2 = b.top + 2f * b.height() / 3f
        canvas.drawLine(b.left, y1, b.right, y1, gridPaint)
        canvas.drawLine(b.left, y2, b.right, y2, gridPaint)
    }

    private fun drawCrosshair(canvas: Canvas) {
        val b = getImageBounds()
        val cx = b.centerX()
        val cy = b.centerY()

        canvas.drawLine(cx, b.top, cx, b.bottom, gridPaint)
        canvas.drawLine(b.left, cy, b.right, cy, gridPaint)
    }
}
