package com.example.data.models

data class NewApiResponse<T>(
        val server: Any? = null,
        val response: T,
        val errors: List<Errors>? = null
)