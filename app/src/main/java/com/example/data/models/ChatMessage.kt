package com.example.data.models

data class ChatMessage(
        val message: String? = null,
        val senderId: String = "",
        var isMyMessage: Boolean = false
)