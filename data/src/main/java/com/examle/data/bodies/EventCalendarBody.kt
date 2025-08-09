package com.examle.data.bodies

data class EventCalendarBody(
        val user: Int,
        val entity: com.examle.data.bodies.EventCalendarBodyEntity
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