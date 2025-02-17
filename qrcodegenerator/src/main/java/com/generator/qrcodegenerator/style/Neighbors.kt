package com.generator.qrcodegenerator.style

data class Neighbors(
    val topLeft : Boolean=false,
    val topRight : Boolean=false,
    val left : Boolean=false,
    val top : Boolean=false,
    val right : Boolean=false,
    val bottomLeft: Boolean=false,
    val bottom: Boolean=false,
    val bottomRight: Boolean=false,
) {

    companion object {
        val Empty = Neighbors()
    }
}

