package com.example.ui.event.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.extensions.*
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.NoDataItem
import com.example.holders.TagsHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragment
import com.example.ui.event.activities.items.*
import com.example.ui.search.SearchInterface
import com.example.ui.subevent.SubEventFragmentArgs
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activitys.*
import kotlinx.android.synthetic.main.fragment_map_new.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.ArrayList


class ActivitiesFragment : BaseFragment(), ActivitiesContract.View {

    @InjectPresenter
    lateinit var presenter: ActivitiesPresenter

    @Inject
    lateinit var presenterProvider: Provider<ActivitiesPresenter>

    private var mDy = 0
    private var mAppBarScrollValue = 0f
    var mCanChangeDay = false

    @ProvidePresenter
    fun providePresenter(): ActivitiesPresenter = presenterProvider.get().apply {
        eventId = ActivitiesFragmentArgs.fromBundle(requireArguments()).eventId.toString()
        tagsNew = ActivitiesFragmentArgs.fromBundle(requireArguments()).tags?.map {
            val t = Tag.EventTag(it.id, it.name)
            t.isSelected = it.isSelected
            t
        }
    }

    private val searchSection = Section()
    private val tagsSection = Section()
    private val calendarSection = Section()
    private val daySection = Section()
    private val eventsSection = Section()

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(searchSection)
            add(tagsSection)
            add(eventsSection)
        }
    }

    private val calendarAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(calendarSection)
        }
    }


    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {

        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
            presenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) {
            presenter.onAddToScheduleClick(subEvent)
        }

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
            presenter.onRemoveFromScheduleClick(subEvent)
        }

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {

        }

    }


    override fun layout(): Int = R.layout.fragment_activitys

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            adapter = groupAdapter
            val mLayoutManager = this.layoutManager as LinearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    mDy += dy
                    val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        if (mDy <= 0) {
                            changeDay(presenter.getFirstDay())
                            calendarPager?.setCurrentItem(0, true)
                        } else {

                            val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                            val now =
                                createCalendarDay(defaultServerDateFormatter.parse(item?.date).time)
                            changeDayWhenScrollDown(now)

                            if (!mCanChangeDay) {
                                if (!recyclerView.canScrollVertically(1)) {
                                    changeDay(presenter.getLastDay())
                                } else
                                    changeDay(now)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    mCanChangeDay = newState != SCROLL_STATE_DRAGGING
                }
            })
        }
        calendarPager.apply {
            adapter = calendarAdapter
            offscreenPageLimit = 3
        }
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        initCollapseLabel()
    }


    override fun setSchemeButton(scheme: List<String>?) {
        btnGoToScheme.apply {
            isVisible = !scheme.isNullOrEmpty()
            setOnClickListener {

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setTags(tags: List<Tag>?) {
        searchSection.update(listOf(SearchActivityItem({ //presenter.onSearchTextChange(it)
        }, {
            presenter.onSearchTextSubmit(it)
            hideKeyboard()
        })))

        if (tags == null || tags.isEmpty()) tagsSection.update(emptyList())
        else {
            tagsSection.update(listOf(TagsHorizontalListItem(tags, {
                presenter.onTagSelectedListChange()
            }, {
                presenter.onShowAllTagsClick()
            })))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setDays(days: List<EventScheduleCalendarDay>?) {
        val listDays = arrayListOf<EventScheduleCalendarDay>()
        var mCount = 0
        var mCountSize = 0
        days?.map { day ->
            mCount += 1
            mCountSize += 1
            listDays.add(day)
            if (mCount == 7) {
                mCount = 0
                calendarSection.add(CalendarHorizontalListItem(listDays) {
                    presenter.onDaySelected(it)
                })
                listDays.clear()
            } else {
                if (mCountSize == days.size) {
                    calendarSection.add(CalendarHorizontalListItem(listDays) {
                        presenter.onDaySelected(it)
                    })
                }
            }
        }
    }

    override fun selectDay(
        day: EventScheduleCalendarDay
    ) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.selectDay(day)
        }
        deselectAllExcept(day)
    }

    private fun changeDay(day: EventScheduleCalendarDay) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.selectDayNew(day)
        }
    }


    private fun deselectAllExcept(except: EventScheduleCalendarDay) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.deselectAllExcept(except)
        }
    }

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        var mPosition = 0
        Handler().post(Runnable {
            selectDay(day)
            val item =
                calendarSection.findItemBy<CalendarHorizontalListItem> { it.scrollToDay(day) }
            if (item != null) {
                mPosition = calendarSection.getPosition(item)
                calendarPager?.setCurrentItem(mPosition, true)
            }
        })
    }

    private fun changeDayWhenScrollDown(day: EventScheduleCalendarDay) {
        var mPosition = 0
        Handler().post(Runnable {
            val item =
                calendarSection.findItemBy<CalendarHorizontalListItem> { it.changeDay(day) }
            if (item != null) {
                mPosition = calendarSection.getPosition(item)
                calendarPager?.setCurrentItem(mPosition, true)
            }
        })
    }


    override fun setSubEvents(
        canShow: Boolean,
        day: EventScheduleCalendarDay,
        subEvents: Map<String, List<EventActivityModel>>,
        selectedTags: List<Tag>
    ) {
        eventsSection.update(
            subEvents.map {
                SubEventsWithDateItem(
                    presenter.eventId,
                    canShow,
                    it.key,
                    it.value,
                    selectedTags,
                    onSubEventClickListener
                )
            }
        )
        eventsSection.add(SubEventItemEmpty())
        scrollContent(day)
    }


    override fun scrollContent(day: EventScheduleCalendarDay) {
        val mSmoothScroller: SmoothScroller =
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
        val date = defaultServerDateFormatter.format(day.millis)
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDay() == date }
        if (group != null) {
            if (date == group.date) {
                val position = groupAdapter.getAdapterPosition(group)
                val mLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                mSmoothScroller.targetPosition = position
                mLayoutManager.startSmoothScroll(mSmoothScroller)
            }
        }
    }


    override fun updateSubEventsNew(
        canShow: Boolean,
        subEvents: Map<String, List<EventActivityModel>>,
        selectedTags: List<Tag>
    ) {
        subEvents.map {
            val group = eventsSection.findGroupBy<SubEventsWithDateItem> { x -> x.date == it.key }
            if (group != null) {
                val position = eventsSection.getPosition(group)
                eventsSection.remove(group)
                eventsSection.add(
                    position,
                    SubEventsWithDateItem(
                        presenter.eventId,
                        canShow,
                        it.key,
                        it.value,
                        selectedTags,
                        onSubEventClickListener
                    )
                )
            }
        }

    }

    override fun showEmptyEventPlaceholder() {
        showPlaceholder(getString(R.string.schedule_empty_event_placeholder), null)
    }


    private fun showPlaceholder(title: String, description: String?) {
        eventsSection.update(
            listOf(
                NoDataItem(
                    title = title,
                    description = description
                )
            )
        )

    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<EventActivityItem> { it -> it.id == idLong }
            ?.notifyChanged(subEvent)
    }

    override fun showDataFormCacheMessage(cacheDate: String) {
    }

    private fun initCollapseLabel() {
        activitiesAppBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                mAppBarScrollValue = Math.abs(i / appBarLayout.totalScrollRange.toFloat())
                updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
            })
    }

    private fun updateViews(offset: Float) {

        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED -> {
                            tvLabelLarge.apply {
                                visibility = View.VISIBLE
                                alpha = 0F
                                animate().setDuration(500).alpha(1.0f)
                            }
                        }
                        TO_COLLAPSED -> {
                            tvLabelLarge.apply {
                                visibility = View.GONE
                                alpha = 0F
                                animate().setDuration(500).alpha(1.0f)
                            }
                        }
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }
        }
    }

    private var cashCollapseState: Pair<Int, Int>? = null

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("value", mAppBarScrollValue)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val value = savedInstanceState?.getFloat("value")
        updateViews(value ?: 0f)
    }


    fun createCalendarDay(date: Long): EventScheduleCalendarDay {
        val cal = date.calendar()
        return EventScheduleCalendarDay(
            date,
            cal.get(Calendar.WEEK_OF_MONTH),
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ?: "",
            cal.get(Calendar.DAY_OF_MONTH),
            false
        )
    }

    override fun showCurrentDay(day: EventScheduleCalendarDay, daysSize: Int) {
    }


}