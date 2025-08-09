package com.examle.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BannedUsersModel(
        val id: Int,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val user: Int? = null,
        val binds: BannedUsersBindsModel? = null
): Parcelable {
        companion object {
                const val BANNED_SORT_TYPE = "sortType"
                const val BANNED_LIMIT = "limit"
                const val BANNED_OFFSET = "offset"
                const val BANNED_BINDS = "binds"
        }
}

@Parcelize
data class BannedUsersBindsModel(
    val user: UserDetail? = null,
    val event: EventResponse? = null,
    @SerializedName("last-unread-message")
        val lastUnreadMessage: MessageModel? = null
): Parcelable