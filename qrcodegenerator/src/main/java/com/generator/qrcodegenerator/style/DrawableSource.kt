package com.generator.qrcodegenerator.style

import android.graphics.*
import android.graphics.drawable.Drawable

internal object EmptyDrawable : Drawable() {
    override fun draw(canvas: Canvas) = Unit
    override fun setAlpha(alpha: Int)  = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    override fun getOpacity(): Int = PixelFormat.TRANSPARENT
}