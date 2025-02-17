package com.generator.qrcodegenerator.style

import com.generator.qrcodegenerator.encoder.QrCodeMatrix


interface RandomBased {
    val seed : Long
}

interface QrShape {

    val shapeSizeIncrease : Float

    fun apply(matrix: QrCodeMatrix) : QrCodeMatrix

    fun pixelInShape(i : Int, j : Int, modifiedByteMatrix: QrCodeMatrix) : Boolean


    
    object Default : QrShape {
        override val shapeSizeIncrease: Float = 1f

        override fun apply(matrix: QrCodeMatrix) = matrix

        override fun pixelInShape(i: Int, j: Int, modifiedByteMatrix: QrCodeMatrix)  = true
    }


    

}