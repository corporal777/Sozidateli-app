package com.example.data.models

data class NewAuthResponse(
        val id: Int? = null,
        val token: String? = null,
        val errors: List<Errors>? = null
)