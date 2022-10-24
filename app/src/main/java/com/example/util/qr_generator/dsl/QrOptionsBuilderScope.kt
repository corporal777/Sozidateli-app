package com.example.util.qr_generator.dsl

import com.example.util.qr_generator.QrErrorCorrectionLevel
import com.example.util.qr_generator.QrOptions
import com.example.util.qr_generator.style.QrShape


sealed interface QrOptionsBuilderScope {

    var shape : QrShape

    val padding : Float

    val width : Int

    val height : Int

    var errorCorrectionLevel : QrErrorCorrectionLevel

    fun offset(block : QrOffsetBuilderScope.() -> Unit)

    fun logo(block : QrLogoBuilderScope.() -> Unit)

    fun background(block: QrBackgroundBuilderScope.() -> Unit)

    fun colors(block : QrColorsBuilderScope.() -> Unit)

    fun shapes(block : QrElementsShapesBuilderScope.() -> Unit)
}



fun QrOptionsBuilderScope(builder: QrOptions.Builder) : QrOptionsBuilderScope =
    InternalQrOptionsBuilderScope(builder)



private class InternalQrOptionsBuilderScope(
    private val builder: QrOptions.Builder
) : QrOptionsBuilderScope {

    override fun offset(block: QrOffsetBuilderScope.() -> Unit) {
        InternalQrOffsetBuilderScope(builder).apply(block)
    }

    override fun logo(block: QrLogoBuilderScope.() -> Unit) {
        InternalQrLogoBuilderScope(builder).apply(block)
    }

    override fun background(block: QrBackgroundBuilderScope.() -> Unit) {
        InternalQrBackgroundBuilderScope(builder).apply(block)
    }

    override fun colors(block: QrColorsBuilderScope.() -> Unit) {
        InternalColorsBuilderScope(builder).apply(block)
    }

    override fun shapes(block: QrElementsShapesBuilderScope.() -> Unit) {
        InternalQrElementsShapesBuilderScope(builder).apply(block)
    }

    override var shape: QrShape
        get() = builder.codeShape
        set(value) {
            builder.setCodeShape(value)
        }

    override val padding: Float
        get() = builder.padding

    override val width: Int
        get() = builder.width

    override val height: Int
        get() = builder.height

    override var errorCorrectionLevel: QrErrorCorrectionLevel
        get() = builder.errorCorrectionLevel
        set(value) {
            builder.setErrorCorrectionLevel(value)
        }
}