package com.example.data.models

sealed class ChatListDataItem {
    data class Chat(val userChat: UserChat): ChatListDataItem()
    data class User(val user: Speaker): ChatListDataItem()
    data class Invite(val userChat: UserChat): ChatListDataItem()
    data class Ban(val user: Speaker): ChatListDataItem()
}