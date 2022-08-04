package com.example.data.bodies

data class AuthBody(
        var login: LoginModel,
        var password: LoginModel,
        var deviceId : String = "",
        var deviceModel : String = "",
        var build : String = "",
        var version : String = ""
)

data class LoginModel(
        val type: String,
        val value: String
)