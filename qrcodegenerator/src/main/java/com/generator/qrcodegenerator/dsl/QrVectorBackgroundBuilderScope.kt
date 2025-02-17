package com.generator.qrcodegenerator.dsl

import android.graphics.drawable.Drawable
import com.generator.qrcodegenerator.style.BitmapScale
import com.generator.qrcodegenerator.style.IQrVectorBackground
import com.generator.qrcodegenerator.style.QrVectorColor

sealed interface QrVectorBackgroundBuilderScope : IQrVectorBackground {

    override var drawable: Drawable?
    override var scale: BitmapScale
    override var color: QrVectorColor
}