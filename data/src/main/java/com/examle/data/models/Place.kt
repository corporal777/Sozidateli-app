package com.examle.data.models

import android.graphics.Bitmap
import android.net.Uri
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
    var image: String?,
    @SerializedName("int_scheme_descriptions")
    val description: String?,
    var bitmap: Bitmap? = null
) : Parcelable