package com.example.data.database.converters

import com.google.gson.Gson
import java.lang.reflect.Type

open class Converter {

    protected fun <T> toJson(value: T?, type: Type): String? = value?.let { Gson().toJson(value, type) }

    protected fun <T> fromJson(json: String?, type: Type): T? = json?.let { Gson().fromJson(it, type) }

    protected fun <T> fromJsonList(json: String?, type: Type): List<T>? = json?.let { Gson().fromJson(it, type) }
}