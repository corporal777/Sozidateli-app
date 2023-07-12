package com.example.data.models

data class EventScheduleCalendarDay(
        val millis: Long,
        val week: Int,
        val dayOfWeek: String,
        val dayOfMonth: Int,
        var hasEvents: Boolean
)

data class EventScheduleDay(
        val date : String,
        val millis: Long,
        val dayOfWeek: String?,
        val dayOfMonth: Int,
        var hasEvents: Boolean
)