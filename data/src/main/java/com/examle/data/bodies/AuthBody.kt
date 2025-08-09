package com.examle.data.bodies


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