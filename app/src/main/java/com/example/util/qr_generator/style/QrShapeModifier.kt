package com.example.util.qr_generator.style

import com.example.util.qr_generator.style.Neighbors
import com.example.util.qr_generator.style.Neighbors.Companion.Empty


fun interface QrShapeModifier {
    operator fun invoke(
        i: Int, j: Int, elementSize: Int, neighbors: Neighbors
    ): Boolean
}
