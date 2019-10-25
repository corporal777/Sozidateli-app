package com.example.data.models

import com.google.gson.annotations.SerializedName

data class RegisterEventField(
        @SerializedName("field_id")
        val id: String,
        val name: String?,
        val sort: Int,
        val type: Type,
        val required: Boolean,
        val description: String?,
        val values: List<String>?,
        @SerializedName("right_file")
        val rightFile: Document?,
        @SerializedName("right_file_description")
        val rightFileDescription: String?
) {

    enum class Type {
        @SerializedName("string")
        STRING,
        @SerializedName("number")
        NUMBER,
        @SerializedName("date")
        DATE,
        @SerializedName("datetime")
        DATETIME,
        @SerializedName("checkbox")
        CHECKBOX,
        @SerializedName("selectbox")
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
        PASSPORT
    }
}