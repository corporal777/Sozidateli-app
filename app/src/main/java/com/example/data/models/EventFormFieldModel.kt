package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Locale


@Parcelize
data class EventFormFieldModel(
    val id: Int,
    val name: String? = null,
    val isRequired: Boolean,
    val sort: Int? = null,
    val description: String? = null,
    val type: Type? = null,
    val parameters: FieldsParameters? = null
) : Parcelable {

    enum class Type {
        @SerializedName("email")
        EMAIL,

        @SerializedName("phone")
        PHONE,

        @SerializedName("site")
        SITE,

        @SerializedName("prefilled")
        PREFILLED,

        @SerializedName("string")
        STRING,

        @SerializedName("separator")
        SEPARATOR,

        @SerializedName("number")
        NUMBER,

        @SerializedName("date")
        DATE,

        @SerializedName("datetime")
        DATETIME,

        @SerializedName("checkbox")
        CHECKBOX,

        @SerializedName("list")
        SELECT_BOX,

        @SerializedName("radiobox")
        RADIO_BOX,

        @SerializedName("file")
        FILE,

        @SerializedName("textarea")
        TEXT_AREA,

        @SerializedName("boolean")
        BOOLEAN,

        @SerializedName("passport")
        PASSPORT,

        @SerializedName("group")
        GROUP,

        @SerializedName("checkboxes")
        CHECKBOXES,

        @SerializedName("datetimeplaned")
        DATETIMEPLANED
    }
}


@Parcelize
data class FieldsParameters(
    @SerializedName("allowedExtensions")
    val extensions: List<String>? = null,
    val options: List<String>? = null
) : Parcelable {

}