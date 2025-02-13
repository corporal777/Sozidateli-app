package com.generator.qrcodegenerator.style

sealed interface QrVectorLogoPadding {

    val value : Float


    object Empty : QrVectorLogoPadding {
        override val value: Float get() = 0f
    }

    data class Accurate(override val value: Float) : QrVectorLogoPadding

    data class Natural(override val value: Float) : QrVectorLogoPadding
}