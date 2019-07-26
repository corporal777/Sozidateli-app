package com.example.data.models

import ru.houseofapps.chat.models.Message

sealed class ChatMessage {
    data class Personal(
            val message: Message,
            val isMyMessage: Boolean
    ) : ChatMessage()

    object NewMessages : ChatMessage()
    data class Date(val date: Long) : ChatMessage()
    data class Accept(val message: Message) : ChatMessage()
}