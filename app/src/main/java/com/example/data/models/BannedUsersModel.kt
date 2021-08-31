package com.example.data.models

import com.google.gson.annotations.SerializedName

data class BannedUsersModel(
        val id: Int,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val user: Int? = null,
        val binds: BannedUsersBindsModel? = null
) {
        companion object {
                const val BANNED_SORT_TYPE = "sortType"
                const val BANNED_LIMIT = "limit"
                const val BANNED_OFFSET = "offset"
                const val BANNED_BINDS = "binds"
        }
}

data class BannedUsersBindsModel(
        val user: UserDetail? = null,
        val event: EventNew? = null,
        @SerializedName("last-unread-message")
        val lastUnreadMessage: MessageModel? = null
)