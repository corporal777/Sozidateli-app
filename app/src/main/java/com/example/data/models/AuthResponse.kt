package com.example.data.models

data class AuthResponse(
    val id: Int? = null,
    val token: String? = null,
    val errors: List<Errors>? = null
)

data class SnAuthResponse(
    val accessData : AuthResponse?,
    val personalData : SnUserData?
)