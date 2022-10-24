package com.example.util.qr_generator.vector.dsl

import com.example.util.qr_generator.vector.QrVectorOptions
import com.example.util.qr_generator.vector.style.IQrVectorColors
import com.example.util.qr_generator.vector.style.QrVectorColor

sealed interface QrVectorColorsBuilderScope : IQrVectorColors {
    override var ball: QrVectorColor
    override var dark: QrVectorColor
    override var frame: QrVectorColor
    override var light: QrVectorColor
}

internal class InternalQrVectorColorsBuilderScope(
    private val builder: QrVectorOptions.Builder
) :  QrVectorColorsBuilderScope {
    override var dark: QrVectorColor
        get() = builder.colors.dark
        set(value) = with(builder){
            setColors(colors.copy(
                dark = value
            ))
        }

    override var light: QrVectorColor
        get() = builder.colors.light
        set(value) = with(builder){
            setColors(colors.copy(
                light = value
            ))
        }
    override var ball: QrVectorColor
        get() = builder.colors.ball
        set(value) = with(builder){
            setColors(colors.copy(
                ball = value
            ))
        }
    override var frame: QrVectorColor
        get() = builder.colors.frame
        set(value) = with(builder){
            setColors(colors.copy(
                frame = value
            ))
        }
}