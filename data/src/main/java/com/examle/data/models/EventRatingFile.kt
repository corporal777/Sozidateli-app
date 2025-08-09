package com.examle.data.models

import com.google.gson.annotations.SerializedName

data class EventRatingFile(
        val id: Int,
        @SerializedName("event_id")
        val eventId: Int,
        @SerializedName("file_name")
        val fileName: String?,
        @SerializedName("file_mime_type")
        val fileMimeType: String?,
        @SerializedName("file_size")
        val fileSize: Int?,
        @SerializedName("file_path")
        val filePath: String?,
        @SerializedName("file_link")
        val fileLink: String?
)