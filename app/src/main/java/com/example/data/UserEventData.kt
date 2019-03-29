package com.example.data

import com.example.data.models.Event
import com.example.data.models.EventCategory
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.EventTag

class UserEventData {

    var event: Event? = null
    var days: List<EventScheduleCalendarDay>? = null
    var tags: List<EventTag>? = null
    var categories: List<EventCategory>? = null

    fun clear() {
        event = null
        days = null
        tags = null
        categories = null
    }
}