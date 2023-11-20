package com.generator.qrcodegenerator.style

import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.serializersModuleFromProviders
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


interface IQRElementsShapes {
    val darkPixel : QrPixelShape
    val lightPixel : QrPixelShape
    val frame : QrFrameShape
    val ball : QrBallShape
    val highlighting : QrHighlightingShape
}

@Serializable
data class QrElementsShapes(
    override val darkPixel : QrPixelShape = QrPixelShape.Default,
    override val lightPixel : QrPixelShape = QrPixelShape.Default,
    override val frame : QrFrameShape = QrFrameShape.Default,
    override val ball : QrBallShape = QrBallShape.Default,
    override val highlighting : QrHighlightingShape = QrHighlightingShape.Default,
) : IQRElementsShapes {
    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            serializersModuleFromProviders(
                QrPixelShape,
                QrFrameShape,
                QrBallShape,
                QrHighlightingShape
            )
        }
    }
}