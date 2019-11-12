package com.example.data.models

sealed class Tag(
        open val id: String,
        open val name: String
) {

    var isSelected = false

    class EventTag(id: String, name: String) : Tag(id, name)

    class Group(id: String, name: String) : Tag(id, name)
}