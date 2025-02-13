package com.generator.qrcodegenerator.dsl

import com.generator.qrcodegenerator.QrVectorOptions
import com.generator.qrcodegenerator.dsl.QrVectorColorsBuilderScope
import com.generator.qrcodegenerator.style.QrVectorColor

internal class InternalQrVectorColorsBuilderScope(
    private val builder: QrVectorOptions.Builder
) : QrVectorColorsBuilderScope {
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