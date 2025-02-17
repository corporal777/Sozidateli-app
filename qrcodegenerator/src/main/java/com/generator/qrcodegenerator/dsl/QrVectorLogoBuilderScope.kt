package com.generator.qrcodegenerator.dsl

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.generator.qrcodegenerator.style.BitmapScale
import com.generator.qrcodegenerator.style.*

sealed interface QrVectorLogoBuilderScope : IQRVectorLogo {
    override var bitmap: Bitmap?
    override var drawable: Drawable?
    override var size : Float
    override var padding : QrVectorLogoPadding
    override var shape: QrVectorLogoShape
    override var scale: BitmapScale
    override var backgroundColor : QrVectorColor
}

