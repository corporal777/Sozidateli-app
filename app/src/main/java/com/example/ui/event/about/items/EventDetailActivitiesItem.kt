package com.example.ui.event.about.items

import com.example.data.UserEventData
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleDay
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ThreadLocalRandom

class EventDetailActivitiesItem(
    val eventId: String,
    val canShow: Boolean?,
    val date: String,
    val subEvents: List<EventActivityModel>,
    private val clickListener: EventActivityItem.OnEventActivityClickListener
) : NestedGroup() {

    private val mContentItem = Section()
    //private val mDateItem = EventActivityDateItem(createEventScheduleDay(date))
    private val mDateItem = Section()

    init {
        add(mDateItem)
        mContentItem.update(subEvents.map { data ->
            EventActivityItem(eventId, data, emptyList(), clickListener, canShow ?: false)
        })
        add(mContentItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mDateItem
            1 -> mContentItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mDateItem -> 0
            mContentItem -> 1
            else -> -1
        }
    }

    fun updateButtonState(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        mContentItem.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged()
    }

    private fun createEventScheduleDay(date: String?): EventScheduleDay? {
        if (date.isNullOrEmpty()) return null

        val millis = defaultServerDateFormatter.parse(date)?.time ?: 0
        val cal = millis.calendar()
        return EventScheduleDay(
            ThreadLocalRandom.current().nextInt(0, 1000),
            date,
            millis,
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
            cal.get(Calendar.DAY_OF_MONTH),
            true
        )
    }

    override fun getGroupCount() = 2

}