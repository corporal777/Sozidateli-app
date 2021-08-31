package com.example.ui.event.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.extensions.findItemBy
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.subevent.SubeventFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activitys.*
import javax.inject.Inject
import javax.inject.Provider

class ActivitiesFragment: BaseFragment(), ActivitiesContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: ActivitiesPresenter

    @Inject
    lateinit var presenterProvider: Provider<ActivitiesPresenter>

    @ProvidePresenter
    fun providePresenter(): ActivitiesPresenter = presenterProvider.get().apply {
        eventId = ActivitiesFragmentArgs.fromBundle(requireArguments()).eventId.toString()
    }

    private val tagsSection = Section()
    private val calendarSection = Section()
    private val daySection = Section()
    private val eventsSection = Section()
    private val allSection = Section()
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(calendarSection)
            add(allSection)
            add(tagsSection)
            add(daySection)
            add(eventsSection)
        }
    }

    private val onSubEventClickListener = object : SubEventItem.OnSubEventClickListener {
        override fun onSubEventClick(subEvent: EventActivityModel) {
            presenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) {
            presenter.onAddToScheduleClick(subEvent)
        }

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
            presenter.onRemoveFromScheduleClick(subEvent)
        }

        override fun onChangeFavoriteClick(subEvent: EventActivityModel) {
            // do nothing
        }
    }

    private var calendarItem: CalendarHorizontalListItem? = null

    override fun layout(): Int = R.layout.fragment_activitys

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("tags_fragment") { _, bundle ->
            presenter.tagsNew = bundle.getParcelableArrayList<NewTags>("tags")?.map {
                val t = Tag.EventTag(it.id, it.name)
                t.isSelected = it.isSelected
                t
            }
        }
        setFragmentResultListener("all_actions") { _, bundle ->
            if (bundle.getBoolean("isUpdate", false)) {
                presenter.getEventData()
            }
        }
    }

    override fun setTags(tags: List<Tag>?) {
        if (tags == null || tags.isEmpty()) tagsSection.update(emptyList())
        else {
            tagsSection.update(listOf(TagsHorizontalListItem(tags, {
                presenter.onTagSelectedListChange()
            }, {
                presenter.onShowAllTagsClick()
            })))
        }
    }

    override fun setDays(days: List<EventScheduleCalendarDay>?) {
        calendarItem = if (days == null) {
            calendarSection.update(emptyList())
            null
        } else {
            if (days.size > 1) {
                val item = CalendarHorizontalListItem(days) { presenter.onDaySelected(it) }
                calendarSection.update(listOf(item))
                allSection.update(listOf(AllActivitiesItem {
                    findNavController().navigate(ActivitiesFragmentDirections.actionActivitiesFragmentToAllActivitiesFragment(presenter.eventId.toInt()))
                }))
                item
            } else {
                calendarSection.update(emptyList())
                null
            }
        }
    }

    override fun selectDay(day: EventScheduleCalendarDay) {
        calendarItem?.selectDay(day)
    }

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        calendarItem?.scrollToDay(day)
    }

    override fun setSubEvents(subEvents: List<EventActivityModel>, selectedTags: List<Tag>) {
        eventsSection.update(subEvents.map { subEvent ->
            SubEventItem(subEvent, SubEventItem.Mode.SCHEDULE, onSubEventClickListener, presenter.canDoActions)
        })
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

    override fun showCurrentDay(day: EventScheduleCalendarDay, daysSize: Int) {
        daySection.update(listOf(DayHeaderItem(day.millis, daysSize != 1)))
    }

    override fun hideCurrentDay() {
        daySection.update(emptyList())
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubeventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subevent_fragment, args)
    }

    override fun showDataFormCacheMessage(cacheDate: String) {
        //        tvCacheData.apply {
//            text = String.format(getString(R.string.schedule_cache_data), cacheDate)
//            visibility = VISIBLE
//        }
    }

    override fun hideDataFormCacheMessage() {
        //tvCacheData.visibility = View.GONE
    }

    override fun showAllTags() {
        findNavController().navigate(ActivitiesFragmentDirections.actionActivitiesFragmentToEventTagsFragment().setTags(presenter.getTagsList().toTypedArray()))
        //findNavController().navigate(R.id.event_tags_fragment, bundleOf("tags" to presenter.getTagsList()))
    }

    override fun updateSubevent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<SubEventItem> { it -> it.id == idLong }?.notifyChanged()
    }

    fun getEmptyDayPlaceholderText(): String = getString(R.string.schedule_my_empty_day_placeholder_title)
    fun getEmptyDayPlaceholderDescription(): String? = getString(R.string.schedule_my_empty_day_placeholder_description)

    override val title: CharSequence?
        get() = getString(R.string.program)
}