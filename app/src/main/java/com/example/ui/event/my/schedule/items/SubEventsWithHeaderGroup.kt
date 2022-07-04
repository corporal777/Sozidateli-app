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
    val id: String,
    val title: String?,
    val image: String?,
    val date: String,
    val listEvents: List<EventActivityModel>,
    val onHeaderClick: (id: String) -> Unit,
    val onSubEventClickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()
    private val mHeaderItem = Section()
    private val mNoParamTitle = "По данным параметрам нет событий"

    init {
        if (title == "No Event") {
            mHeaderItem.add(EventActivityDateItem(date))
            mDataItem.add(NoSubEventItem(mNoParamTitle))
            add(mHeaderItem)
            add(mDataItem)
        } else {
            mHeaderItem.apply {
                if (firstItem) {
                    update(listOf(EventActivityDateItem(date), EventImageHeaderItem(title, image) {
                        onHeaderClick(id)
                    }))
                } else {
                    update(listOf(EventActivityDateItem(date)))
                }
            }
            add(mHeaderItem)
            mDataItem.update(
                listEvents.map { data ->
                    EventActivityItem(id, data, emptyList(), onSubEventClickListener, false)
                }
            )
            add(mDataItem)
        }


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
