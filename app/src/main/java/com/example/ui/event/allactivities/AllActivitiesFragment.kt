package com.example.ui.event.allactivities

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.extensions.*
import com.example.holders.DayAllHeaderItem
import com.example.holders.DayHeaderItem
import com.example.ui.subevent.items.SubEventItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.subevent.SubeventFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activitys.*
import javax.inject.Inject
import javax.inject.Provider

class AllActivitiesFragment: BaseFragment(), AllActivitiesContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: AllActivitiesPresenter

    @Inject
    lateinit var presenterProvider: Provider<AllActivitiesPresenter>

    @ProvidePresenter
    fun providePresenter(): AllActivitiesPresenter = presenterProvider.get().apply {
        eventId = AllActivitiesFragmentArgs.fromBundle(requireArguments()).eventId.toString()
    }

    override fun layout(): Int = R.layout.fragment_activitys

    private val daySection = Section()
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(daySection)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun setSubEventsData(subEvents: List<EventActivityModel>, day: List<EventScheduleCalendarDay>) {
        daySection.update(
                day.map { dat ->
                    val subEvent = subEvents.let {
                        it.filter { event ->
                            val date = defaultServerDateTimeFormatter.parse(event.holdingDate?.from)
                            date.time >= dat.millis.startOfDay() && date.time <= dat.millis.endOfDay()
                        }
                    }
                    DayAllHeaderItem(dat.millis, true, subEvent, presenter.canDoActions, onSubEventClickListener)
                }
        )
    }

    override fun setSubEvents(subEvents: List<EventActivityModel>) {
        /*eventsSection.update(subEvents.map { subEvent ->
            SubEventItem(subEvent, SubEventItem.Mode.SCHEDULE, onSubEventClickListener, presenter.canDoActions)
        })*/
    }

    override fun showEmptyEventPlaceholder() {
        showPlaceholder(getString(R.string.schedule_empty_event_placeholder), null)
    }

    override fun showEmptyDayPlaceholder() {
        showPlaceholder(getEmptyDayPlaceholderText(), getEmptyDayPlaceholderDescription())
    }

    private fun showPlaceholder(title: String, description: String?) {
        /*eventsSection.update(listOf(NoDataItem(
                title = title,
                description = description
        )))*/
    }

    override fun hidePlaceholder() = Unit

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

    override fun showCurrentDay(day: EventScheduleCalendarDay) {
        daySection.update(listOf(DayHeaderItem(day.millis)))
    }

    override fun updateSubevent(subEvent: EventActivityModel, date: Long) {
        val idLong = subEvent.id?.toLong()
        daySection.findItemBy<DayAllHeaderItem> { it -> it.id == idLong }?.notifyChanged()
    }

    fun isUpdate() = presenter.isUpdate

    fun getEmptyDayPlaceholderText(): String = getString(R.string.schedule_my_empty_day_placeholder_title)
    fun getEmptyDayPlaceholderDescription(): String? = getString(R.string.schedule_my_empty_day_placeholder_description)

    override val title: CharSequence?
        get() = getString(R.string.all_activities)
}