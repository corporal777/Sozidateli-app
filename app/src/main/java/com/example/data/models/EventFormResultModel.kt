package com.example.data.models

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class EventFormResultModel(
        val id: Int? = null,
        @SerializedName("ratingMark")
        val ratingMark: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        val form: Int? = null,
        val user: Int? = null,
        val fields: List<EventFormResultFieldsModel>? = null
) {
        companion object {
            const val EVENT_FORM_RESULT_FORM_ID = "form"
            const val EVENT_FORM_RESULT_USER_ID = "user"
        }
}

data class EventFormResultFieldsModel(
    val id: Int? = null,
    val value: JsonElement? = null
)