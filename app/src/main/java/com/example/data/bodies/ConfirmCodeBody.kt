package com.example.data.bodies

data class ConfirmCodeBody(
        val type: String,
        val phone: String,
        val code: String
)