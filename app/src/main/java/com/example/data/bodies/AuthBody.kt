package com.example.data.bodies

data class AuthBody(
        var login: LoginModel,
        var password: LoginModel,
        var deviceId : String = ""
)

data class LoginModel(
        val type: String,
        val value: String
)