package com.example.util.qr_generator.dsl

import com.example.util.qr_generator.QrOptions
import com.example.util.qr_generator.style.BitmapScale
import com.example.util.qr_generator.style.DrawableSource
import com.example.util.qr_generator.style.IQRBackground
import com.example.util.qr_generator.style.QrColor


sealed interface QrBackgroundBuilderScope : IQRBackground {

    override var drawable: DrawableSource

    override var alpha : Float

    override var scale: BitmapScale

    override var color : QrColor
}

class InternalQrBackgroundBuilderScope internal constructor(
    val builder: QrOptions.Builder
) : QrBackgroundBuilderScope {

    override var drawable: DrawableSource
        get() = builder.background.drawable
        set(value) = with(builder) {
            setBackground(background.copy(drawable = value))
        }

    override var alpha: Float
        get() = builder.background.alpha
        set(value) = with(builder) {
            setBackground(background.copy(alpha = value))
        }

    override var scale: BitmapScale
        get() = builder.background.scale
        set(value) = with(builder) {
            setBackground(background.copy(scale = value))
        }

    override var color: QrColor
        get() = builder.background.color
        set(value) = with(builder) {
            setBackground(background.copy(color = value))
        }
}