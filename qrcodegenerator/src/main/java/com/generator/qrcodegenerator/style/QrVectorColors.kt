package com.generator.qrcodegenerator.style

import android.graphics.Color
import androidx.core.graphics.toColor

interface IQrVectorColors {

    val dark : QrVectorColor
    val light : QrVectorColor
    val ball : QrVectorColor
    val frame : QrVectorColor
}

data class QrVectorColors(
    override val dark : QrVectorColor = QrVectorColor.Solid(Color.BLACK),
    override val light : QrVectorColor = QrVectorColor.Unspecified,
    override val ball : QrVectorColor = QrVectorColor.Unspecified,
    override val frame : QrVectorColor = QrVectorColor.Unspecified,
) : IQrVectorColors