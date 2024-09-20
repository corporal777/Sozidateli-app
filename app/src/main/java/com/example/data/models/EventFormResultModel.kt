package com.example.data.models

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

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
    var fields: List<EventFormResultFieldsModel>? = null
) : Parcelable

@Parcelize
data class EventFormResultFieldsModel(
    val id: Int? = null,
    val value: @RawValue JsonElement? = null,
    val fields : @RawValue JsonElement? = null
) : Parcelable {

    fun createData(type : EventRegisterField.Type?): EventRegisterResponseField {
        return EventRegisterResponseField(
            id?.toString() ?: "",
            type ?: EventRegisterField.Type.STRING,
            if (type == EventRegisterField.Type.PREFILLED ) fields else value
        )
    }
}


@Parcelize
data class EventFormResultDraftModel(
    @SerializedName("draftId")
    val id: Int? = null,
    @SerializedName("userId")
    val user: Int? = null,
    @SerializedName("formId")
    val form: Int? = null,
    @SerializedName("draft")
    val fields: List<EventFormResultFieldsModel>? = null
) : Parcelable {
    companion object {
        const val EVENT_FORM_RESULT_FORM_ID = "form"
        const val EVENT_FORM_RESULT_USER_ID = "user"
    }
}