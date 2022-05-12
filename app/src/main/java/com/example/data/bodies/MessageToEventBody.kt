package com.example.data.bodies

data class MessageToEventBody(
        val message: String,
        val user: Int,
        val event: Int
)