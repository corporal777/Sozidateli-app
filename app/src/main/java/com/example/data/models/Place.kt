package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Place(
        @SerializedName("event_place_id")
        val id: String,
        @SerializedName("event_id")
        val event: String,
        val name: String,
        @SerializedName("int_scheme")
        val image: String,
        @SerializedName("int_scheme_descriptions")
        val description: String
)