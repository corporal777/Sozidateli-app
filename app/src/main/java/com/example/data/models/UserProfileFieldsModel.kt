package com.example.data.models

import com.google.gson.annotations.SerializedName

data class UserProfileFieldsModel(
        @SerializedName("fields")
        val fields: List<UserProfileFields>? = null
)

data class UserProfileFields(
        val name: String? = null,
        val value: Boolean? = false
)