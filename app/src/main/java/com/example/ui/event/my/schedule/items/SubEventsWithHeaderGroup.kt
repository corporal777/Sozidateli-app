package com.example.ui.event.my.schedule.items

import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.event.activities.items.NoSubEventItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class SubEventsWithHeaderGroup(
    val firstItem: Boolean,
    val title: String?,
    val image: String?,
    val date: String,
    val listEvents: List<EventActivityModel>
) : NestedGroup() {

    private val mDataItem = Section()
    private val mHeaderItem = Section()

    init {
        mHeaderItem.apply {
            if (firstItem) {
                update(listOf(EventActivityDateItem(date), EventImageHeaderItem(title, image)))
            } else {
                update(listOf(EventActivityDateItem(date)))
            }
        }
        add(mHeaderItem)
        mDataItem.update(
            listEvents.map { data ->
                EventActivityItem(data, emptyList(), null)
            }
        )
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
