package com.generator.qrcodegenerator.style

import com.generator.qrcodegenerator.SerializationProvider
import com.generator.qrcodegenerator.encoder.QrCodeMatrix
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


interface QrLogoPadding {


    val value : Float


    val shouldApplyAccuratePadding : Boolean


    fun apply(
        matrix: QrCodeMatrix,
        logoSize: Int,
        logoPos : Int,
        logoShape: QrLogoShape
    )


    @Serializable
    @SerialName("Empty")
    object Empty : QrLogoPadding {

        override val value: Float
            get() = 0f

        override val shouldApplyAccuratePadding: Boolean
            get() = false

        override fun apply(
            matrix: QrCodeMatrix,
            logoSize: Int,
            logoPos: Int,
            logoShape: QrLogoShape
        ) = Unit
    }

    @Serializable
    @SerialName("Accurate")
    data class Accurate(override val value: Float) : QrLogoPadding {

        override val shouldApplyAccuratePadding: Boolean
            get() = true

        override fun apply(
            matrix: QrCodeMatrix,
            logoSize: Int,
            logoPos: Int,
            logoShape: QrLogoShape
        ) = Unit
    }

    @Serializable
    @SerialName("Natural")
    data class Natural(override val value: Float) : QrLogoPadding {

        override val shouldApplyAccuratePadding: Boolean
            get() = false

        override fun apply(
            matrix: QrCodeMatrix,
            logoSize: Int,
            logoPos : Int,
            logoShape: QrLogoShape,
        ) {
            for (x in 0 until logoSize){
                for (y in 0 until logoSize){
                    if (logoShape.invoke(x, y, logoSize, Neighbors.Empty)){
                        matrix[logoPos+x, logoPos+y] =
                            QrCodeMatrix.PixelType.Logo
                    }
                }
            }
        }
    }

    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        @Suppress("unchecked_cast")
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            SerializersModule {
                polymorphicDefaultSerializer(QrLogoPadding::class){
                    Empty.serializer() as SerializationStrategy<QrLogoPadding>
                }
                polymorphicDefaultDeserializer(QrLogoPadding::class) {
                    Empty.serializer()
                }
                polymorphic(QrLogoPadding::class) {
                    subclass(Empty::class)
                    subclass(Accurate::class)
                    subclass(Natural::class)
                }
            }
        }
    }
}