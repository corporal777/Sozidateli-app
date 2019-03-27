package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organization(
        val id: Int,
        val name: String?,
        val description: String?,
        val logo: String?,
        val bg_image: String?,
        val status: String?,
        @SerializedName("is_user_subscribed")
        val isSubscribed: Boolean?,
        @SerializedName("is_user_in_favorite")
        val isInFavorite: Boolean?
):Parcelable