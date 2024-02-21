package com.example.data.bodies

data class EventCalendarBody(
        val user: Int,
        val entity: EventCalendarBodyEntity
) {
    companion object {
        const val CALENDAR_EVENT = "event"
        const val CALENDAR_EVENT_ACTIVITY = "eventActivity"

        fun toCalendarBody(userId : Int, subEventId : Int?): EventCalendarBody {
            return EventCalendarBody(
                userId, EventCalendarBodyEntity(
                    EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEventId ?: 0
                )
            )
        }
    }
}

data class EventCalendarBodyEntity(
        val type: String,
        val id: Int
)