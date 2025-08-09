package com.examle.data.bodies

data class ConfirmCodeBody(
        val phone: String,
        val code: String,
        val type: String = "personal",
)