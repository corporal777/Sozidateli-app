package com.examle.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventFormModel(
    val id: Int? = null,
    val event: Int? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val type: Type? = null,
    val background: BackgroundType,
    val files: List<FileModel>? = null,
    val fields: List<EventFormFieldModel>? = null
) : Parcelable {
    enum class Type {
        @SerializedName("participation")
        PARTICIPATION,

        @SerializedName("rating")
        RATING
    }

    enum class BackgroundType {
        @SerializedName("event")
        EVENT,

        @SerializedName("organization")
        ORGANIZATION
    }
}