package com.example.data.models

import com.example.ui.event.activities.SubEventsData

data class EventScheduleData(
    val event : EventNew?,
    val titleDate: String?,
    val eventDates : List<String>,
    var subEvents: List<SubEventsData>
) {
    fun getId() = event?.id.toString()
    fun getName() = event?.name
    fun getImage() = event?.image?.uri
    fun getBackgroundColor(): String? {
        return event?.backgroundColor?.value
    }
}