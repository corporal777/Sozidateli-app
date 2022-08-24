package com.example.ui.event.my.schedule.items

import android.util.Log
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.event.activities.items.NoSubEventItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class SubEventGroup(
    val eventId: String,
    val date: String,
    val list: List<EventActivityModel>,
    private val selectedTags: List<Tag>?,
    val clickListener: EventActivityItem.OnEventActivityClickListener?,
    private val canShow: Boolean,
) : NestedGroup() {

    private val listData = arrayListOf<EventActivityModel>()
    private val mEventSection = Section()
    private val mDateItem = EventActivityDateItem(date)

    init {
        listData.addAll(list)
        add(mDateItem)
        listData.forEach {
            mEventSection.add(
                EventActivityItem(
                    eventId,
                    it,
                    emptyList(),
                    clickListener,
                    canShow
                )
            )
        }

        add(mEventSection)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mDateItem
            1 -> mEventSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mDateItem -> 0
            mEventSection -> 1
            else -> -1
        }
    }


    fun removeSubEventItem(id: String) {
        listData.forEachIndexed { index, event ->
            if (event.id.toString() == id) {
                listData.removeAt(index)
            }
        }
        Log.e("SIZE", listData.size.toString())
        if (listData.isNullOrEmpty()) {
            this.remove(mDateItem)
            this.remove(mEventSection)
        } else {
            mEventSection.update(
                listData.map {
                    EventActivityItem(
                        eventId,
                        it,
                        emptyList(),
                        clickListener,
                        canShow
                    )
                }
            )
        }
    }

    fun isHasEvent(id: Long): Boolean {
        val item = mEventSection.findItemBy<EventActivityItem> { x -> x.id == id }
        return item != null
    }

    override fun getGroupCount() = 2

}