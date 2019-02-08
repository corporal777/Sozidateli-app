package com.example.data.models

data class ProfileField(
        var nameField: String,
        var nameFieldIsShowOnlyProfile:String?=null,
        var type: Type,
        var label: String? = null,
        var isShowOnlyProfile: Boolean = false,
        var isOnlyProfile:Boolean = false,
        var data: Any?
)

enum class Type(val code: Int) {
    EMAIL(1),
    TEXT(2),
    PASSWORD(3),
    PHONE(4),
    DATE(5)
}