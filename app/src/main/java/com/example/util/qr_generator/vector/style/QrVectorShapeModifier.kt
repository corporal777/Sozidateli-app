package com.example.util.qr_generator.vector.style

import android.graphics.Path
import com.example.util.qr_generator.style.Neighbors

interface QrVectorShapeModifier {

    val isDependOnNeighbors : Boolean

    fun createPath(size : Float, neighbors: Neighbors) : Path
}