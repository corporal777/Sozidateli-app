package com.generator.qrcodegenerator.vector

import androidx.annotation.FloatRange
import com.generator.qrcodegenerator.QrErrorCorrectionLevel
import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.SerializersModuleFromProviders
import com.generator.qrcodegenerator.style.QrLogo
import com.generator.qrcodegenerator.style.QrLogoBuilder
import com.generator.qrcodegenerator.style.QrOffset
import com.generator.qrcodegenerator.style.QrOffsetBuilder
import com.generator.qrcodegenerator.vector.dsl.InternalQrVectorOptionsBuilderScope
import com.generator.qrcodegenerator.vector.dsl.QrVectorOptionsBuilderScope
import com.generator.qrcodegenerator.vector.style.QrVectorColors
import com.generator.qrcodegenerator.vector.style.QrVectorShapes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class QrVectorOptions(
    @FloatRange(from = .0, to = .5)
    val padding : Float = .125f,
    val offset: QrOffset,
    val shapes: QrVectorShapes,
    val colors : QrVectorColors,
    val logo : QrLogo,
    val errorCorrectionLevel: QrErrorCorrectionLevel
)  {
    class Builder : QrOffsetBuilder, QrLogoBuilder {

        @FloatRange(from = .0, to = .5)
        var padding : Float = 0f
        override var offset: QrOffset = QrOffset(0f,0f)
        var shapes: QrVectorShapes = QrVectorShapes()
        var colors : QrVectorColors = QrVectorColors()
        override var logo : QrLogo = QrLogo()
        var errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Low

        fun setPadding(@FloatRange(from = .0, to = .5) padding: Float) = apply {
            this.padding = padding
        }

        fun setOffset(offset: QrOffset) = apply {
            this.offset = offset
        }

        fun setShapes(shapes: QrVectorShapes) = apply {
            this.shapes = shapes
        }

        fun setColors(colors: QrVectorColors) = apply {
            this.colors = colors
        }

        fun setLogo(logo: QrLogo) = apply {
            this.logo = logo
        }

        fun setErrorCorrectionLevel(errorCorrectionLevel: QrErrorCorrectionLevel) = apply {
            this.errorCorrectionLevel = errorCorrectionLevel
        }

        fun build() : QrVectorOptions = QrVectorOptions(
            padding, offset, shapes, colors, logo, errorCorrectionLevel
        )
    }

    companion object : SerializationProvider {
        @ExperimentalSerializationApi
        override val defaultSerializersModule: SerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            SerializersModuleFromProviders(
                QrVectorShapes, QrVectorColors, QrLogo
            )
        }
    }
}

fun createQrVectorOptions(block : QrVectorOptionsBuilderScope.() -> Unit) : QrVectorOptions {
    val builder = QrVectorOptions.Builder()
    InternalQrVectorOptionsBuilderScope(builder).apply(block)
    return builder.build()
}

