package com.example.ui.event.my.schedule

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.FragmentMyScheduleEventsBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.activities.items.SearchActivityItem
import com.example.ui.event.my.schedule.items.MyScheduleEventsData
import com.example.ui.event.my.schedule.items.MyScheduleSubEventsGroup
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.calendarView.CalendarDay
import com.example.ui.views.dialogs_new.CalendarBottomSheet
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.example.util.getMonthName
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleEventsFragment : BaseFragmentNew<FragmentMyScheduleEventsBinding>(),
    MyScheduleEventsContract.View {

    private var mDy = 0
    var mCanChangeDay = false

    private lateinit var mCurrentDay: EventScheduleCalendarDay

    private var mIsCurrentPageItem = 0
    private var mIsCurrentPageDay: EventScheduleCalendarDay? = null

    @InjectPresenter
    lateinit var mPresenter: MyScheduleEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyScheduleEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyScheduleEventsPresenter = presenterProvider.get().apply {

    }


    private val mSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
            mPresenter.onSubEventClick(eventId, subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) {
            mPresenter.onAddSubEventToScheduleClick(subEvent)
        }

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
            mPresenter.onRemoveSubEventFromScheduleClick(subEvent)
        }

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {
        }

    }

    private val searchSection = Section()
    private val eventsSection = Section()
    private val calendarSection = Section()

