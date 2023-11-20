package com.generator.qrcodegenerator

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.generator.qrcodegenerator.dsl.QrOptionsBuilderScope
import com.generator.qrcodegenerator.style.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class QrOptions(
    @IntRange(from = 0) val width: Int,
    @IntRange(from = 0) val height: Int,
    @FloatRange(from = .0, to = .5) val padding: Float,
    val offset: QrOffset,
    val colors: QrColors,
    val logo: QrLogo,
    val background: QrBackground,
    val shapes: QrElementsShapes,
    val codeShape: QrShape,
    val errorCorrectionLevel: QrErrorCorrectionLevel
) {

    class Builder(
        @IntRange(from = 0) val width: Int,
        @IntRange(from = 0) val height: Int = width
    ) : QrLogoBuilder, QrOffsetBuilder {

        var padding = .125f
        override var offset = QrOffset.Zero
        var colors = QrColors()
        override var logo = QrLogo()
        var background = QrBackground()
        var elementsShapes = QrElementsShapes()
        var codeShape: QrShape = QrShape.Default
        var errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto

        fun build(): QrOptions = QrOptions(
            width, height, padding, offset, colors, logo, background,
            elementsShapes, codeShape, errorCorrectionLevel
        )

        fun setPadding(@FloatRange(from = 0.0, to = .5) padding: Float) = apply {
            this.padding = padding
        }

        fun setOffset(offset: QrOffset) = apply {
            this.offset = offset
        }

        fun setColors(colors: QrColors) = apply {
            this.colors = colors
        }

        fun setLogo(logo: QrLogo?) = apply {
            this.logo = logo ?: QrLogo()
        }

        fun setBackground(background: QrBackground?) = apply {
            this.background = background ?: QrBackground()
        }

        fun setCodeShape(shape: QrShape): Builder = apply {
            this.codeShape = shape
        }

        fun setElementsShapes(shapes: QrElementsShapes) = apply {
            this.elementsShapes = shapes
        }

        fun setErrorCorrectionLevel(level: QrErrorCorrectionLevel) = apply {
            errorCorrectionLevel = level
        }
    }


    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            serializersModuleFromProviders(
                QrColors,
                QrLogo,
                QrBackground,
                QrElementsShapes,
                QrShape
            )
        }
    }
}

inline fun createQrOptions(
    width: Int,
    height: Int = width,
    padding: Float = .125f,
    crossinline build: QrOptionsBuilderScope.() -> Unit
): QrOptions = with(QrOptions.Builder(width, height).setPadding(padding)) {
    QrOptionsBuilderScope(this).apply(build)
    build()
}

fun createReadyBitmapQrOptions(uri: String, bm: Bitmap, icon: Int, color: Int): QrOptions {
    return createQrOptions(1400, 1400, .1f) {
        logo {
            val drawableSource: DrawableSource
            val drawableShape: QrLogoShape
            if (!uri.isNullOrEmpty()) {
                drawableShape = QrLogoShape.RoundCorners(.30f)
                drawableSource = DrawableSource.DecodedBitmap(bm)
            } else {
                drawableShape = QrLogoShape.Circle
                drawableSource = DrawableSource.Resource(icon)
            }
            drawable = drawableSource
            size = .25f
            padding = QrLogoPadding.Accurate(.1f)
            shape = drawableShape
        }
        colors {
            dark = QrColor.Solid(color)
        }
        shapes {
//                    darkPixel = QrPixelShape.RoundCorners()
//                    ball = QrBallShape.RoundCorners(.30f)
//                    frame = QrFrameShape.RoundCorners(.30f)
            darkPixel = QrPixelShape.Default
            ball = QrBallShape.Default
            frame = QrFrameShape.Default
        }
    }
}

