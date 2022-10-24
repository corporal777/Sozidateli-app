package com.example.util.qr_generator.vector.style

import com.example.util.qr_generator.SerializationProvider
import com.example.util.qr_generator.SerializersModuleFromProviders
import com.example.util.qr_generator.style.QrFrameShape
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
            SerializersModuleFromProviders(
                QrVectorPixelShape,
                QrVectorBallShape,
                QrFrameShape
            )
        }
    }
}