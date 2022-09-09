package com.example.ui.event.schedule

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.*
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.ui.base.BaseFragment
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.subevent.items.SubEventItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_schedule.*

abstract class EventScheduleFragment<P : EventSchedulePresenter> : BaseFragment(), EventScheduleContract.View {

    abstract var presenter: P

    private val tagsSection = Section()
    private val calendarSection = Section()
    private val daySection = Section()
    private val eventsSection = Section()
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(ScreenLabelItem(getTitle()))
            add(tagsSection)
            add(calendarSection)
            add(daySection)
            add(eventsSection)
        }
    }

    private val onSubEventClickListener = object : SubEventItem.OnSubEventClickListener {
        override fun onSubEventClick(subEvent: /*SubEvent*/EventActivityModel) {
            presenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: /*SubEvent*/EventActivityModel) {
            presenter.onAddToScheduleClick(subEvent)
        }

        override fun onRemoveFromScheduleClick(subEvent: /*SubEvent*/EventActivityModel) {
            presenter.onRemoveFromScheduleClick(subEvent)
        }

        override fun onChangeFavoriteClick(subEvent: /*SubEvent*/EventActivityModel) {
            // do nothing
        }
    }

    private var calendarItem: CalendarHorizontalListItem? = null

    override fun layout() = R.layout.fragment_event_schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun setTags(tags: List<Tag>?) {
        if (tags == null || tags.isEmpty()) tagsSection.update(emptyList())
        else tagsSection.update(listOf(TagsHorizontalListItem(tags, {
            presenter.onTagSelectedListChange()
        })))
    }

    override fun setDays(days: List<EventScheduleCalendarDay>?) {
        calendarItem = if (days == null) {
            calendarSection.update(emptyList())
            null
        } else {
            val item = CalendarHorizontalListItem(days) { presenter.onDaySelected(it) }
            calendarSection.update(listOf(item))
            item
        }
    }

    override fun selectDay(day: EventScheduleCalendarDay) {
        calendarItem?.selectDay(day)
    }

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        calendarItem?.scrollToDay(day)
    }

    override fun setSubEvents(subEvents: List</*SubEvent*/EventActivityModel>, selectedTags: List<Tag>) {
        eventsSection.update(subEvents.map { subEvent ->
            SubEventItem(subEvent, SubEventItem.Mode.SCHEDULE, onSubEventClickListener)
        })
    }

    override fun updateSubevent(subEvent: /*SubEvent*/EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<SubEventItem> { it -> it.id == idLong }?.notifyChanged()
    }

    override fun showEmptyEventPlaceholder() {
        showPlaceholder(getString(R.string.schedule_empty_event_placeholder), null)
    }

    override fun showEmptyDayPlaceholder() {
        showPlaceholder(getEmptyDayPlaceholderText(), getEmptyDayPlaceholderDescription())
    }

    private fun showPlaceholder(title: String, description: String?) {
        eventsSection.update(listOf(NoDataItem(
                title = title,
                description = description
        )))
    }

    override fun hidePlaceholder() = Unit

    override fun showCurrentDay(day: EventScheduleCalendarDay) {
        daySection.update(listOf(DayHeaderItem(day.millis)))
    }

    override fun hideCurrentDay() {
        daySection.update(emptyList())
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun showAllTags() {
        findNavController().navigate(R.id.event_tags_fragment)
    }

    override fun showDataFormCacheMessage(cacheDate: String) {
//        tvCacheData.apply {
//            text = String.format(getString(R.string.schedule_cache_data), cacheDate)
//            visibility = VISIBLE
//        }
    }

    override fun hideDataFormCacheMessage() {
        tvCacheData.visibility = GONE
    }

    abstract fun getTitle(): String
    abstract fun getEmptyDayPlaceholderText(): String
    abstract fun getEmptyDayPlaceholderDescription(): String?
}
