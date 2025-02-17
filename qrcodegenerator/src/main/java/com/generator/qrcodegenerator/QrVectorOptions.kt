package com.generator.qrcodegenerator

import androidx.annotation.FloatRange
import com.generator.qrcodegenerator.QrHighlighting
import com.generator.qrcodegenerator.QrErrorCorrectionLevel
import com.generator.qrcodegenerator.style.QrOffset
import com.generator.qrcodegenerator.style.QrShape
import com.generator.qrcodegenerator.dsl.InternalQrVectorOptionsBuilderScope
import com.generator.qrcodegenerator.dsl.QrVectorOptionsBuilderScope
import com.generator.qrcodegenerator.style.QrVectorBackground
import com.generator.qrcodegenerator.style.QrVectorColors
import com.generator.qrcodegenerator.style.QrVectorLogo
import com.generator.qrcodegenerator.style.QrVectorShapes


data class QrVectorOptions(
    @FloatRange(from = .0, to = .5)
    val padding : Float,
    val offset: QrOffset,
    val shapes: QrVectorShapes,
    val codeShape : QrShape,
    val colors : QrVectorColors,
    val logo : QrVectorLogo,
    val background: QrVectorBackground,
    val errorCorrectionLevel: QrErrorCorrectionLevel,
    val fourthEyeEnabled : Boolean,
    val highlighting: QrHighlighting
) {
    class Builder {

        @FloatRange(from = .0, to = .5)
        var padding: Float = 0f
            private set
        var offset: QrOffset = QrOffset(0f, 0f)
            private set
        var shapes: QrVectorShapes = QrVectorShapes()
            private set
        var shape: QrShape = QrShape.Default
            private set
        var colors: QrVectorColors = QrVectorColors()
            private set
        var logo: QrVectorLogo = QrVectorLogo()
            private set
        var background: QrVectorBackground = QrVectorBackground()
            private set
        var errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto
            private set
        var fourthEyeEnabled: Boolean = false
            private set

        var highlighting : QrHighlighting = QrHighlighting()
            private set

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

        fun setCodeShape(shape: QrShape) = apply {
            this.shape = shape
        }

        fun setLogo(logo: QrVectorLogo) = apply {
            this.logo = logo
        }

        fun setBackground(background: QrVectorBackground) = apply {
            this.background = background
        }

        fun setErrorCorrectionLevel(errorCorrectionLevel: QrErrorCorrectionLevel) = apply {
            this.errorCorrectionLevel = errorCorrectionLevel
        }

        fun setFourthEyeEnabled(enabled: Boolean) = apply {
            this.fourthEyeEnabled = enabled
        }

        fun setAnchorsHighlighting(highlighting: QrHighlighting) = apply {
            this.highlighting = highlighting
        }

        fun build(): QrVectorOptions = QrVectorOptions(
            padding = padding,
            offset = offset,
            shapes = shapes,
            codeShape = shape,
            colors = colors,
            logo = logo,
            background = background,
            errorCorrectionLevel = errorCorrectionLevel,
            fourthEyeEnabled = fourthEyeEnabled,
            highlighting = highlighting
        )
    }
}
fun createQrVectorOptions(block : QrVectorOptionsBuilderScope.() -> Unit) : QrVectorOptions {
    val builder = QrVectorOptions.Builder()
    InternalQrVectorOptionsBuilderScope(builder).apply(block)
    return builder.build()
}

