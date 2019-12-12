package com.example.data.models

import java.io.Serializable

sealed class Tag(
        open val id: String,
        open val name: String
) : Serializable {

    var isSelected = false

    class EventTag(id: String, name: String) : Tag(id, name)

    class Group(id: String, name: String) : Tag(id, name)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tag) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (isSelected != other.isSelected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + isSelected.hashCode()
        return result
    }
}