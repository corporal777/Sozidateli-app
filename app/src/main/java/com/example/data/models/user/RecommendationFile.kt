package com.example.data.models.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendationFile(
        var id: Int,
        var type: String?,
        var name: String?,
        var desc: String?,
        var url: String?
) : Parcelable {

    fun getReadableName(): String {
        return desc?.takeIf { it.isNotBlank() } ?: name?.takeIf { it.isNotBlank() } ?: "file"
    }
}