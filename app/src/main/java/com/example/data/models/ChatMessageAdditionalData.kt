package com.example.data.models

import com.google.gson.annotations.SerializedName

data class ChatMessageAdditionalData(
        @SerializedName("user_id")
        val id: Int,
        @SerializedName("user_name")
        val name: String?,
        @SerializedName("user_last_name")
        val lastName: String?,
        @SerializedName("user_middle_name")
        val middleName: String?,
        @SerializedName("user_avatar")
        val avatar: String?
)