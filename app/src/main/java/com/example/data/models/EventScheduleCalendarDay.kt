package com.example.data.models

data class EventScheduleCalendarDay(
        val millis: Long,
        val dayOfWeek: String,
        val dayOfMonth: Int,
        val hasEvents: Boolean
)