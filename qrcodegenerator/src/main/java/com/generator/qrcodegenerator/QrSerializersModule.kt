package com.generator.qrcodegenerator

import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
val QrSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
    SerializersModuleFromProviders(QrOptions, QrData)
}