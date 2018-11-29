package com.example.data.models

class UserChat(
        var userId: String? = null,
        var chatId: String? = null,
        var user: User?=null,
        var lastMessage: ChatMessage?=null
)