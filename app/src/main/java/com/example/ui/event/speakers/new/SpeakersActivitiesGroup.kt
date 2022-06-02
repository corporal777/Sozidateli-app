package com.example.ui.event.speakers.new

import android.util.Log
import com.example.data.models.EventActivityModel
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.event.activities.items.NoSubEventItem
import com.example.ui.event.my.items.NoEventItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class SpeakersActivitiesGroup(
    private val noActivitiesTitle : String,
    private val list: List<EventActivityModel>,
    val clickListener : EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()

    init {
        if (!list.isNullOrEmpty()) {
            val listActivitiesMap =
                list.groupBy { it.holdingDate?.from?.split(" ")?.get(0) }
            listActivitiesMap.map { map ->
                val date = EventActivityDateItem(map.key)
                mDataItem.add(date)
                map.value.forEach { data ->
                    mDataItem.add(EventActivityItem(data, null, clickListener))
                }
            }
        } else {
            mDataItem.add(NoSubEventItem(noActivitiesTitle))
        }

        add(mDataItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mDataItem
            //1 -> mDataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mDataItem -> 0
            //mDataItem -> 1
            else -> -1
        }
    }


    fun updateButtonState(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        mDataItem.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged(subEvent)
    }

    override fun getGroupCount() = 1
}