package com.example.ui.event.schedule

import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.Tag
import com.example.holders.*
import com.example.ui.base.BaseNestedNavigationFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_event_activity.*
import javax.inject.Inject
import javax.inject.Provider

class EventScheduleFragment : BaseNestedNavigationFragment(), EventScheduleContract.View {

    @InjectPresenter
    lateinit var presenter: EventSchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<EventSchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): EventSchedulePresenter = presenterProvider.get()

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
        daySection.update(listOf(DayHeaderItem(day)))
    }

    override fun setSubEvents(subEvents: PagedList<SubEventItem>) {
        eventsSection.submitList(subEvents)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_event_activity
}
