package com.example.holders

import android.graphics.Color
import androidx.annotation.BoolRes
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.EventScheduleCalendarDay
import com.example.ui.event.activities.items.CalendarGroup
import com.example.ui.event.activities.items.HorizontalListItemNew
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class CalendarHorizontalListItem(
    days: List<EventScheduleCalendarDay>,
    private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : HorizontalListItemNew<GroupieViewHolder>() {


    private val items = days.map { day ->
        DayItem(day, onDaySelect)
    }


    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply { addAll(items) }
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

    fun changeDay(day: EventScheduleCalendarDay) : Boolean{
        var isDay = false
        items.find { it.day == day }?.let {
            isDay = true
        }
        return isDay
    }

    fun scrollToDay(day: EventScheduleCalendarDay): Boolean {
        var isDay = false
        items.find { it.day == day }?.let {
            isDay = true

        }
//        val position = items.indexOfFirst { it.day == day }
//
//        if (position == RecyclerView.NO_POSITION) return
//        scrollToPositionWithOffset(position, 0)
        return isDay
    }

    fun deselectAllExcept(except: EventScheduleCalendarDay) {
        items.forEach {
            if (it.day != except && it.isSelected) {
                it.isSelected = false
                it.notifyChanged()
            }
        }
    }


    fun selectDayNew(day: EventScheduleCalendarDay) {
        items.find { it.day.millis == day.millis }?.let {
            if (!it.isSelected) {
                it.isSelected = true
                it.notifyChanged()
            }
        }
        deselectAllExceptNew(day)
    }


    fun deselectAllExceptNew(except: EventScheduleCalendarDay){
        items.forEach {
            if (it.day.dayOfMonth != except.dayOfMonth && it.isSelected) {
                it.isSelected = false
                it.notifyChanged()
            }
        }
    }
}