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
        @SerializedName("invitedUser")
        val invitedUser: ChatInvitedUserModel? = null,
        val binds: ChatBinds? = null
) {

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
        val bans: List<ChatBanModel>? = null
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
        val acknowledge: List<MessageAcknowledgeModel>? = null
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
        val binds: ChatBanBindsModel? = null
)

data class ChatBanBindsModel(
        val users: UserDetail? = null
)