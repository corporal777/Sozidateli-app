package com.examle.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EventFormatsModel(
     @SerializedName("totalCount")
     val totalCount: Int? = null,
     val data: List< NewEventFormat>? = null
)

@Parcelize
data class NewEventFormat(
    val id: Int? = null,
    val name: String? = null,
    val order: Int? = null,
    val hasMask : Boolean = false
) : Parcelable