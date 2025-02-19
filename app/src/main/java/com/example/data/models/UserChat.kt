package com.example.data.models

import com.example.data.models.user.User
import com.example.ui.notification.NotificationsSortedData
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
            val sender =
                if (!chatModel.binds?.lastMessage?.acknowledge.isNullOrEmpty())
                    chatModel.binds?.lastMessage?.acknowledge?.get(0)?.user
                else 0
            return UserChatModel(
                id = chatModel.id,
                user = createUser(chatModel, id),
                eventId = chatModel.binds?.event?.id.toString(),
                lastMessageId = chatModel.binds?.lastMessage?.id.toString(),
                lastMessage = chatModel.binds?.lastMessage?.message,
                lastMessageDate = chatModel.binds?.lastMessage?.createdDate,
                lastMessageType = if (chatModel.binds?.lastMessage?.file == null) Message.MessageType.TEXT else Message.MessageType.IMAGE,
                lastMessageSender = sender,
                unreadMessageCount = chatModel.unreadMessagesCount ?: 0
            )
        }

        private fun createUser(it: ChatModel, id: Int): UserChatSender {
            return if (it.isEventChat()) {
                UserChatSender(
                    id = it.binds?.event?.id ?: 0,
                    name = it.binds?.event?.name ?: "",
                    image = it.binds?.event?.image?.uri ?: "",
                )
            } else {
                UserChatSender(
                    id = it.binds?.users?.firstOrNull { us -> us.id != id }?.id ?: 0,
                    name = it.binds?.users?.firstOrNull { us -> us.id != id }?.nameLastName ?: "",
                    image = it.binds?.users?.firstOrNull { us -> us.id != id }?.loadUserImage() ?: "",
                )

            }
        }
    }
}

data class UserChatSender(
    val id : Int,
    val name : String,
    val image : String
)