@file:Suppress("UNUSED")

package com.generator.qrcodegenerator

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel


enum class QrErrorCorrectionLevel(
    internal val lvl : ErrorCorrectionLevel
) {
    Auto(ErrorCorrectionLevel.L),

    Low(ErrorCorrectionLevel.L),

    Medium(ErrorCorrectionLevel.M),

    MediumHigh(ErrorCorrectionLevel.Q),

    High(ErrorCorrectionLevel.H)
}

internal fun QrErrorCorrectionLevel.fit(
    hasLogo: Boolean,
    logoSize : Float,
) : QrErrorCorrectionLevel {
    return if (this == QrErrorCorrectionLevel.Auto)
        when {
            !hasLogo -> QrErrorCorrectionLevel.Low
            logoSize > .3 -> QrErrorCorrectionLevel.High
            logoSize in .2 .. .3 && lvl < ErrorCorrectionLevel.Q ->
                QrErrorCorrectionLevel.MediumHigh
            logoSize > .05f && lvl < ErrorCorrectionLevel.M ->
                QrErrorCorrectionLevel.Medium
            else -> this
        } else this
}