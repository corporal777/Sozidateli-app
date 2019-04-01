package com.example.holders

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.EventScheduleCalendarDay
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class CalendarHorizontalListItem(
        days: List<EventScheduleCalendarDay>,
        private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : HorizontalListItem<ViewHolder>() {

    private val items = days.map { day ->
        DayItem(day, onDaySelect)
    }

    init {
        adapter = GroupAdapter<ViewHolder>().apply { addAll(items) }
        backgroundColor = Color.WHITE
    }

    fun selectDay(day: EventScheduleCalendarDay) {
        items.find { it.day == day }?.let {
            if (!it.isSelected) {
                it.isSelected = true
                it.notifyChanged()
            }
        }

        deselectAllExcept(day)
    }

    fun scrollToDay(day: EventScheduleCalendarDay) {
        val position = items.indexOfFirst { it.day == day }
        if (position == RecyclerView.NO_POSITION) return
        scrollToPositionWithOffset(position, 0)
    }

    private fun deselectAllExcept(except: EventScheduleCalendarDay) {
        items.forEach {
            if (it.day != except && it.isSelected) {
                it.isSelected = false
                it.notifyChanged()
            }
        }
    }
}