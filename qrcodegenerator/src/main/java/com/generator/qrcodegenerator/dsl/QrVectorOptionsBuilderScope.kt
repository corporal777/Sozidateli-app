package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.QrErrorCorrectionLevel
import com.generator.qrcodegenerator.style.QrShape
import com.generator.qrcodegenerator.style.QrVectorColor

sealed interface QrVectorOptionsBuilderScope  {

    var padding: Float

    var errorCorrectionLevel: QrErrorCorrectionLevel

    var codeShape : QrShape

    var fourthEyeEnabled : Boolean

    fun offset(x : Float, y : Float)

    fun shapes(centralSymmetry : Boolean = true, block: QrVectorShapesBuilderScope.() -> Unit)

    fun colors(block: QrVectorColorsBuilderScope.() -> Unit)

    fun background(block: QrVectorBackgroundBuilderScope.() -> Unit)

    fun logo(block: QrVectorLogoBuilderScope.() -> Unit)

    fun highlighting(block : QrHighlightingBuilderScope.() -> Unit)
}




