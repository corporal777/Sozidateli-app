package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Page(
        val id: Int,
        @SerializedName("event_id")
        val eventId: String,
        @SerializedName("is_file_page")
        val isFilePage: Boolean,
        val menu: String,
        val title: String,
        val subtitle: String,
        val content: String,
        val sort: Int,
        val picture: String,
        val files: List<Document>
)