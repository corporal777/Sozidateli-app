package com.example.ui.event.my.schedule.items

import com.example.data.models.EventActivityModel

data class MyScheduleEventsData(
    val firstDate: String,
    val eventId: String,
    val eventName: String,
    val eventImage: String,
    val subEvents: Map<String, List<EventActivityModel>>,
    val showPlaceholder : Boolean,
)
