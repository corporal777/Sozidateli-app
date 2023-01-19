package com.generator.qrcodegenerator

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule

interface SerializationProvider {

    @ExperimentalSerializationApi
    val defaultSerializersModule : SerializersModule
}

@ExperimentalSerializationApi
fun SerializersModuleFromProviders(vararg provider : SerializationProvider) =
    SerializersModule {
        provider.forEach {
            include(it.defaultSerializersModule)
        }
    }