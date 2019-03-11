package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Speaker(
        val id: Int,
        @SerializedName("user_id")
        val uid: Int,
        val name: String,
        val position: String?,
        val description: String?,
        val photo: String?,
        @SerializedName("is_user_in_favorite")
        var isInFavorite: Boolean
):Parcelable