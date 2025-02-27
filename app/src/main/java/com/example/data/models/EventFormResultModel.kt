package com.example.data.models

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class EventFormResultModel(
    val id: Int? = null,
    @SerializedName("ratingMark")
    val ratingMark: Int? = null,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    val form: Int? = null,
    val user: Int? = null,
    @SerializedName("fio")
    val nameLastName : String? = null,
    var fields: List<EventFormResultFieldModel>? = null
) : Parcelable



@Parcelize
data class EventFormResultDraftModel(
    @SerializedName("draftId")
    val id: Int? = null,
    @SerializedName("userId")
    val user: Int? = null,
    @SerializedName("formId")
    val form: Int? = null,
    @SerializedName("draft")
    val fields: List<EventFormResultFieldModel>? = null
) : Parcelable