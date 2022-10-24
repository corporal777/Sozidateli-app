package com.example.util.qr_generator.vector.dsl

import com.example.util.qr_generator.QrErrorCorrectionLevel
import com.example.util.qr_generator.dsl.InternalQrLogoBuilderScope
import com.example.util.qr_generator.dsl.InternalQrOffsetBuilderScope
import com.example.util.qr_generator.dsl.QrLogoBuilderScope
import com.example.util.qr_generator.dsl.QrOffsetBuilderScope
import com.example.util.qr_generator.vector.QrVectorOptions

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