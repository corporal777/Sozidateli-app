package com.example.data.models

import com.google.gson.annotations.SerializedName

data class UnreadCountResponse(
        @SerializedName("notification_unread")
        val unreadCount: Int
)