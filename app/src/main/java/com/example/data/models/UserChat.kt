package com.example.data.models

import com.example.data.models.user.User

class UserChat(
        val id: String,
        val user: User? = null,
        val lastMessage: ChatMessage? = null
) {
    companion object {
        const val FIELD_LAST_MESSAGE = "lastMessage"
    }
}