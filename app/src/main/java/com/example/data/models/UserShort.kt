package com.example.data.models

import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName

data class UserShort(
        @SerializedName("user_id")
        val id: Int,
        @SerializedName("user_email")
        val email: String?,
        @SerializedName("user_name")
        val name: String?,
        @SerializedName("user_middle_name")
        val middleName: String?,
        @SerializedName("user_last_name")
        val lastName: String?,
        @SerializedName("user_avatar")
        val avatar: String?,
        @SerializedName("default_event")
        val event: Event?,
        @SerializedName("notification_unread")
        val notificationsUnreadCount: Int,
        @SerializedName("notification_inapp")
        val inapps: List<RemoteNotification>,
        @SerializedName("user_status")
        val status: User.Status?
) {
    fun toUser() = User().apply {
        user_id = id
        user_email = email
        user_name = name ?: ""
        user_last_name = lastName ?: ""
        user_middle_name = middleName ?: ""
        user_avatar = avatar
        default_event = event
        notification_unread = notificationsUnreadCount
        user_status = status
    }
}