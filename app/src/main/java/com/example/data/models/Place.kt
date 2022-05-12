package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
        @SerializedName("event_place_id")
        val id: String,
        @SerializedName("event_id")
        val event: String,
        val name: String?,
        @SerializedName("int_scheme")
        val image: String?,
        @SerializedName("int_scheme_descriptions")
        val description: String?
) : Parcelable