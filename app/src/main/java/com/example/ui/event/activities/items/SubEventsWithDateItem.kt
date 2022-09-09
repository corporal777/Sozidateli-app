package com.example.ui.event.activities.items

import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.util.custom.LinkedSet
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import java.util.*

class SubEventsWithDateItem(
    val eventId : String,
    val canShow: Boolean?,
    val date: String,
    val listEvents: List<EventActivityModel>,
    val selectedTags: List<Tag>,
    private val clickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mDataItem = Section()
    private val mId = defaultServerDateFormatter.parse(date).time
    private val mDateItem = EventActivityDateItem(date, mId)

    private val mNoParamTitle = "По данным параметрам нет событий"
    private val mNoSubEvent = "Событий нет"
    private var prevEventNoParam = false

    init {
        add(mDateItem)
        if (!listEvents.isNullOrEmpty()) {
            listEvents.map { data ->
                mDataItem.add(
                    EventActivityItem(
                        eventId,
                        data,
                        selectedTags,
                        clickListener,
                        canShow ?: false
                    )
                )
            }
        }
        add(mDataItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mDateItem
            1 -> mDataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    fun update(newData: Map<String, List<EventActivityModel>>) {

        mDataItem.notifyChanged()
        this.notifyItemChanged(0)
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mDateItem -> 0
            mDataItem -> 1
            else -> -1
        }
    }


    fun updateButtonState(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        mDataItem.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged()
    }

//    fun getDayName(): String? {
//        return mDateItem.findItemBy<EventActivityDateItem> { true }?.getDay()
//    }

    override fun getGroupCount() = 2

}
