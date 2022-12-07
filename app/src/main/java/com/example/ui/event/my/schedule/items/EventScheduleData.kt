package com.example.ui.event.my.schedule.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import parseColor

data class EventScheduleData(
    val event : EventNew?,
    val firstDate: String,
    var subEvents: Map<String, List<EventActivityModel>>
) {
    fun getId() = event?.id.toString()
    fun getName() = event?.name
    fun getImage() = event?.image?.uri
    fun getBackgroundColor(): String? {
        return event?.backgroundColor?.value
    }
}