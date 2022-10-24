package com.example.util.qr_generator

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