package com.examle.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class EducationLevelModel(
    val data: List<EducationLevel>?,
    val totalCount: Int?
)

@Parcelize
data class EducationLevel(
    val id: Int?,
    val name: String,
    val order: Int?
): Parcelable