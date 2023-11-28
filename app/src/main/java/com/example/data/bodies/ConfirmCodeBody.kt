package com.example.data.bodies

data class ConfirmCodeBody(
        val phone: String,
        val code: String,
        val type: String = "personal",
)