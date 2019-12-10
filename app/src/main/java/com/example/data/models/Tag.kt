package com.example.data.models

import java.io.Serializable

sealed class Tag(
        open val id: String,
        open val name: String
) : Serializable {

    var isSelected = false

    class EventTag(id: String, name: String) : Tag(id, name)

    class Group(id: String, name: String) : Tag(id, name)
}