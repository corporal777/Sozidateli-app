package com.example.ui.event.my.schedule

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.FragmentMyScheduleEventsBinding
import com.example.extensions.findItemBy
import com.example.holders.CalendarHorizontalListItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.about.redesign.AboutEventPresenterNew
import com.example.ui.event.activities.items.SearchActivityItem
import com.example.ui.event.my.schedule.items.SubEventsWithHeaderGroup
import com.example.ui.views.dialogs_new.CalendarBottomSheet
import com.example.ui.views.dialogs_new.CalendarBottomSheetFragment
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activitys.*
import kotlinx.android.synthetic.main.fragment_my_schedule_events.*
import kotlinx.android.synthetic.main.fragment_my_schedule_events.activitiesAppBar
import kotlinx.android.synthetic.main.fragment_my_schedule_events.calendarPager
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleEventsFragment : BaseFragment(), MyScheduleEventsContract.View {


    private var mAppBarScrollValue = 0f

    private var _binding: FragmentMyScheduleEventsBinding? = null
    private val mBinding get() = _binding!!

    @InjectPresenter
    lateinit var mPresenter: MyScheduleEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyScheduleEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyScheduleEventsPresenter = presenterProvider.get().apply {

    }

    private val searchSection = Section()
    private val eventsSection = Section()
    private val calendarSection = Section()

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(searchSection)
            add(eventsSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    mPresenter.onItemTake(position)
                }
            })
        }
    }

//    private val groupAdapter by lazy {
//        GroupAdapter<GroupieViewHolder>().apply {
//            add(searchSection)
//            add(eventsSection)
//        }
//    }

    private val calendarAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(calendarSection)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyScheduleEventsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = groupAdapter
            }
            calendarPager.apply {
                adapter = calendarAdapter
                offscreenPageLimit = 3
            }
        }

        initCollapseLabel()
    }

    override fun setContent(data: List<EventNew?>) {
        data.forEach {
            var isFirstItem = true
            val title = it?.name
            val image = it?.image?.uri
            val mSubEventsMap = it?.binds?.activity?.groupBy { event ->
                event.holdingDate?.from?.split(" ")?.get(0) ?: ""
            }
            mSubEventsMap?.forEach {
                eventsSection.add(SubEventsWithHeaderGroup(isFirstItem, title, image, it.key, it.value))
                isFirstItem = false
            }
        }
    }

    override fun setHeaderAndCalendar(month: String, days: List<EventScheduleCalendarDay>?) {
        mBinding.apply {
            calendarContainer.isVisible = true
            tvMonth.apply {
                text = month
                setOnClickListener {
                    CalendarBottomSheet(requireContext())
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
                })
                listDays.clear()
            }
        }
    }

    override fun setSearchBlock() {
        searchSection.update(listOf(SearchActivityItem({
            //presenter.onSearchTextChange(it)
        }, {
            hideKeyboard()
        })))
    }


    override fun showAboutEvent(event: String) {
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

    override fun selectDay(
        day: EventScheduleCalendarDay
    ) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun layout(): Int = R.layout.fragment_my_schedule_events

}