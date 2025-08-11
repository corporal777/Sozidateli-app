package com.examle.data.models

import com.google.gson.annotations.SerializedName

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
    val lastMessageType: Message.MessageType?,
    @SerializedName("last_message_user_id")
    val lastMessageSender: Int?,
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

data class UserChatModel(
    val id: Int,
    var user: UserChatSender,
    var eventId: String?,
    var lastMessageId: String?,
    var lastMessage: String?,
    var lastMessageDate: String?,
    var lastMessageType: Message.MessageType?,
    val lastMessageSender: Int?,
    var unreadMessageCount: Int = 0
) {
    companion object {
        fun createFromChatModel(chatModel: ChatModel, id: Int): UserChatModel {
            val sender = chatModel.binds?.lastMessage?.acknowledge?.find { x -> x.user != id }?.user ?: 0
            val user = if (chatModel.isEventChat()) {
                UserChatSender(
                   chatModel.binds?.event?.id ?: 0,
                    chatModel.binds?.event?.name ?: "",
                    chatModel.binds?.event?.image?.uri ?: "",
                )
            } else {
                UserChatSender(
                    chatModel.binds?.users?.firstOrNull { it.id != id }?.id ?: 0,
                    chatModel.binds?.users?.firstOrNull { it.id != id }?.nameLastName ?: "",
                    chatModel.binds?.users?.firstOrNull { it.id != id }?.loadUserImage() ?: "",
                )

            }
            return UserChatModel(
                id = chatModel.id,
                user = user,
                eventId = chatModel.binds?.event?.id.toString(),
                lastMessageId = chatModel.binds?.lastMessage?.id.toString(),
                lastMessage = chatModel.binds?.lastMessage?.message,
                lastMessageDate = chatModel.binds?.lastMessage?.createdDate,
                lastMessageType = if (chatModel.binds?.lastMessage?.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                lastMessageSender = sender,
                unreadMessageCount = chatModel.unreadMessagesCount ?: 0
            )
        }
    }
}

data class UserChatSender(
    val id : Int,
    val name : String,
    val image : String
)