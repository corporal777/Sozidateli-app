package com.example.ui.event.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.NoDataItem
import com.example.holders.TagsHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragment
import com.example.ui.event.activities.items.CalendarHorizontalListPager
import com.example.ui.event.activities.items.SubEventsWithDateItem
import com.example.ui.search.SearchInterface
import com.example.ui.subevent.SubeventFragmentArgs
import com.example.util.custom.LinkedSet
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activitys.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ActivitiesFragment : BaseFragment(), ActivitiesContract.View, SearchInterfaceProvider {

    @InjectPresenter
    lateinit var presenter: ActivitiesPresenter

    @Inject
    lateinit var presenterProvider: Provider<ActivitiesPresenter>
    private var mIsCurrentPageItem = 0
    private var mIsCurrentPageDay: EventScheduleCalendarDay? = null

    private val searchInterface = SearchInterface()
    private var mDy = 0
    private var mAppBarScrollValue = 0f
    var mCount = 0

    @ProvidePresenter
    fun providePresenter(): ActivitiesPresenter = presenterProvider.get().apply {
        eventId = ActivitiesFragmentArgs.fromBundle(requireArguments()).eventId.toString()
        tagsNew = ActivitiesFragmentArgs.fromBundle(requireArguments()).tags?.map {
            val t = Tag.EventTag(it.id, it.name)
            t.isSelected = it.isSelected
            t
        }
        searchInterface = this@ActivitiesFragment.searchInterface.apply {

        }
    }

    private val headerSection = Section()
    private val searchSection = Section()
    private val tagsSection = Section()
    private val calendarSection = Section()
    private val daySection = Section()
    private val eventsSection = Section()
    private val allSection = Section()

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            //add(headerSection)
            // add(calendarSection)
            add(searchSection)
            add(allSection)
            add(tagsSection)
            add(daySection)
            add(eventsSection)
        }
    }

    private val calendarAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(calendarSection)
        }
    }


    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {

        override fun onActivityClick(subEvent: EventActivityModel) {
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

    private var calendarItem: CalendarHorizontalListPager? = null

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
            isHardwareAccelerated
            setHasFixedSize(false)
            setWillNotDraw(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    mDy += dy
                    val mLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastItem = mLayoutManager.findLastCompletelyVisibleItemPosition()
                    try {
                        val firstItem = eventsSection.getItem(0) as EventActivityDateItem
                        val firstDay =
                            createCalendarDay(defaultServerDateFormatter.parse(firstItem.getDay()).time)

                        if (mDy <= 0) {
                            mIsCurrentPageDay?.let { changeDay(it) }
                            calendarPager?.setCurrentItem(mIsCurrentPageItem, true)
                        } else {
                            val item = eventsSection.getItem(lastItem) as EventActivityDateItem
                            val now =
                                createCalendarDay(defaultServerDateFormatter.parse(item.getDay()).time)
                            changeDay(now)
                            changeDayWhenScrollDown(now)
                        }


                    } catch (e: Exception) {

                    }
                }
            })
        }

        calendarPager.apply {
            adapter = calendarAdapter
            offscreenPageLimit = 3
        }
        btnGoToScheme.setOnClickListener {

        }
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        initCollapseLabel()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setTags(tags: List<Tag>?) {
        //headerSection.update(listOf(ScreenHeaderItem(getString(R.string.timetable))))

        searchSection.update(listOf(SearchActivityItem({
            //presenter.onSearchTextChange(it)
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
        days?.map { day ->
            mCount += 1
            listDays.add(day)
            if (mCount == 7) {
                mCount = 0
                calendarSection.add(CalendarHorizontalListItem(listDays) {
                    presenter.onDaySelected(it)
                })
                //listItems.add(CalendarHorizontalListItem(listDays, onDaySelect))
                listDays.clear()
            }
        }
    }

    override fun selectDay(day: EventScheduleCalendarDay) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.selectDay(day)
        }
        deselectAllExcept(day)

        mIsCurrentPageItem = calendarPager.currentItem
        mIsCurrentPageDay = day
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
                mIsCurrentPageItem = calendarPager.currentItem
            }
        })
        if (day.hasEvents) {

        }
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

    override fun setSubEvents(subEvents: List<EventActivityModel>, selectedTags: List<Tag>) {
//        eventsSection.update(subEvents.map { subEvent ->
//            SubEventItem(subEvent, SubEventItem.Mode.SCHEDULE, onSubEventClickListener, presenter.canDoActions)
//        })
//        eventsSection.update(subEvents.map { subEvent ->
//            EventActivityItem(subEvent, selectedTags,onSubEventClickListener)
//        })
    }

    override fun setSubEventsNew(
        subEvents: Map<String, LinkedSet<EventActivityModel>>,
        selectedTags: List<Tag>
    ) {
        eventsSection.update(
            listOf(
                SubEventsWithDateItem(
                    getString(R.string.no_activity_title),
                    getString(R.string.no_activity_with_params_title),
                    subEvents,
                    selectedTags,
                    onSubEventClickListener
                )
            )
        )


    }

    override fun showCurrentDay(day: EventScheduleCalendarDay, daysSize: Int) {
        //daySection.update(listOf(DayHeaderItem(day.millis, daysSize != 1)))
    }


    override fun showEmptyEventPlaceholder() {
        showPlaceholder(getString(R.string.schedule_empty_event_placeholder), null)
    }

    override fun showEmptyDayPlaceholder() {
        showPlaceholder(getEmptyDayPlaceholderText(), getEmptyDayPlaceholderDescription())
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
        val args = SubeventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subevent_fragment, args)
    }

    override fun updateSubevent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<EventActivityItem> { it -> it.id == idLong }?.notifyChanged()
    }


    override fun hideDataFormCacheMessage() {
        //tvCacheData.visibility = View.GONE
    }

    override fun showAllTags() {
        findNavController().navigate(
            ActivitiesFragmentDirections.actionActivitiesFragmentToEventTagsFragment()
                .setTags(presenter.getTagsList().toTypedArray())
        )
        //findNavController().navigate(R.id.event_tags_fragment, bundleOf("tags" to presenter.getTagsList()))
    }

    override fun hideCurrentDay() {
        daySection.update(emptyList())
    }

    private fun getEmptyDayPlaceholderText(): String =
        getString(R.string.schedule_my_empty_day_placeholder_title)

    private fun getEmptyDayPlaceholderDescription(): String? =
        getString(R.string.schedule_my_empty_day_placeholder_description)


    override fun provideSearchInterface(): SearchInterface {
        return searchInterface
    }

    override fun showDataFormCacheMessage(cacheDate: String) {
        //        tvCacheData.apply {
//            text = String.format(getString(R.string.schedule_cache_data), cacheDate)
//            visibility = VISIBLE
//        }
    }

    override fun hidePlaceholder() = Unit

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

}