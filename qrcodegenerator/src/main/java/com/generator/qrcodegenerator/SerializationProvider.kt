package com.generator.qrcodegenerator

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule

interface SerializationProvider {

    val defaultSerializersModule : SerializersModule
}

@ExperimentalSerializationApi
fun serializersModuleFromProviders(vararg provider : SerializationProvider) =
    SerializersModule {
        provider.forEach {
            include(it.defaultSerializersModule)
        }
    }