package com.example.data.models

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class EventRegisterField(
        @SerializedName("field_id")
        val id: String,
        val name: String?,
        val sort: Int,
        val type: Type,
        val required: Boolean,
        val description: String?,
        val values: List<String>?
) {

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