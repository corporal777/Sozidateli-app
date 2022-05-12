package com.example.ui.event.activities.items

import android.graphics.Color
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.extensions.findItemBy
import com.example.holders.DayItem
import com.example.holders.HorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class CalendarGroup(
    days: List<EventScheduleCalendarDay>,
    private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : HorizontalListItem<GroupieViewHolder>() {


    private val items = days.map { day ->
        //DayItem(day, onDaySelect)
    }


    init {
       // adapter = GroupAdapter<GroupieViewHolder>().apply { addAll(items) }
        backgroundColor = Color.WHITE
    }

    fun selectDay(day: EventScheduleCalendarDay) {
//        items.find { it.day == day }?.let {
//            if (!it.isSelected) {
//                it.isSelected = true
//                it.notifyChanged()
//            }
//        }

        deselectAllExcept(day)
    }

    fun scrollToDay(day: EventScheduleCalendarDay) {
//        val position = items.indexOfFirst { it.day == day }
//        if (position == RecyclerView.NO_POSITION) return
//        scrollToPositionWithOffset(position, 0)
    }

    private fun deselectAllExcept(except: EventScheduleCalendarDay) {
//        items.forEach {
//            if (it.day != except && it.isSelected) {
//                it.isSelected = false
//                it.notifyChanged()
//            }
//        }
    }
}