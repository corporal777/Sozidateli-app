package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.style.IQrOffset
import com.generator.qrcodegenerator.style.QrOffsetBuilder


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