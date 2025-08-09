package com.examle.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ChatModel(
        val id: Int,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        val type: String? = null,
        @SerializedName("unreadMessagesCount")
        val unreadMessagesCount: Int? = null,
        val users: List<UsersModel>? = null,
        val binds: ChatBinds? = null
) {

        fun isEventChat(): Boolean =
                type == "event"

        fun isWaitForAcceptInvites(myId: Int): Boolean {
                val me = users?.firstOrNull { it.user == myId }
                val opponent = users?.firstOrNull { it.user != myId }
                return me?.status == "accepted" && opponent?.status == "pending"
                //return invitedUser?.status == "pending"
        }

        fun isInInvites(myId: Int): Boolean {
                val me = users?.firstOrNull { it.user == myId }
                val opponent = users?.firstOrNull { it.user != myId }
                return opponent?.status == "accepted" && me?.status == "pending"
               //return invitedUser?.id == myId && invitedUser?.status == "pending"
        }

        fun isBannedByYou(myId: Int) : Boolean =
                binds?.bans?.firstOrNull { it.createdBy == myId } != null

        fun isBannedByRecipient(myId: Int) : Boolean =
                binds?.bans?.firstOrNull { it.user == myId } != null

        companion object {
                const val CHAT_SORT = "sortType"
                const val CHAT_LIMIT = "limit"
                const val CHAT_OFFSET = "offset"
                const val CHAT_BINDS = "binds"
                const val CHAT_ID = "id"
                const val CHAT_TYPE = "type"
                const val CHAT_INVITED_USER = "invitedUser"
                const val CHAT_INVITED_USER_STATUS = "invitedUserStatus"
                const val CHAT_USER_STATUS = "userStatus"
                const val CHAT_USER = "user"
                const val CHAT_SHOW_EVENTS = "showEventRooms"
        }
}

@Parcelize
data class UsersModel(
        val user: Int? = null,
        val status: String? = null
): Parcelable

data class ChatInvitedUserModel(
        val id: Int? = null,
        val status: String? = null
)

data class ChatBinds(
        val users: List<UserDetail>? = null,
        val event: EventResponse? = null,
        @SerializedName("last-unread-message")
        val lastUnreadMessage: MessageModel? = null,
        val bans: List<ChatBanModel>? = null,
        @SerializedName("last-message")
        val lastMessage: MessageModel? = null
        //val rights
)

@Parcelize
data class MessageModel(
        val id: Int,
        val chat: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val message: String? = null,
        val file: FileModel? = null,
        var acknowledge: List<MessageAcknowledgeModel>? = null,
        val sender: MessageSender? = null,
        val event: MessageEventData? = null
): Parcelable {
        companion object {
                const val MESSAGES_SORT_TYPE = "sortType"
                const val MESSAGES_LIMIT = "limit"
                const val MESSAGES_OFFSET = "offset"
                const val MESSAGES_CHAT = "chat"
                const val MESSAGES_START_FROM = "startFrom"
                const val MESSAGES_ENDS_BY = "endsBy"
                const val MESSAGES_ACKNOWLEDGED_STATE = "acknowledgedState"
                const val MESSAGES_ACKNOWLEDGED_BY = "acknowledgedBy"
        }
}

@Parcelize
data class MessageEventData(
        val name: String,
        val url: String
): Parcelable

@Parcelize
data class MessageSender(
        val id: Int,
        val name: String? = null,
        @SerializedName("lastName")
        val lastName: String? = null,
        @SerializedName("middleName")
        val middleName: String? = null,
        val avatar: String? = null
): Parcelable

@Parcelize
data class MessageAcknowledgeModel(
        val user: Int,
        val state: Boolean
): Parcelable

data class ChatBanModel(
        val id: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val user: Int? = null,
        val binds: ChatBanBindsModel? = null,
        val chat: Int? = null
)

data class ChatBanBindsModel(
        val users: UserDetail? = null
)