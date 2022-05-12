package com.example.holders.redesign.blocks

import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventTagsListItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailActivitiesBlock(
    subEvents: List<EventActivityModel>,
    private val clickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()

    //private val mTagsItem = EventTagsListItem(eventData?.binds?.tag!!, {onTagCLick(it)})

    init {
        //add(mTagsItem)

        if (!subEvents.isNullOrEmpty()) {
            val listMap = subEvents.groupBy { it.holdingDate?.from?.split(" ")?.get(0) }
            var mSize = 4
            run breaking@{
                listMap.map {
                    if (mSize == 0){
                        return@breaking
                    }
                    mDataItem.add(EventActivityDateItem(it.key))
                    it.value.forEach { data ->
                        if (mSize != 0){
                            mDataItem.add(EventActivityItem(data,null, clickListener))
                            mSize -= 1
                        }else {
                            return@breaking
                        }
                    }
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

    override fun getGroupCount() = 1

}