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
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.FragmentMyScheduleEventsBinding
import com.example.extensions.*
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheet
import com.example.ui.event.my.schedule.items.*
import com.example.ui.views.dialogs_new.CustomProgressDialog
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.example.util.SearchInput
import com.example.util.getMonthName
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import offsetChangedListener
import onPageChanged
import onScrollStateChanged
import onScrolled
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class MyScheduleEventsFragment : BaseFragmentNew<FragmentMyScheduleEventsBinding>(),
    MyScheduleEventsContract.View {

    private var mDy = 0
    var mCanChangeDay = false
    private lateinit var mCurrentDay: EventScheduleCalendarDay
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
                onScrolled { _, dy ->
                    mDy += dy
                    val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                        val now =
                            mPresenter.createCalendarDay(defaultServerDateFormatter.parse(item.date).time)
                        changeDayWhenScrollDown(now)
                        if (!mCanChangeDay) changeDay(now)

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
                    onPageChanged { position, _, _ ->
                        try {
                            val item =
                                calendarAdapter.getItem(position) as CalendarHorizontalListItem
                            val day = item.getFirstItem().millis.calendar()
                            val mMonth = getMonthName(day)
                            tvMonth.text = mMonth
                        } catch (e: Exception) {

                        }
                    }
                }
                appBar.ivBack.setOnClickListener { findNavController().navigateUp() }
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

    override fun setHeaderCalendar(days: List<List<EventScheduleCalendarDay>>) {
        calendarSection.update(
            days.mapIndexed { index, d ->
                CalendarHorizontalListItem(index, d) { day ->
                    mPresenter.onDaySelected(day)
                }
            }
        )
        mBinding.appBar.tvMonth.isVisible = !days.isNullOrEmpty()
        mBinding.appBar.clSearch.isVisible = true
    }

    override fun setMonthCalendar(subEvents: List<EventActivityModel>, month: String) {
        mBinding.appBar.apply {
            tvMonth.apply {
                text = month
                setOnClickListener {
                    CalendarBottomSheet(
                        requireContext(),
                        mCurrentDay,
                        subEvents
                    ).setDateSelectCallback { date ->
                        changeDayWhenScrollDown(date)
                        changeDay(date)
                        scrollContent(date)
                    }.show()
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
            mLayoutManager.startSmoothScroll(getSmoothScroller(position))
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
        hideLoadingAlertDialog()
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

    override fun scrollToDay(day: EventScheduleCalendarDay) {
        var mPosition = 0
        mCurrentDay = day
        Handler().post(Runnable {
            val item = calendarSection.findItemBy<CalendarHorizontalListItem> { it.scrollToDay(day) }
            if (item != null) {
                item.selectDay(day)
                mPosition = calendarSection.getPosition(item)
                mBinding.appBar.calendarPager.setCurrentItem(mPosition, true)
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
                    mBinding.appBar.calendarPager.setCurrentItem(mPosition, true)

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