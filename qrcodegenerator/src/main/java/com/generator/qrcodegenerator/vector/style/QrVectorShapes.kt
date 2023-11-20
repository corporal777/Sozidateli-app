package com.generator.qrcodegenerator.vector.style

import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.serializersModuleFromProviders
import com.generator.qrcodegenerator.style.QrFrameShape
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

interface IQrVectorShapes{
    val darkPixel: QrVectorPixelShape
    val lightPixel : QrVectorPixelShape
    val ball : QrVectorBallShape
    val frame : QrVectorFrameShape
}

@Serializable
data class QrVectorShapes(
    override val darkPixel: QrVectorPixelShape = QrVectorPixelShape.Default,
    override val lightPixel : QrVectorPixelShape = QrVectorPixelShape.Default,
    override val ball : QrVectorBallShape = QrVectorBallShape.Default,
    override val frame : QrVectorFrameShape = QrVectorFrameShape.Default
) : IQrVectorShapes {
    companion object : SerializationProvider {
        @ExperimentalSerializationApi
        override val defaultSerializersModule: SerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            serializersModuleFromProviders(
                QrVectorPixelShape,
                QrVectorBallShape,
                QrFrameShape
            )
        }
    }
}