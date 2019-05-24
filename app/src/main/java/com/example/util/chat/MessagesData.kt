package com.example.util.chat

import ru.houseofapps.chat.models.Message

class MessagesData(
        val action: Action,
        val position: Int,
        val messages: List<Message>
)

enum class Action {
    INSERT, UPDATE, REMOVE
}