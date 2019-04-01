package com.example.ui.event.schedule

import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.Tag
import com.example.holders.*
import com.example.ui.base.BaseNestedNavigationFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_event_schedule.*

abstract class EventScheduleFragment<P : EventSchedulePresenter> : BaseNestedNavigationFragment(), EventScheduleContract.View {

    abstract var presenter: P

    private val tagsSection = Section()
    private val calendarSection = Section()
    private val daySection = Section()
    private val eventsSection = PagedListGroup<SubEventItem>()
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        add(tagsSection)
        add(calendarSection)
        add(daySection)
        add(eventsSection)
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
        if (tags == null) tagsSection.update(emptyList())
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

    override fun setSubEvents(subEvents: PagedList<SubEventItem>) {
        eventsSection.submitList(subEvents)
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

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_event_schedule

    abstract fun getEmptyDayPlaceholderText(): String
}
