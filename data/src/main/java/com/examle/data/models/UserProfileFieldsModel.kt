package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class UserProfileFieldsModel(
        @SerializedName("fields")
        val fields: List<UserProfileFields>,
        val state: String
)

data class UserProfileFields(
        val name: String,
        val filled: Boolean,
        @SerializedName("requiredFor")
        val requiredFor: List<String>
)