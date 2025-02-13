package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.style.IQrVectorShapes
import com.generator.qrcodegenerator.style.QrVectorBallShape
import com.generator.qrcodegenerator.style.QrVectorFrameShape
import com.generator.qrcodegenerator.style.QrVectorPixelShape

sealed interface QrVectorShapesBuilderScope : IQrVectorShapes {
    override var darkPixel: QrVectorPixelShape
    override var lightPixel: QrVectorPixelShape
    override var ball: QrVectorBallShape
    override var frame: QrVectorFrameShape
}

