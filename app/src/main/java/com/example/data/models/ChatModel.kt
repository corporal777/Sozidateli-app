package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ChatModel(
        val id: Int,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val type: String? = null,
        @SerializedName("unreadMessagesCount")
        val unreadMessagesCount: Int? = null,
        @SerializedName("invitedUser")
        val invitedUser: ChatInvitedUserModel? = null,
        val binds: ChatBinds? = null
) {

        fun isEventChat(): Boolean =
                type == "event"

        fun isWaitForAcceptInvites(): Boolean =
                invitedUser?.status == "pending"

        fun isInInvites(myId: Int): Boolean =
                invitedUser?.id == myId && invitedUser?.status == "pending"

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
        }
}

data class ChatInvitedUserModel(
        val id: Int? = null,
        val status: String? = null
)

data class ChatBinds(
        val users: List<UserDetail>? = null,
        val event: EventNew? = null,
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
        var acknowledge: List<MessageAcknowledgeModel>? = null
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