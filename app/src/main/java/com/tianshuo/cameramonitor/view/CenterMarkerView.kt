package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class CenterMarkerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var sourceImageView: ImageView? = null

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
        if (!showMarker) return

        val b = getImageBounds()
        val cx = b.centerX()
        val cy = b.centerY()
        val size = 20f

        // Draw crosshair at center
        canvas.drawLine(cx - size, cy, cx + size, cy, markerPaint)
        canvas.drawLine(cx, cy - size, cx, cy + size, markerPaint)
        canvas.drawCircle(cx, cy, size * 0.7f, markerPaint)
    }
}
