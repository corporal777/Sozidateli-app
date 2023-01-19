package com.generator.qrcodegenerator.vector.dsl

import com.generator.qrcodegenerator.QrErrorCorrectionLevel
import com.generator.qrcodegenerator.dsl.InternalQrLogoBuilderScope
import com.generator.qrcodegenerator.dsl.InternalQrOffsetBuilderScope
import com.generator.qrcodegenerator.dsl.QrLogoBuilderScope
import com.generator.qrcodegenerator.dsl.QrOffsetBuilderScope
import com.generator.qrcodegenerator.vector.QrVectorOptions

sealed interface QrVectorOptionsBuilderScope  {

    var padding: Float
    var errorCorrectionLevel: QrErrorCorrectionLevel

    fun offset(block: QrOffsetBuilderScope.() -> Unit)
    fun shapes(block: QrVectorShapesBuilderScope.() -> Unit)
    fun colors(block: QrVectorColorsBuilderScope.() -> Unit)
    fun logo(block: QrLogoBuilderScope.() -> Unit)
}

internal class InternalQrVectorOptionsBuilderScope(
    val builder: QrVectorOptions.Builder
) : QrVectorOptionsBuilderScope {

    override var padding: Float by builder::padding

    override var errorCorrectionLevel: QrErrorCorrectionLevel by builder::errorCorrectionLevel

    override fun offset(block: QrOffsetBuilderScope.() -> Unit) {
        InternalQrOffsetBuilderScope(builder).apply(block)
    }

    override fun shapes(block: QrVectorShapesBuilderScope.() -> Unit) {
        InternalQrVectorShapesBuilderScope(builder).apply(block)
    }

    override fun colors(block: QrVectorColorsBuilderScope.() -> Unit) {
        InternalQrVectorColorsBuilderScope(builder).apply(block)
    }

    override fun logo(block: QrLogoBuilderScope.() -> Unit) {
        InternalQrLogoBuilderScope(builder, codePadding = padding)
            .apply(block)
    }

}