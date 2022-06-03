package com.example.ui.event.activities.items

import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.util.custom.LinkedSet
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import java.util.*

class SubEventsWithDateItem(
    private val emptyEventTitle : String,
    private val noEventWithParamsTitle : String,
    subEvents: Map<String, LinkedList<EventActivityModel>>,
    val selectedTags : List<Tag>,
    private val clickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()

    init {
        if (!subEvents.isNullOrEmpty()) {
            subEvents.map {
                mDataItem.add(EventActivityDateItem(it.key))
                if (!it.value.isNullOrEmpty()){
                    it.value.forEach { data ->
                        if (data.mNoEvent){
                            mDataItem.add(NoSubEventItem(noEventWithParamsTitle))
                        }else {
                            mDataItem.add(EventActivityItem(data, selectedTags, clickListener))
                        }

                    }
                }else {
                    mDataItem.add(NoSubEventItem(emptyEventTitle))
                }

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
            //mTagsItem -> 0
            mDataItem -> 0
            else -> -1
        }
    }


    fun updateButtonState(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        mDataItem.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged()
    }

    fun getDayName() : String? {
        return mDataItem.findItemBy<EventActivityDateItem> { true }?.getDay()
    }

    override fun getGroupCount() = 1

}
