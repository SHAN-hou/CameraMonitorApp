package com.tianshuo.cameramonitor.view

import android.graphics.RectF
import android.widget.ImageView

object ImageBoundsHelper {

    fun getImageBounds(imageView: ImageView): RectF {
        val drawable = imageView.drawable ?: return RectF(0f, 0f, imageView.width.toFloat(), imageView.height.toFloat())

        val viewWidth = imageView.width.toFloat()
        val viewHeight = imageView.height.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()

        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return RectF(0f, 0f, viewWidth, viewHeight)
        }

        val imageRatio = drawableWidth / drawableHeight
        val viewRatio = viewWidth / viewHeight

        val displayWidth: Float
        val displayHeight: Float

        if (imageRatio > viewRatio) {
            displayWidth = viewWidth
            displayHeight = viewWidth / imageRatio
        } else {
            displayHeight = viewHeight
            displayWidth = viewHeight * imageRatio
        }

        val left = (viewWidth - displayWidth) / 2f
        val top = (viewHeight - displayHeight) / 2f

        return RectF(left, top, left + displayWidth, top + displayHeight)
    }
}
