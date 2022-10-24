package com.example.util.qr_generator.style

import com.example.util.qr_generator.QrSerializersModule
import com.example.util.qr_generator.SerializationProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

import kotlinx.serialization.modules.polymorphic

import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun interface QrColorSeparatePixels : QrColor {

    override fun invoke(i: Int, j: Int, width: Int, height: Int): Int

    @Serializable
    data class Random(
        val colors : Map<Int, Float>
    ) : QrColorSeparatePixels {

        private val sorted = colors.toList().sortedBy { it.second }
        private val sum = colors.values.sum()

        override fun invoke(i: Int, j: Int, width: Int, height: Int): Int {
            if (colors.isEmpty())
                return 0
            val random = kotlin.random.Random.nextFloat() * sum

            var cSum = 0f
            for ((k,v) in sorted){
                cSum += v
                if (cSum > random)
                    return k
            }
            return sorted.last().first
        }
    }
    companion object : SerializationProvider {

        @ExperimentalSerializationApi
        override val defaultSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
            SerializersModule {
                polymorphic(QrColor::class) {
                    subclass(Random::class)
                }
            }
        }
    }
}
