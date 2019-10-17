package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Partner(
        val id: Int,
        @SerializedName("event_id")
        val event: Int,
        val logo: String?,
        @SerializedName("bg_image")
        val background: String?,
        val name: String?
) : Parcelable