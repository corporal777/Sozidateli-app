package com.generator.qrcodegenerator.style

import androidx.annotation.FloatRange
import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.SerializersModuleFromProviders
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


interface IQRLogo {
    val drawable: DrawableSource
    val size: Float
    val padding : QrLogoPadding
    val shape: QrLogoShape
    val scale: BitmapScale
    val backgroundColor : QrColor
}

@Serializable
data class QrLogo(
    override val drawable: DrawableSource = DrawableSource.Empty,
    @FloatRange(from = 0.0, to = 1/3.0)
    override val size : Float = 0.2f,
    override val padding : QrLogoPadding = QrLogoPadding.Empty,
    override val shape: QrLogoShape = QrLogoShape.Default,
    override val scale: BitmapScale = BitmapScale.FitXY,
    override val backgroundColor : QrColor = QrColor.Unspecified
) : IQRLogo {

    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            SerializersModuleFromProviders(
                DrawableSource,
                QrLogoPadding,
                QrLogoShape,
                BitmapScale
            )
        }
    }
}

interface QrLogoBuilder {
    var logo : QrLogo
}

