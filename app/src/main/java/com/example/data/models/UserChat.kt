package com.example.data.models

class UserChat(
        val id: String,
        val user: User? = null,
        val lastMessage: ChatMessage? = null
) {
    companion object {
        const val FIELD_LAST_MESSAGE = "lastMessage"
    }
}