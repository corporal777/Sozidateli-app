package com.example.ui.event.my.schedule.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.ui.event.activities.SubEventsData
import parseColor

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