package com.generator.qrcodegenerator.style


fun interface QrShapeModifier {
    operator fun invoke(
        i: Int, j: Int, elementSize: Int, neighbors: Neighbors
    ): Boolean
}
