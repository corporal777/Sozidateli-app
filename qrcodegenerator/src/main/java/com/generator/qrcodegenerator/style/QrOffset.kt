package com.generator.qrcodegenerator.style

import androidx.annotation.FloatRange

interface IQrOffset {
    val x : Float
    val y : Float
}

data class QrOffset(
    @FloatRange(from = -1.0, to = 1.0) override val x : Float,
    @FloatRange(from = -1.0, to = 1.0) override val y : Float,
) : IQrOffset {
    companion object {
        val Zero = QrOffset(0f,0f)
    }
}
