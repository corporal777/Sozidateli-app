package com.generator.qrcodegenerator.encoder

import com.google.zxing.qrcode.encoder.ByteMatrix

fun ByteMatrix.toQrMatrix() : QrCodeMatrix {
    if (width != height)
        throw IllegalStateException("Non-square qr byte matrix")

    return QrCodeMatrix(width).apply {
        for (i in 0 until width){
            for (j in 0 until width){
                this[i,j] = if (this@toQrMatrix[i,j].toInt() == 1)
                    QrCodeMatrix.PixelType.DarkPixel
                else QrCodeMatrix.PixelType.LightPixel
            }
        }
    }
}