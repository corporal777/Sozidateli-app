package com.example.ui.event.my.schedule.items

import com.example.data.models.EventActivityModel
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.event.about.redesign.items.EventDetailActivitiesBlock
import com.example.ui.event.activities.items.NoSubEventItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class NewItemTest(
    val data: SortedEvents,
    val onHeaderClick: (id: String) -> Unit,
    val onSubEventClickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()
    private val mHeaderItem = Section()
    private val mNoParamTitle = "По данным параметрам нет событий"

    init {
        mHeaderItem.apply {
            if (data.showPlaceholder) {
                update(
                    listOf(
                        EventActivityDateItem(data.firstDate),
                    )
                )
            } else {
                update(listOf(
                    EventActivityDateItem(data.firstDate),
                    EventImageHeaderItem(data.eventName, data.eventImage) {
                        onHeaderClick(data.eventId ?: "")
                    }
                ))
            }
        }
        add(mHeaderItem)
        mDataItem.apply {
            if (data.showPlaceholder) {
                add(NoSubEventItem(mNoParamTitle))
            } else {
                if (!data.subEvents.isNullOrEmpty()) {
                    data.subEvents.forEach { subEvents ->
                        if (!subEvents.date.isNullOrEmpty()) {
                            add(EventActivityDateItem(subEvents.date))
                        }
                        if (!subEvents.data.isNullOrEmpty()) {
                            subEvents.data.forEach {
                                add(
                                    EventActivityItem(
                                        data.eventId,
                                        it,
                                        emptyList(),
                                        onSubEventClickListener,
                                        false
                                    )
                                )
                            }
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
