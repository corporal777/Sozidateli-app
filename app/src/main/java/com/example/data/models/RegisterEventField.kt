package com.example.data.models

import com.google.gson.JsonElement

data class RegisterEventField(
        var field_id: String,
        var name: String?,
        var sort: Int,
        var type: String,
        var description: String?,
        var values: ArrayList<String>?,
        var dataFromServer: EventRegisterResponseField?
)

enum class FieldType(val code: String) {
    STRING("string"),
    NUMBER("number"),
    DATE("date"),
    DATETIME("datetime"),
    CHECKBOX("checkbox"),
    SELECTBOX("selectbox"),
    FILE("file"),
    SELECTGEO("selectgeo")

}