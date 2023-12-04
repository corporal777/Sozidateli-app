package com.example.ui.event.my.schedule.items

import com.example.data.models.EventScheduleData
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventScheduleGroup(
    val data: EventScheduleData,
    val onHeaderClick: (id: String) -> Unit,
    val onSubEventClickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()
    private val mHeaderItem = Section()

    init {
        mHeaderItem.update(listOf(
            EventActivityDateItem(data.titleDate),
            EventImageHeaderItem(
                data.getId(),
                data.getName(),
                data.getImage(),
                data.getBackgroundColor()
            ) { onHeaderClick(data.getId()) }
        ))
        mDataItem.apply {
            data.subEvents.forEach {
                if (it.titleDate != null)
                    add(EventActivityDateItem(it.titleDate, it.titleDate?.uniqueId?.toLong()))
                add(
                    EventActivityItem(
                        data.getId(),
                        it.subEvent,
                        emptyList(),
                        onSubEventClickListener,
                        true
                    )
                )
            }
        }

        mHeaderItem.registerGroupDataObserver(this)
        mDataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mHeaderItem
            1 -> mDataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mHeaderItem -> 0
            mDataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2

}
