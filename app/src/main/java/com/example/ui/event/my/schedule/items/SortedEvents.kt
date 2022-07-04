package com.example.ui.event.my.schedule.items

import com.example.data.models.EventActivityModel

data class SortedEvents(
    val firstDate: String,
    val eventId: String,
    val eventName: String,
    val eventImage: String,
    val subEvents: List<SortedSubEvents>,
    val showPlaceholder : Boolean,
)

data class SortedSubEvents(
    val date : String,
    val data : List<EventActivityModel>
)