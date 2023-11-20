package com.generator.qrcodegenerator.style

import androidx.annotation.FloatRange

import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.serializersModuleFromProviders
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

interface IQRBackground {
    val drawable: DrawableSource
    val alpha : Float
    val scale: BitmapScale
    val color: QrColor
}

@Serializable
data class QrBackground(
    override val drawable: DrawableSource = DrawableSource.Empty,
    @FloatRange(from = 0.0, to = 1.0)
    override val alpha : Float = 1f,
    override val scale: BitmapScale = BitmapScale.FitXY,
    override val color: QrColor = QrColor.Solid(Color(0xffffffff))
) : IQRBackground {
    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            serializersModuleFromProviders(
                DrawableSource,
                BitmapScale,
                QrColor
            )
        }
    }
}