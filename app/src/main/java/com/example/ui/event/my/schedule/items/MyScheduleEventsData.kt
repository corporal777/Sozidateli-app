package com.example.ui.event.my.schedule.items

import com.example.data.models.EventActivityModel
import java.util.*
import kotlin.collections.ArrayList

data class MyScheduleEventsData(
    val firstDate: String,
    val eventId: String,
    val eventName: String,
    val eventImage: String,
    var subEvents: Map<String, List<EventActivityModel>>
)
