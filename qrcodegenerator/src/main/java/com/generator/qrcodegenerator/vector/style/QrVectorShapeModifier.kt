package com.generator.qrcodegenerator.vector.style

import android.graphics.Path
import com.generator.qrcodegenerator.style.Neighbors

interface QrVectorShapeModifier {

    val isDependOnNeighbors : Boolean

    fun createPath(size : Float, neighbors: Neighbors) : Path
}