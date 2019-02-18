package com.example.data.models

data class ChatStartResponse(
        var success:Boolean = false,
        var chat_id: Int = -1,
        var user_id: Int = -1
)