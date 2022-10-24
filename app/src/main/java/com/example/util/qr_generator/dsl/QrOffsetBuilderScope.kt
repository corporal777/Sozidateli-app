package com.example.util.qr_generator.dsl

import com.example.util.qr_generator.QrOptions
import com.example.util.qr_generator.style.IQrOffset
import com.example.util.qr_generator.style.QrOffsetBuilder
import com.example.util.qr_generator.vector.QrVectorOptions


interface QrOffsetBuilderScope : IQrOffset {
    override var x: Float
    override var y: Float
}

internal class InternalQrOffsetBuilderScope(
    private val builder: QrOffsetBuilder
) : QrOffsetBuilderScope {

    override var x: Float
        get() = builder.offset.x
        set(value) = with(builder) {
            offset = offset.copy(x = value)
        }

    override var y: Float
        get() = builder.offset.y
        set(value) = with(builder) {
            offset = offset.copy(y = value)
        }
}