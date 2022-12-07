package com.example.ui.event.my.schedule.items

import com.example.extensions.findItemBy
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
    private val mNoParamTitle = "По данным параметрам нет событий"

    init {
        mHeaderItem.apply {
            update(listOf(
                EventActivityDateItem(data.firstDate),
                EventImageHeaderItem(data.getName(), data.getImage(), data.getBackgroundColor()) {
                    onHeaderClick(data.getId())
                }
            ))
        }
        add(mHeaderItem)
        mDataItem.apply {
            if (!data.subEvents.isNullOrEmpty()) {
                data.subEvents.forEach { subEvents ->
                    if (!subEvents.key.isNullOrEmpty()) {
                        add(EventActivityDateItem(subEvents.key))
                    }
                    if (!subEvents.value.isNullOrEmpty()) {
                        subEvents.value.forEach {
                            add(
                                EventActivityItem(
                                    data.getId(),
                                    it,
                                    emptyList(),
                                    onSubEventClickListener,
                                    true)
                            )
                        }
                    }
                }
            }
        }
        add(mDataItem)
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
