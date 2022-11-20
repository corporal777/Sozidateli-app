package com.example.ui.chatList.contacts.items

import com.example.data.models.Message

data class UserChatData(
    val id: Int,
    val userId: Int,
    val userName: String,
    val userImage: String,
    val lastMessageId: String,
    val lastMessage: String,
    val lastMessageDate: String,
    val unreadMessagesCount: Int
) {
    fun setLastMessage(type : Message.MessageType) {

    }
}