package com.example.data.models

import java.util.*

data class ProfileField(
        var type:Type,
        var nameField: String,
        var label: String? = null,
        var data: Any?=null,
        var id:String = UUID.randomUUID().toString()
)

enum class Type(val code: Int) {
    EMAIL(1),
    TEXT(2),
    PASSWORD(3),
    PHONE(4),
    DATE(5),
    SWITCH(6),
    SUPPORT(7)
}