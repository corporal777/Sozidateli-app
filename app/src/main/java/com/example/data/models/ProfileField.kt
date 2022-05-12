package com.example.data.models

import java.util.*

data class ProfileField(
        var nameField: String,
        var label: String? = null,
        var data: Any? = null,
        var required: Boolean = false,
        var type: Type = Type.TEXT,
        var id: String = UUID.randomUUID().toString(),
        var isValid:Boolean = true
) {
    constructor(type: Type, nameField: String, label: String? = null, required: Boolean = false, data: Any? = null) : this(nameField, label, data, required, type)
}

enum class Type(val code: Int) {
    EMAIL(1),
    TEXT(2),
    PASSWORD(3),
    PHONE(4),
    DATE(5),
    SWITCH(6),
    SUPPORT(7)
}