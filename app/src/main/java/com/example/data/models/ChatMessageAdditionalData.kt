package com.example.data.models

import com.google.gson.annotations.SerializedName

data class ChatMessageAdditionalData(
        val id: Int,
        val name: String?,
        @SerializedName("last_name")
        val lastName: String?,
        val avatar: String?
)