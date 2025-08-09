package com.examle.data.models

sealed class ChatMessage {
    data class Personal(
            val message: Message,
            val isMyMessage: Boolean
    ) : ChatMessage()

    data class NewMessages(val count: Int) : ChatMessage()
    data class Date(val date: Long) : ChatMessage()
    data class Accept(val message: Message) : ChatMessage()
}