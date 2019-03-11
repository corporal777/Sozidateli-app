package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Speaker(
        val id: Int,
        @SerializedName("user_id")
        val uid: Int,
        val name: String,
        val position: String,
        val description: String,
        val photo: String,
        @SerializedName("is_user_in_favorite")
        val isInFavorite: Boolean?
)