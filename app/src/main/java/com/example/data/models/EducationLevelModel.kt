package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EducationLevelModel(
        val data: List<EducationLevel>?,
        @SerializedName("totalCount")
        val totalCount: Int?
)

@Parcelize
data class EducationLevel(
        val id: Int?,
        val name: String,
        val order: Int?
): Parcelable