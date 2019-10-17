package com.example.ui.event.schedule

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.SubEvent
import com.example.data.models.SubEventCheckLast
import com.example.data.models.Tag
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.DayHeaderItem
import com.example.holders.SubEventItem
import com.example.holders.TagsHorizontalListItem
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.ui.subevent.SubeventFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_schedule.*

abstract class EventScheduleFragment<P : EventSchedulePresenter> : BaseNestedNavigationFragment(), EventScheduleContract.View {

    abstract var presenter: P

    private val tagsSection = Section()
    private val calendarSection = Section()
    private val daySection = Section()
    private val eventsSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(tagsSection)
        add(calendarSection)
        add(daySection)
        add(eventsSection)
    }

    private val onSubEventClickListener = object : SubEventItem.OnSubEventClickListener {
        override fun onSubEventClick(subEvent: SubEvent) {
            presenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: SubEvent) {
            presenter.onAddToScheduleClick(subEvent)
        }

        override fun onRemoveFromScheduleClick(subEvent: SubEvent) {
            presenter.onRemoveFromScheduleClick(subEvent)
        }
    }

    private var calendarItem: CalendarHorizontalListItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            val layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
            this.layoutManager = layoutManager
            if (itemDecorationCount == 0) {
                addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
            }
        }
    }

    override fun setTags(tags: List<Tag>?) {
        if (tags == null || tags.isEmpty()) tagsSection.update(emptyList())
        else tagsSection.update(listOf(TagsHorizontalListItem(tags) {
            presenter.onTagSelectedListChange(it)
        }))
    }

    override fun setDays(days: List<EventScheduleCalendarDay>?) {
        if (days == null) {
            calendarSection.update(emptyList())
            calendarItem = null
        } else {
            val item = CalendarHorizontalListItem(days) { presenter.onDaySelected(it) }
            calendarSection.update(listOf(item))
            calendarItem = item
        }
    }

    override fun selectDay(day: EventScheduleCalendarDay) {
        calendarItem?.selectDay(day)
    }

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        calendarItem?.scrollToDay(day)
    }

    override fun setSubEvents(subEvents: List<SubEvent>, selectedTags: List<Tag>) {
        eventsSection.update(subEvents.mapIndexed { index, subEvent ->
            SubEventItem(SubEventCheckLast(subEvent, index == subEvents.size - 1), selectedTags, onSubEventClickListener)
        })
    }

    override fun showEmptyEventPlaceholder() {
        tvMessage.text = getString(R.string.schedule_empty_event_placeholder)
        placeholder.visibility = View.VISIBLE
    }

    override fun showEmptyDayPlaceholder() {
        tvMessage.text = getEmptyDayPlaceholderText()
        placeholder.visibility = View.VISIBLE
    }

    override fun hidePlaceholder() {
        placeholder.visibility = View.GONE
    }

    override fun showCurrentDay(day: EventScheduleCalendarDay) {
        daySection.update(listOf(DayHeaderItem(day)))
    }

    override fun hideCurrentDay() {
        daySection.update(emptyList())
    }

    override fun showSubEvent(eventId: String, subEventId: Int) {
//        val args = SubeventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
//        findParentNavigation().navigate(R.id.subevent_fragment, args)
    }

    override fun showDataFormCacheMessage(cacheDate: String) {
        tvCacheData.apply {
            text = String.format(getString(R.string.schedule_cache_data), cacheDate)
            visibility = VISIBLE
        }
    }

    override fun hideDataFormCacheMessage() {
        tvCacheData.visibility = GONE
    }

    override fun layout() = R.layout.fragment_event_schedule

    abstract fun getEmptyDayPlaceholderText(): String
}
