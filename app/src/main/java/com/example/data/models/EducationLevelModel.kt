package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EducationLevelModel(
        val data: List<EducationLevel>?,
        @SerializedName("totalCount")
        val totalCount: Int?
)

data class EducationLevel(
        val id: Int?,
        val name: String,
        val order: Int?
)