package com.example.holders

import android.graphics.Color
import com.example.data.models.EventScheduleDay
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class EventDaysListItem (
    id: Int?,
    days: List<EventScheduleDay>,
    private val onDaySelect: (date: EventScheduleDay) -> Unit
) : HorizontalListItem<GroupieViewHolder>(id?.toLong() ?: 0) {


    private val items = days.map { day -> EventDayItem(day, onDaySelect) }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply { update(items) }
    }

    init {
        adapter = groupAdapter
        backgroundColor = Color.WHITE
        isScrollingEnabled = false
    }

    fun selectDay(day: EventScheduleDay) {
        items.find { it.day == day }?.let {
            if (!it.isDaySelected) {
                it.isDaySelected = true
                it.notifyChanged()
            }
        }
        deselectAllExcept(day)
    }

    private fun deselectAllExcept(except: EventScheduleDay) {
        items.filter { x -> x.isDaySelected  }.forEach {
            if (it.day != except) {
                it.isDaySelected = false
                it.notifyChanged()
            }
        }
    }

    fun changeDay(day: String?){
        items.find { it.day.date == day }?.let {
            if (!it.isDaySelected) {
                it.isDaySelected = true
                it.notifyChanged()
            }
        }
        items.filter { x -> x.isDaySelected  }.forEach {
            if (it.day.date != day) {
                it.isDaySelected = false
                it.notifyChanged()
            }
        }
    }

    fun isHasDay(day: String?): Boolean {
        return items.any { x -> x.day.date == day }
    }

    fun getFirstItem(): EventScheduleDay {
        return items[0].day
    }
}