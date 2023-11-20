package com.generator.qrcodegenerator.style

import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.serializersModuleFromProviders
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


interface IQRColors {
    val light : QrColor
    val dark : QrColor
    val frame : QrColor
    val ball : QrColor
    val highlighting : QrColor
    val symmetry : Boolean
}

@Serializable
data class QrColors(
    override val light : QrColor = QrColor.Unspecified,
    override val dark : QrColor = QrColor.Solid(Color(0xff000000)),
    override val frame : QrColor = QrColor.Unspecified,
    override val ball : QrColor = QrColor.Unspecified,
    override val highlighting : QrColor = QrColor.Unspecified,
    override val symmetry : Boolean = true,
) : IQRColors {

    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            serializersModuleFromProviders(QrColor)
        }
    }
}