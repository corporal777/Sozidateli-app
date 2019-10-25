package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventRegisterResponseField(
        @SerializedName("field_id")
        val id: String,
        @SerializedName("field_type")
        val type: RegisterEventField.Type,
        val value: Any?
)