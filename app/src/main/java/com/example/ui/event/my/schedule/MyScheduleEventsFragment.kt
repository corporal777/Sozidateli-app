package com.example.ui.event.my.schedule

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.FragmentMyScheduleEventsBinding
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
import com.example.util.TranslateAnimationUtil
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

    private var mDy = 0f
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

    private val eventsSection = Section()
    private val calendarSection = Section()

    private var calendarItem: CalendarHorizontalListItem? = null
    private var searchItem: SearchActivityItem? = null

//    private val groupAdapter by lazy {
//        GroupAdapter<GroupieViewHolder>().apply {
//            add(searchSection)
//            add(eventsSection)
//        }
//    }

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
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

    private val onScrollingStateListener by lazy {
        TranslateAnimationUtil.OnScrollingState { dy ->
            mDy += dy
            Log.e("DISTANCE", dy.toString())
            val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
            val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
            try {
                if (mDy <= 0) {
                    //mIsCurrentPageDay?.let { changeDay(it) }
                    //calendarPager.setCurrentItem(mIsCurrentPageItem, true)
                } else {
                    val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                    val now =
                        mPresenter.createCalendarDay(
                            defaultServerDateFormatter.parse(item.date).time
                        )
                    //changeDayWhenScrollDown(now)
                    scrollToDay(now)
                    if (!mCanChangeDay) {
                        //selectDay(now)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = groupAdapter

//                setOnTouchListener(
//                    TranslateAnimationUtil(
//                        requireContext(),
//                        onScrollingStateListener
//                    )
//                )

                val mLayoutManager = this.layoutManager as LinearLayoutManager
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        //super.onScrolled(recyclerView, dx, dy)
                        mDy += dy
                        val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                        try {
                            if (mDy <= 0) {
                                if (!mCanChangeDay) {
                                    calendarItem?.selectFirstDay()
                                }
                            } else {
                                val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                                val now =
                                    mPresenter.createCalendarDay(
                                        defaultServerDateFormatter.parse(item.date).time
                                    )
                                //changeDayWhenScrollDown(now)

                                if (!mCanChangeDay) {
                                    scrollToDay(now)
                                    //selectDay(now)
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        //mCanChangeDay = newState !== RecyclerView.SCROLL_STATE_DRAGGING
                        mCanChangeDay = newState !== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                    }
                })
            }
            calendarList.apply {
                adapter = calendarAdapter
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
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
        days: List<EventScheduleCalendarDay>,
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
                        scrollContent(date)
                    }
                }
            }
        }

        calendarSection.update(
            listOf(
                CalendarHorizontalListItem(days) { day ->
                    mPresenter.onDaySelected(day)
                }.apply {
                    this@MyScheduleEventsFragment.calendarItem = this
                },
                SearchActivityItem({ mPresenter.onSearchTextChange(it) }, {
                    mPresenter.onSearchTextSubmit(it)
                    hideKeyboard()
                }).apply {
                    this@MyScheduleEventsFragment.searchItem = this
                })
        )
    }

    override fun scrollContent(day: EventScheduleCalendarDay) {
        val date = defaultServerDateFormatter.format(day.millis)
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDay() == date }
        if (group == null) {
            val message = getString(R.string.this_day_doesnt_have_event)
            showMessageDialog(message)
        } else {
            val position = groupAdapter.getAdapterPosition(group)
            val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
            mSmoothScroller.targetPosition = position
            mLayoutManager.startSmoothScroll(mSmoothScroller)
        }
    }


    override fun scrollToDay(day: EventScheduleCalendarDay) {
        mCurrentDay = day
        Handler().post(Runnable {
            calendarItem?.scrollToDay(day)
            //calendarItem?.selectDay(day)
        })
    }

    override fun selectDay(
        day: EventScheduleCalendarDay
    ) {
        mCurrentDay = day
        calendarItem?.selectDay(day)
    }


    override fun showMessageDialog(message: String) {
        MessageDialogWithGreenButton(requireContext(), message)
    }

    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(eventId).build().toBundle()
        )
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<EventActivityItem> { it.id == idLong }?.notifyChanged(subEvent)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
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

    private val mSmoothScroller: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }


        }
    }

    override fun layout(): Int = R.layout.fragment_my_schedule_events

}