package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class UserProfileFieldsModel(
        @SerializedName("fields")
        val fields: List<UserProfileFields>? = null,
        val state: String? = null
)

data class UserProfileFields(
        val name: String? = null,
        val filled: Boolean? = false,
        @SerializedName("requiredFor")
        val requiredFor: List<String>? = null
)