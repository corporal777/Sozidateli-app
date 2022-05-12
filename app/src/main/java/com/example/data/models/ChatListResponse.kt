package com.example.data.models

import com.example.data.models.user.User

data class ChatListResponse(
        val chats: List<UserChat>,
        val favorites: List<User>
)