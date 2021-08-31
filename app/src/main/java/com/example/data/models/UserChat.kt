package com.example.data.models

import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName
import ru.houseofapps.chat.models.Message

data class UserChat(
        val id: Int,
        @SerializedName("user_recepient")
        var user: /*User*/UserDetail,
        val created: String,
        @SerializedName("last_message")
        val lastMessage: String?,
        @SerializedName("last_message_datetime")
        val lastMessageDate: String?,
        @SerializedName("last_message_type")
        val lastMessageType: Message.Type?,
        @SerializedName("last_message_user_id")
        val lastMessageSender: Int?,
        @SerializedName("user_sender")
        var userSender: User?,
        @SerializedName("last_message_id")
        var messageId: String?,
        @SerializedName("user_recepient_in_favorite")
        var inFavorite: Boolean,
        @SerializedName("chat_is_in_invites")
        var isInInvites: Boolean,
        @SerializedName("chat_wait_accept_invite")
        var isWaitForAcceptInvites: Boolean,
        @SerializedName("chat_banned_by_recipient")
        var isBannedByRecipient: Boolean,
        @SerializedName("chat_banned_by_you")
        var isBannedByYou: Boolean,
        @SerializedName("is_event_chat")
        var isEventChat: Boolean,
        @SerializedName("event_id")
        var eventId: String?,
        var unreadMessageCount: Int = 0
)