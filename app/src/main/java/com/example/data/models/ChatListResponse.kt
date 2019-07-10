package com.example.data.models

data class ChatListResponse(
        val chats: List<UserChat>,
        val favorites: List<Speaker>
)