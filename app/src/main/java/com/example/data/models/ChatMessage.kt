package com.example.data.models

import ru.houseofapps.chat.models.Message

sealed class ChatMessage {
    data class Personal(
            val message: Message,
            val isMyMessage: Boolean
    ) : ChatMessage()

    data class Service(
            val type: Type
    ) : ChatMessage() {
        enum class Type {
            NO_TYPE, NEW_MESSAGES, ACCEPT
        }
    }
}