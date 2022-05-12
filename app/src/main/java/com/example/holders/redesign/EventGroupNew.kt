package com.example.holders.redesign

import com.example.data.models.*
import com.example.holders.EventStatusItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventGroupNew(
    data : EventNew?,
    eventClickListener: EventStatusItem.OnEventClickListener,

) : NestedGroup() {
    private val eventStatusItem = EventItemNew(
        data,
        eventClickListener,
    )

    init {
        eventStatusItem.registerGroupDataObserver(this)
        //dataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> eventStatusItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            eventStatusItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 1
}