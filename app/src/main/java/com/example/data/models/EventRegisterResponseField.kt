package com.example.data.models

import com.google.gson.JsonElement

data class EventRegisterResponseField(
        var field_id: String,
        var field_type: String,
        var value: Any
)