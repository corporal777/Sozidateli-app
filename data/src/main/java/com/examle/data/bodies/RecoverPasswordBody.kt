package com.examle.data.bodies

data class RecoverPasswordBody(
    val type: String,
    val code: String,
    val password: String,
    val id: String
)

data class RecoverPasswordResponse(
    val userId : String
)