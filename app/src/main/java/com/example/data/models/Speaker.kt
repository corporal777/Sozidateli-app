package com.example.data.models

import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName

data class Speaker(
        val id: Int,
        @SerializedName("user_id")
        val uid: String,
        @SerializedName("event_id")
        val eventId: String,
        @SerializedName("activity_id")
        val activityId: String,
        val description: String?,
        val user: User
)