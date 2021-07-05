package com.example.data.models

data class EventCalendarItem(
        val id: Int,
        val user: Int? = null,
        val date: DateModel? = null,
        val entity: EventEntity? = null
)

data class EventEntity(
        val type: String? = null,
        val id: Int
)