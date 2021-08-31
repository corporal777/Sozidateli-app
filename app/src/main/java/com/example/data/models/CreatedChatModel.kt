package com.example.data.models

import com.google.gson.annotations.SerializedName

data class CreatedChatModel(
        val id: Int,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val type: String? = null,
        @SerializedName("invitedUser")
        val invitedUser: ChatInvitedUserModel? = null
)