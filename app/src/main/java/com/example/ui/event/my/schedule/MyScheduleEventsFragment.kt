package com.example.ui.event.my.schedule

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleData
import com.example.data.models.EventScheduleDay
import com.example.databinding.FragmentMyScheduleEventsBinding
import com.example.extensions.calendar
import com.example.extensions.dp
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.EventDaysListItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheet
import com.example.ui.event.my.schedule.items.EventScheduleGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.dialogs.CustomProgressDialog
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.ui.views.dialogs.MessageDialogWithGreenButton
import com.example.util.SearchInput
import com.example.util.getMonthName
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onScrollStateChanged
import onScrolled
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleEventsFragment : BaseFragment<FragmentMyScheduleEventsBinding>(),
    MyScheduleEventsContract.View {

    var mCanChangeDay = false
    private val mProgressDialog by lazy { CustomProgressDialog(requireContext()) }
    private val mLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    @InjectPresenter
    lateinit var mPresenter: MyScheduleEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyScheduleEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyScheduleEventsPresenter = presenterProvider.get()

    private val subEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) =
            mPresenter.onSubEventClick(eventId, subEvent)

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            mPresenter.onAddSubEventToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            mPresenter.onRemoveSubEventFromScheduleClick(subEvent)
    }

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
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                layoutManager = mLayoutManager
                adapter = groupAdapter
                setItemViewCacheSize(10)
                recycledViewPool.setMaxRecycledViews(0, 10)
                onScrolled { _, _ ->
                    val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        if (groupAdapter.getItem(lastItem) is EventActivityDateItem) {
                            val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                            if (!mPresenter.isJumping){
                                changeDayWhenScrollDown(item.getDay())
                                if (!mCanChangeDay) changeDay(item.getDay())
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
                onScrollStateChanged { rv, newState ->
                    mCanChangeDay =
                        newState !== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                }
            }
            appBar.apply {
                calendarPager.apply {
                    adapter = calendarAdapter
                    offscreenPageLimit = 3
                }
                ivBack.setOnClickListener { findNavController().navigateUp() }
                etSearch.apply {
                    SearchInput(this).apply {
                        setOnTextChange { mPresenter.onSearchTextChange(it) }
                        setOnTextChangeDone { mPresenter.onSearchTextSubmit(it) }
                    }

                    onTextChanged { btnClear.isVisible = !it.isNullOrEmpty() }
                    btnClear.apply {
                        isVisible = !etSearch.text.isNullOrEmpty()
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
    }


    override fun setContentPlaceholder() {
        calendarSection.updateItem(PlaceholderItem(PlaceholderItem.Type.SCHEDULE_CALENDAR))
        eventsSection.update(List(3) { PlaceholderItem(PlaceholderItem.Type.SCHEDULE_LIST) })
    }


    override fun setHeaderCalendar(days: List<List<EventScheduleDay>>) {
        calendarSection.update(
            days.mapIndexed { i, l ->
                EventDaysListItem(i, l) { day -> mPresenter.onDaySelected(day) }
            }
        )
        mBinding.appBar.tvMonth.isVisible = !days.isNullOrEmpty()
        mBinding.appBar.clSearch.isVisible = true
    }

    override fun setMonthCalendar(dates: List<EventScheduleDay>, month: String) {
        mBinding.appBar.apply {
            tvMonth.apply {
                text = month
                setOnClickListener {
                    CalendarBottomSheet(
                        requireContext(),
                        mPresenter.currentDay,
                        dates
                    ).setDateSelectCallback { date ->
                        changeDayWhenScrollDown(date.date)
                        changeDay(date.date)
                        scrollContent(date)
                    }.show()
                }
            }
        }
    }

    override fun setContent(data: List<EventScheduleData>) {
        eventsSection.update(
            data.map {
                EventScheduleGroup(
                    it,
                    { id -> mPresenter.onShowEventClick(id) },
                    subEventClickListener
                )
            }
        )
    }

    override fun selectDay(day: EventScheduleDay) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as EventDaysListItem
            item.selectDay(day)
        }
    }

    override fun scrollContent(day: EventScheduleDay) {
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDay() == day.date }
        if (group == null) {
            val message = getString(R.string.this_day_doesnt_have_event)
            showMessageDialog(message)
        } else {
            val position = groupAdapter.getAdapterPosition(group)
            mPresenter.startJumpingTimer()
            mLayoutManager.startSmoothScroll(getSmoothScroller(position))
        }
    }

    override fun scrollToDay(day: EventScheduleDay) {
        Handler().post(Runnable {
            val item = calendarSection.findItemBy<EventDaysListItem> { it.isHasDay(day.date) }
            if (item != null) {
                val position = calendarSection.getPosition(item)
                mBinding.appBar.calendarPager.setCurrentItem(position, true)
                item.selectDay(day)
            }
        })
    }

    private fun changeDayWhenScrollDown(day: String?) {
        try {
            Handler().post(Runnable {
                mPresenter.apply { currentDay = createCalendarDay(day) }
                val item = calendarSection.findItemBy<EventDaysListItem> { it.isHasDay(day) }
                if (item != null) {
                    val position = calendarSection.getPosition(item)
                    mBinding.appBar.apply {
                        calendarPager.setCurrentItem(position, true)
                        tvMonth.text = getMonthName(item.getFirstItem().millis.calendar())
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun changeDay(day: String?) {
        try {
            for (i in 0 until calendarSection.itemCount) {
                val item = calendarSection.getItem(i) as EventDaysListItem
                item.changeDay(day)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        )
    }

    override fun showMessageDialog(message: String) {
        MessageDialogWithGreenButton(requireContext(), message)
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
    }

    override fun showEmptyListPlaceholder() {
        hideLoadingAlertDialog()
        calendarSection.update(emptyList())
        eventsSection.updateItem(
            NoScheduleEventItem(
                "Нет результатов",
                "По заданным параметрам нет подходящих событий",
                70.dp
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
            }
        }
    }

    override fun showErrorMessage(eventId: String, message: String) {
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            if (!eventId.isNullOrEmpty()) {
                mPresenter.getEventsList()
                showLoadingAlertDialog()
            }
        }
    }

    override fun showLoadingAlertDialog() = mProgressDialog.showDialog()
    override fun hideLoadingAlertDialog() = mProgressDialog.hideDialog()


    private fun getSmoothScroller(jumPosition: Int): LinearSmoothScroller {
        val scroller by lazy {
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }

                override fun updateActionForInterimTarget(action: Action?) {
                    action?.jumpTo(jumPosition)
                }
            }
        }
        scroller.targetPosition = jumPosition
        return scroller
    }

    override fun layout(): Int = R.layout.fragment_my_schedule_events

}