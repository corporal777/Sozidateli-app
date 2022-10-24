package com.example.util.qr_generator.vector.dsl

import com.example.util.qr_generator.vector.QrVectorOptions
import com.example.util.qr_generator.vector.style.IQrVectorShapes
import com.example.util.qr_generator.vector.style.QrVectorBallShape
import com.example.util.qr_generator.vector.style.QrVectorFrameShape
import com.example.util.qr_generator.vector.style.QrVectorPixelShape

sealed interface QrVectorShapesBuilderScope : IQrVectorShapes {
    override var darkPixel: QrVectorPixelShape
    override var lightPixel: QrVectorPixelShape
    override var ball: QrVectorBallShape
    override var frame: QrVectorFrameShape
}

internal class InternalQrVectorShapesBuilderScope(
    private val builder: QrVectorOptions.Builder
) : QrVectorShapesBuilderScope {
    override var darkPixel: QrVectorPixelShape
        get() = builder.shapes.darkPixel
        set(value) = with(builder){
            setShapes(shapes.copy(
                darkPixel = value
            ))
        }

    override var lightPixel: QrVectorPixelShape
        get() = builder.shapes.lightPixel
        set(value) = with(builder){
            setShapes(shapes.copy(
                lightPixel = value
            ))
        }

    override var ball: QrVectorBallShape
        get() = builder.shapes.ball
        set(value) = with(builder){
            setShapes(shapes.copy(
                ball = value
            ))
        }

    override var frame: QrVectorFrameShape
        get() = builder.shapes.frame
        set(value) = with(builder){
            setShapes(shapes.copy(
                frame = value
            ))
        }
}