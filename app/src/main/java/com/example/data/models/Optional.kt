package com.example.data.models

data class Optional<T>(
        val value: T?
)

fun <T> T?.asOptional() = Optional(this)