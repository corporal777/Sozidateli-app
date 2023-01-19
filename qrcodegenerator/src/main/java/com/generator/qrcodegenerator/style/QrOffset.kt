package com.generator.qrcodegenerator.style

import androidx.annotation.FloatRange
import kotlinx.serialization.Serializable


interface IQrOffset {
    val x : Float
    val y : Float
}

@Serializable
data class QrOffset(
    @FloatRange(from = -1.0, to = 1.0) override val x : Float,
    @FloatRange(from = -1.0, to = 1.0) override val y : Float,
) : IQrOffset {
    companion object {
        val Zero = QrOffset(0f,0f)
    }
}

interface QrOffsetBuilder {
    var offset : QrOffset
}