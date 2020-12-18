package com.example.data.models.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendationFile(
        var id: Int,
        var type: String? = null,
        var name: String? = null,
        var desc: String? = null,
        var url: String? = null,
        var newName: String? = null
) : Parcelable {

    fun getReadableName(): String {
        return desc?.takeIf { it.isNotBlank() } ?: name?.takeIf { it.isNotBlank() } ?: "file"
    }
}