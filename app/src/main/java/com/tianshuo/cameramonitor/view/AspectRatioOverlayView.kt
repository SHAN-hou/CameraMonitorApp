package com.tianshuo.cameramonitor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class AspectRatioOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class AspectRatio(val label: String, val widthRatio: Float, val heightRatio: Float) {
        NONE("关闭", 0f, 0f),
        RATIO_16_9("16:9", 16f, 9f),
        RATIO_4_3("4:3", 4f, 3f),
        RATIO_185_1("1.85:1", 1.85f, 1f),
        RATIO_235_1("2.35:1", 2.35f, 1f)
    }

    var sourceImageView: ImageView? = null

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
        alpha = 200
    }

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 120
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        alpha = 180
    }

    var currentRatio: AspectRatio = AspectRatio.NONE
        set(value) {
            field = value
            invalidate()
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
        if (currentRatio == AspectRatio.NONE) return

        val imgBounds = getImageBounds()
        val imgWidth = imgBounds.width()
        val imgHeight = imgBounds.height()
        val targetRatio = currentRatio.widthRatio / currentRatio.heightRatio
        val imgRatio = imgWidth / imgHeight

        val frameWidth: Float
        val frameHeight: Float

        if (targetRatio > imgRatio) {
            frameWidth = imgWidth
            frameHeight = imgWidth / targetRatio
        } else {
            frameHeight = imgHeight
            frameWidth = imgHeight * targetRatio
        }

        val left = imgBounds.left + (imgWidth - frameWidth) / 2f
        val top = imgBounds.top + (imgHeight - frameHeight) / 2f
        val right = left + frameWidth
        val bottom = top + frameHeight

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Draw mask (semi-transparent black outside the frame)
        canvas.drawRect(0f, 0f, viewWidth, top, maskPaint)
        canvas.drawRect(0f, bottom, viewWidth, viewHeight, maskPaint)
        canvas.drawRect(0f, top, left, bottom, maskPaint)
        canvas.drawRect(right, top, viewWidth, bottom, maskPaint)

        // Draw frame border
        canvas.drawRect(left, top, right, bottom, framePaint)

        // Draw label
        val label = currentRatio.label
        val labelWidth = labelPaint.measureText(label)
        canvas.drawText(label, right - labelWidth - 8f, bottom - 8f, labelPaint)
    }
}
