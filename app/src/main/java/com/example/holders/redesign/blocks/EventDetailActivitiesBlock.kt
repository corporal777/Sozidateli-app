package com.example.holders.redesign.blocks

import android.util.Log
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventTagModel
import com.example.data.models.Tag
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.chat.`ChatContract$View$$State`
import com.example.ui.event.activities.items.NoSubEventItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item

class EventDetailActivitiesBlock(
    val date: String,
    val list: ArrayList<EventActivityModel>,
    private val clickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()

    private var mListTags = arrayListOf<Tag>()
    //private val mTagsItem = EventDetailTagsBlock(eventData) {}

    init {
        mDataItem.apply {
            add(EventActivityDateItem(date))
            list.map { data ->
                add(EventActivityItem(data, emptyList(), clickListener))
            }
        }

        add(mDataItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            //0 -> mTagsItem
            0 -> mDataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            // mTagsItem -> 0
            mDataItem -> 0
            else -> -1
        }
    }

    fun updateButtonState(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        mDataItem.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged()
    }

    override fun getGroupCount() = 1

}