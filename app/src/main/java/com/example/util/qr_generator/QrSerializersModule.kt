package com.example.util.qr_generator

import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
val QrSerializersModule by lazy(LazyThreadSafetyMode.NONE) {
    SerializersModuleFromProviders(QrOptions, QrData)
}