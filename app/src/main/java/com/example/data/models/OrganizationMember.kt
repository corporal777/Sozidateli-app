package com.example.data.models

import com.example.data.models.user.User
import com.google.gson.annotations.SerializedName

data class OrganizationMember(
        @SerializedName("member_id")
        val id: Int,
        val position: String?,
        val user: User
)