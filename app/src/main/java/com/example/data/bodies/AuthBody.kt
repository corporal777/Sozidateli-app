package com.example.data.bodies

data class AuthBody(
    var login: LoginModel,
    var password: LoginModel,
    var deviceId: String = "",
    var deviceModel: String = "",
    var build: String = "",
    var version: String = "",
    var tempToken: String = ""
)

data class LoginModel(
    val type: String,
    val value: String
)

data class VKAuthBody(
    val accessToken: String,
    val uuid: String,
    val deviceId: String = "",
    val deviceModel: String = "",
    val build: String = "",
    val version: String = ""
)

data class FcmTokenBody(
    val token : String
)