//    private val groupAdapter by lazy {
//        GroupAdapter<GroupieViewHolder>().apply {
//            add(searchSection)
//            add(eventsSection)
//        }
//    }

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(searchSection)
            add(eventsSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                }
            })
        }
    }

    private val calendarAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(calendarSection)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = groupAdapter
                val mLayoutManager = this.layoutManager as LinearLayoutManager
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        mDy += dy
                        val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                        try {
                            if (mDy <= 0) {
                                mIsCurrentPageDay?.let { changeDay(it) }
                                calendarPager.setCurrentItem(mIsCurrentPageItem, true)
                            } else {
                                val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                                val now =
                                    mPresenter.createCalendarDay(
                                        defaultServerDateFormatter.parse(item.date).time
                                    )
                                changeDayWhenScrollDown(now)
                                if (!mCanChangeDay) {
                                    changeDay(now)
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        mCanChangeDay = newState !== RecyclerView.SCROLL_STATE_DRAGGING
                    }
                })
            }
            calendarPager.apply {
                adapter = calendarAdapter
                offscreenPageLimit = 3
            }
        }

        initCollapseLabel()
    }

    override fun setContent(data: List<EventNew?>) {
        eventsSection.clear()
    }

    override fun setContentNew(data: List<MyScheduleEventsData?>) {
        eventsSection.clear()
        data.map {
            if (it != null) {
                eventsSection.add(
                    MyScheduleSubEventsGroup(
                        it,
                        { id ->
                            mPresenter.onShowEventClick(id)
                        }, mSubEventClickListener
                    )
                )
            }
        }
    }

    override fun setHeaderAndCalendar(
        subEventDays: List<CalendarDay>,
        month: String,
        days: List<EventScheduleCalendarDay>?,
        firstDate: CalendarDay?,
        lastDate: CalendarDay?
    ) {
        mBinding.apply {
            calendarContainer.isVisible = true
            tvMonth.apply {
                text = month
                setOnClickListener {
                    CalendarBottomSheet(
                        requireContext(),
                        mCurrentDay,
                        subEventDays,
                        firstDate,
                        lastDate
                    ).setSelectCallback {
                        val valueLong =
                            defaultServerDateFormatter.parse(defaultServerDateFormatter.format(it.time)).time
                        val date = mPresenter.createCalendarDay(valueLong)
                        changeDayWhenScrollDown(date)
                        changeDay(date)
                        scrollContent(date)
                    }
                }
            }
        }

        val listDays = arrayListOf<EventScheduleCalendarDay>()
        var mCount = 0
        days?.map { day ->
            mCount += 1
            listDays.add(day)
            if (mCount == 7) {
                mCount = 0
                calendarSection.add(CalendarHorizontalListItem(listDays) {
                    mPresenter.onDaySelected(it)
                })
                listDays.clear()
            }
        }
        mBinding.calendarPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                try {
                    val item = calendarAdapter.getItem(position) as CalendarHorizontalListItem
                    val day = item.getFirstItem().millis.calendar()
                    val currentYear = System.currentTimeMillis().calendar().get(Calendar.YEAR)
                    val mMonth = if (currentYear == day.get(Calendar.YEAR)) {
                        getMonthName(day.get(Calendar.MONTH))
                    } else {
                        getMonthName(day.get(Calendar.MONTH)) + " " + day.get(Calendar.YEAR)
                    }
                    mBinding.tvMonth.text = mMonth
                } catch (e: Exception) {

                }
            }
        })
    }

    override fun scrollContent(day: EventScheduleCalendarDay) {
        val mSmoothScroller: RecyclerView.SmoothScroller =
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
        val date = defaultServerDateFormatter.format(day.millis)
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDay() == date }
        if (group?.date.isNullOrEmpty()) {
            val message = "По выбранному дню нет событий."
            showMessageDialog(message)
            //mPresenter.findNearestEventDay(day)
        }
        if (group != null) {
            if (date == group.date) {
                val position = groupAdapter.getAdapterPosition(group)
                val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
                mSmoothScroller.targetPosition = position
                mLayoutManager.startSmoothScroll(mSmoothScroller)
            }
        }
    }

    override fun setSearchBlock() {
        searchSection.update(listOf(SearchActivityItem({
            mPresenter.onSearchTextChange(it)
        }, {
            mPresenter.onSearchTextSubmit(it)
            hideKeyboard()
        })))
    }


    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
        )
    }

    override fun showMessageDialog(message: String) {
        MessageDialogWithGreenButton(requireContext(), message)
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged(subEvent)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        var mPosition = 0
        mCurrentDay = day
        Handler().post(Runnable {
            selectDay(day)
            val item =
                calendarSection.findItemBy<CalendarHorizontalListItem> { it.scrollToDay(day) }

            if (item != null) {
                mPosition = calendarSection.getPosition(item)
                mBinding.calendarPager.setCurrentItem(mPosition, true)
                mIsCurrentPageItem = mBinding.calendarPager.currentItem
                mIsCurrentPageDay = day
            }
        })
    }

    override fun selectDay(
        day: EventScheduleCalendarDay
    ) {
        mCurrentDay = day
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.selectDay(day)
        }
        deselectAllExcept(day)
    }

    private fun deselectAllExcept(except: EventScheduleCalendarDay) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.deselectAllExcept(except)
        }
    }

    private fun changeDayWhenScrollDown(day: EventScheduleCalendarDay) {
        var mPosition = 0
        mCurrentDay = day
        Handler().post(Runnable {
            val item =
                calendarSection.findItemBy<CalendarHorizontalListItem> { it.changeDay(day) }
            if (item != null) {
                mPosition = calendarSection.getPosition(item)
                mBinding.calendarPager.setCurrentItem(mPosition, true)

            }
        })
    }

    private fun changeDay(day: EventScheduleCalendarDay) {
        Handler().post(Runnable {
            for (i in 0 until calendarSection.itemCount) {
                val item = calendarSection.getItem(i) as CalendarHorizontalListItem
                item.selectDayNew(day)
            }
        })

    }

    private fun initCollapseLabel() {
        mBinding.appBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
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
                            mBinding.apply {
                                tvLabelLarge.apply {
                                    visibility = View.VISIBLE
                                    alpha = 0F
                                    animate().setDuration(500).alpha(1.0f)
                                }
                            }

                        }
                        TO_COLLAPSED -> {
                            mBinding.apply {
                                tvLabelLarge.apply {
                                    visibility = View.GONE
                                    alpha = 0F
                                    animate().setDuration(500).alpha(1.0f)
                                }
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


    override fun layout(): Int = R.layout.fragment_my_schedule_events

}