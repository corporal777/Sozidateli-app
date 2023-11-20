package com.generator.qrcodegenerator.vector.style

import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.serializersModuleFromProviders
import com.generator.qrcodegenerator.style.toColor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

interface IQrVectorColors {
    val dark : QrVectorColor
    val light : QrVectorColor
    val ball : QrVectorColor
    val frame : QrVectorColor
}

/**
 * Colors of QR code elements
 */
@Serializable
data class QrVectorColors(
    override val dark : QrVectorColor = QrVectorColor.Solid(0xff000000.toColor()),
    override val light : QrVectorColor = QrVectorColor.Solid(0),
    override val ball : QrVectorColor = QrVectorColor.Unspecified,
    override val frame : QrVectorColor = QrVectorColor.Unspecified
) : IQrVectorColors {

    companion object : SerializationProvider {
        @ExperimentalSerializationApi
        override val defaultSerializersModule: SerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            serializersModuleFromProviders(QrVectorColor)
        }
    }
}