package com.example.data.models

data class EventTag(
        val id: Int,
        val name: String
) : Tag {
    override fun getTagId() = id

    override fun getTagName() = name
}