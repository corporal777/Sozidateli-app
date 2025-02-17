package com.generator.qrcodegenerator.style

import android.graphics.Path

fun interface QrVectorShapeModifier {

    fun createPath(size : Float, neighbors: Neighbors) : Path
}