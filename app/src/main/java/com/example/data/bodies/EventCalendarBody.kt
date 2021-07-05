package com.example.data.bodies

data class EventCalendarBody(
        val user: Int,
        val entity: EventCalendarBodyEntity
) {
    companion object {
        const val CALENDAR_EVENT = "event"
        const val CALENDAR_EVENT_ACTIVITY = "eventActivity"
    }
}

data class EventCalendarBodyEntity(
        val type: String,
        val id: Int
)