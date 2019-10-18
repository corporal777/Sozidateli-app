package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Document(
        @SerializedName("file_id")
        val id: Int,
        val public_date: String?,
        val mime: String?,
        val filename: String?,
        val description: String?,
        val file: String?,
        @SerializedName("filesize")
        val fileSize: Int?
)