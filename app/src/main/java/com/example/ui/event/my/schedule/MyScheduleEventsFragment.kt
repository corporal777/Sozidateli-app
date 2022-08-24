package com.example.ui.event.my.schedule

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.FragmentMyScheduleEventsBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.activities.items.SearchActivityItem
import com.example.ui.event.my.items.NoEventItem
import com.example.ui.event.my.schedule.items.MyScheduleEventsData
import com.example.ui.event.my.schedule.items.MyScheduleSubEventsGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.calendarView.CalendarDay
import com.example.ui.views.dialogs_new.CalendarBottomSheet
import com.example.ui.views.dialogs_new.CustomProgressDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.example.util.SearchInput
import com.example.util.getMonthName
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onPageChanged
import onScrollStateChanged
import onScrolled
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleEventsFragment : BaseFragmentNew<FragmentMyScheduleEventsBinding>(),
    MyScheduleEventsContract.View {

    private var mDy = 0
    var mCanChangeDay = false
    private lateinit var mProgressDialog: CustomProgressDialog

    private lateinit var mCurrentDay: EventScheduleCalendarDay

    @InjectPresenter
    lateinit var mPresenter: MyScheduleEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyScheduleEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyScheduleEventsPresenter = presenterProvider.get()

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

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(eventsSection)
        }
    }

    private val calendarAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(calendarSection)
            //add(searchSection)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressDialog = CustomProgressDialog(requireContext())
        initCollapseLabel()
        mBinding.apply {
            eventsList.apply {
                setItemViewCacheSize(10)
                recycledViewPool.setMaxRecycledViews(0, 10)
                adapter = groupAdapter
                val mLayoutManager = this.layoutManager as LinearLayoutManager
                onScrolled { _, dy ->
                    mDy += dy
                    val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                        val now =
                            mPresenter.createCalendarDay(
                                defaultServerDateFormatter.parse(item.date).time
                            )
                        changeDayWhenScrollDown(now)
                        if (!mCanChangeDay) {
                            changeDay(now)
                        }
                        /* if (mDy <= 0) {
                            calendarPager.setCurrentItem(0, true)
                            val item = eventsSection.getGroup(0) as MyScheduleSubEventsGroup
                            val day =
                                mPresenter.createCalendarDay(defaultServerDateFormatter.parse(item.getFirstItemDate()).time)
                            changeDay(day)
                            changeDayWhenScrollDown(day)
                        } else {

                        } */
                    } catch (e: Exception) {

                    }
                }
                onScrollStateChanged { rv, newState ->
                    mCanChangeDay =
                        newState !== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                }
            }
            calendarPager.apply {
                adapter = calendarAdapter
                offscreenPageLimit = 3
                onPageChanged { position, _, _ ->
                    try {
                        val item = calendarAdapter.getItem(position) as CalendarHorizontalListItem
                        val day = item.getFirstItem().millis.calendar()
                        val mMonth = getMonthName(day)
                        mBinding.tvMonth.text = mMonth
                    } catch (e: Exception) {

                    }
                }
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etSearch.apply {
                SearchInput(this).apply {
                    setOnTextChange {
                        mPresenter.onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        mPresenter.onSearchTextSubmit(it)
                        hideKeyboard()

                    }
                }

                onTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                }
                btnClear.apply {
                    btnClear.isVisible = !etSearch.text.isNullOrEmpty()
                    setOnClickListener { etSearch.text = null }
                }

                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }

        }
    }

    override fun setSearchContent() {
    }

    override fun setContent(data: List<MyScheduleEventsData>) {
        eventsSection.update(
            data.map {
                MyScheduleSubEventsGroup(
                    it,
                    { id ->
                        mPresenter.onShowEventClick(id)
                    }, mSubEventClickListener
                )
            }
        )
    }

    override fun setHeaderCalendar(days: List<EventScheduleCalendarDay>) {
        val listDays = arrayListOf<EventScheduleCalendarDay>()
        val listItems = arrayListOf<CalendarHorizontalListItem>()
        var mCount = 0
        var mCountSize = 0
        days?.map { day ->
            mCount += 1
            mCountSize += 1
            listDays.add(day)
            if (mCount == 7) {
                mCount = 0
                listItems.add(CalendarHorizontalListItem(listDays) {
                    mPresenter.onDaySelected(it)
                }
                )
                listDays.clear()
            } else {
                if (mCountSize == days.size) {
                    listItems.add(CalendarHorizontalListItem(listDays) {
                        mPresenter.onDaySelected(it)
                    }
                    )
                }
            }
        }
        calendarSection.update(listItems)
    }

    override fun setMonthCalendar(
        subEventDays: List<CalendarDay>,
        month: String,
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
    }

    override fun showEmptyListPlaceholder() {
//        calendarSection.clear()
//        searchSection.clear()
//        mBinding.tvMonth.apply {
//            text = ""
//            isVisible = false
//        }
        hideLoadingAlertDialog()
        eventsSection.update(
            listOf(
                NoScheduleEventItem(
                    "Нет результатов",
                    "По заданным параметрам нет подходящих событий"
                    //getString(R.string.no_sub_events_in_schedule),
                )
            )
        )
    }


    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun getResultForUpdate() {
        setFragmentResultListener("eventKey") { _, bundle ->
            val result = bundle.getString("eventId")
            if (!result.isNullOrEmpty()) {
                showLoadingAlertDialog()
                mPresenter.getEventsList()
                //removeBannedOrCancelledEvent(result)
                showToast(result)
            } else {
                showToast("NULL EVENT ID")
            }
        }
    }

    override fun showErrorMessage(eventId: String, message: String) {
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            if (!eventId.isNullOrEmpty()) {
                mPresenter.getEventsList()
                showLoadingAlertDialog()
                //removeBannedOrCancelledEvent(eventId)
            }
        }
    }

    override fun showLoadingAlertDialog() {
        mProgressDialog.showDialog()
    }

    override fun hideLoadingAlertDialog() {
        mProgressDialog.hideDialog()
    }

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        var mPosition = 0
        mCurrentDay = day
        Handler().post(Runnable {
            val item =
                calendarSection.findItemBy<CalendarHorizontalListItem> { it.scrollToDay(day) }
            if (item != null) {
                item.selectDay(day)
                mPosition = calendarSection.getPosition(item)
                mBinding.calendarPager.setCurrentItem(mPosition, true)
            }
        })
    }

    override fun selectDay(day: EventScheduleCalendarDay) {
        mCurrentDay = day
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalListItem
            item.selectDay(day)
        }
    }

    private fun changeDayWhenScrollDown(day: EventScheduleCalendarDay) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun changeDay(day: EventScheduleCalendarDay) {
        try {
            for (i in 0 until calendarSection.itemCount) {
                val item = calendarSection.getItem(i) as CalendarHorizontalListItem
                item.selectDayNew(day)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
//                                tvLabelLarge.apply {
//                                    visibility = View.VISIBLE
//                                    alpha = 0F
//                                    animate().setDuration(500).alpha(1.0f)
//                                }
                            }

                        }
                        TO_COLLAPSED -> {
                            mBinding.apply {
//                                tvLabelLarge.apply {
//                                    visibility = View.GONE
//                                    alpha = 0F
//                                    animate().setDuration(500).alpha(1.0f)
//                                }
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

    private val mSmoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }


    override fun layout(): Int = R.layout.fragment_my_schedule_events

}