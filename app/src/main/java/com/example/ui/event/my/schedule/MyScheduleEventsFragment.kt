package com.example.ui.event.my.schedule

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
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
import com.example.extensions.*
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheet
import com.example.ui.event.my.schedule.calendar.CalendarHorizontalDaysItem
import com.example.ui.event.my.schedule.items.EventScheduleGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.dialogs.CustomProgressDialog
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.util.SearchInput
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import getLocationOfView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleEventsFragment : BaseFragment<FragmentMyScheduleEventsBinding>(),
    MyScheduleEventsContract.View {

    private val progressDialog by lazy { CustomProgressDialog(requireContext()) }
    private val listManager by lazy { LinearLayoutManager(requireContext()) }
    private var oldPosition = 0

    @InjectPresenter
    lateinit var presenter: MyScheduleEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyScheduleEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyScheduleEventsPresenter = presenterProvider.get()

    private val subEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) = presenter.onSubEventClick(eventId, subEvent)
        override fun onAddToScheduleClick(subEvent: EventActivityModel) = presenter.onAddSubEventToScheduleClick(subEvent)
        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) = presenter.onRemoveSubEventFromScheduleClick(subEvent)
    }

    private val eventsSection by lazy {
        Section().apply { update(List(3) { PlaceholderItem(PlaceholderItem.Type.SCHEDULE_LIST) }) }
    }
    private val calendarSection by lazy {
        Section().apply { updateItem(PlaceholderItem(PlaceholderItem.Type.SCHEDULE_CALENDAR)) }
    }

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
                layoutManager = listManager
                adapter = groupAdapter
                setItemViewCacheSize(10)
                recycledViewPool.setMaxRecycledViews(0, 10)

                onScrolled { _, _ ->
                    val lastItem = listManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        if (groupAdapter.getItem(lastItem) is EventActivityDateItem) {
                            val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                            if (!presenter.isJumping){
                                if (isLocationInOneLine(item.getView())) scrollPageContent(item.getDate())
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
            appBar.apply {
                ivBack.setOnClickListener { findNavController().navigateUp() }
                calendarPager.apply {
                    adapter = calendarAdapter
                    offscreenPageLimit = 3
                }
                etSearch.apply {
                    SearchInput(this).apply {
                        setOnFocusChange { hasFocus -> clSearch.changeBackground(hasFocus) }
                        setOnTextChange {
                            btnClear.isVisible = !it.isNullOrEmpty()
                            presenter.onSearchTextChange(it)
                        }
                        setOnTextChangeDone { presenter.onSearchTextSubmit(it) }
                    }
                    btnClear.apply {
                        isVisible = !etSearch.text.isNullOrEmpty()
                        setOnClickListener { etSearch.text = null }
                    }
                }
            }
        }
    }


    override fun setCalendar(days: List<List<EventScheduleDay>>, month: String) {
        calendarSection.update(
            days.mapIndexed { i, l ->
                CalendarHorizontalDaysItem(i, l) { day -> presenter.onDaySelected(day) }
            }
        )
        mBinding.appBar.apply {
            clSearch.isInvisible = days.isEmpty()
            tvMonth.apply {
                isInvisible = days.isEmpty()
                text = month
                setOnClickListener {
                    CalendarBottomSheet(requireContext(), presenter.getCurrentDay(), days.flatten())
                        .setDateSelectCallback { date ->
                            scrollPageContent(date)
                            scrollListContent(date)
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
                    { id -> presenter.onShowEventClick(id) },
                    subEventClickListener
                )
            }
        )
    }

    override fun selectDay(day: EventScheduleDay?) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as CalendarHorizontalDaysItem
            item.selectDay(day)
        }
    }

    override fun scrollPageContent(day: EventScheduleDay?) {
        if (day == null) return
        Handler().post(Runnable {
            val position = presenter.findPositionFromList(day) ?:return@Runnable
            mBinding.appBar.apply {
                calendarPager.setCurrentItem(position, true)
                tvMonth.text = getMonthName(day.millis.calendar())
                presenter.setCurrentDay(day)
            }
            val item = calendarSection.getItem(position)
            if (item is CalendarHorizontalDaysItem) item.selectDay(day)
        })
    }

    override fun scrollListContent(day: EventScheduleDay?) {
        if (day == null) showErrorMessage(false, getString(R.string.this_day_doesnt_have_event))
        else {
            val group = groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDate() == day }
            if (group == null) showErrorMessage(false, getString(R.string.this_day_doesnt_have_event))
            else {
                val position = groupAdapter.getAdapterPosition(group)
                presenter.startJumpingTimer()
                if (position == 0) listManager.startSmoothScroll(smoothScroller(position, 2))
                else {
                    if (position < oldPosition)
                        listManager.startSmoothScroll(smoothScroller(position, position + 2))
                    else listManager.startSmoothScroll(smoothScroller(position, position - 1))
                }
                oldPosition = position
            }
        }
    }


    override fun showAboutEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(eventId).build().toBundle()
        )
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
                presenter.getEventsList()
            }
        }
    }

    override fun showErrorMessage(eventId: String, message: String) {
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            if (!eventId.isNullOrEmpty()) {
                presenter.getEventsList()
                showLoadingAlertDialog()
            }
        }
    }

    override fun showLoadingAlertDialog() = progressDialog.showDialog()
    override fun hideLoadingAlertDialog() = progressDialog.hideDialog()


    private fun smoothScroller(targetPosition: Int, jumPosition: Int): LinearSmoothScroller {
        val scroller by lazy {
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int {
                    return if (targetPosition == 0) SNAP_TO_END
                    else if (targetPosition < oldPosition) SNAP_TO_END
                    else SNAP_TO_START
                }

                override fun updateActionForInterimTarget(action: Action?) {
                    action?.jumpTo(jumPosition)
                }
            }
        }
        scroller.targetPosition = targetPosition
        return scroller
    }

    private fun View.changeBackground(hasFocus: Boolean) {
        setBackgroundResource(
            if (hasFocus) R.drawable.background_search_field_rounded_focused
            else R.drawable.background_search_field_rounded_normal
        )
    }


    private fun isLocationInOneLine(view: View?): Boolean {
        return if (view == null) true
        else {
            val lineY = mBinding.lineView.getLocationOfView().second
            val viewY = view.getLocationOfView().second
            if (lineY == 0 || viewY == 0) return true
            else return viewY >= lineY && viewY <= (lineY + 250)
        }
    }

    override fun layout(): Int = R.layout.fragment_my_schedule_events

}