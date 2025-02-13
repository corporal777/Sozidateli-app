package com.generator.qrcodegenerator.style

import android.graphics.drawable.Drawable

interface IQrVectorBackground  {
    val drawable: Drawable?
    val scale: BitmapScale
    val color : QrVectorColor
}


data class QrVectorBackground(
    override val drawable: Drawable? = null,
    override val scale: BitmapScale = BitmapScale.FitXY,
    override val color : QrVectorColor = QrVectorColor.Transparent
) : IQrVectorBackground