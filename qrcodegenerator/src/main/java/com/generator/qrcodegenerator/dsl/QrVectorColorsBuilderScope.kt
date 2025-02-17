package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.style.IQrVectorColors
import com.generator.qrcodegenerator.style.QrVectorColor


sealed interface QrVectorColorsBuilderScope : IQrVectorColors {
    override var ball: QrVectorColor
    override var dark: QrVectorColor
    override var frame: QrVectorColor
    override var light: QrVectorColor
}

