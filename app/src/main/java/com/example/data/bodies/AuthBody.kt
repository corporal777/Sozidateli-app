package com.example.data.bodies

data class AuthBody(
        var login: LoginModel,
        var password: LoginModel
)

data class LoginModel(
        val type: String,
        val value: String
)