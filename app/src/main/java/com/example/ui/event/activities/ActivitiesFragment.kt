package com.example.ui.event.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AbsListView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.databinding.FragmentActivitysBinding
import com.example.extensions.*
import com.example.holders.CalendarHorizontalListItem
import com.example.holders.PlaceholderItem
import com.example.holders.TagsHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.activities.items.*
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.event.location.buildingScheme.redesign.DestinationSchemeFragmentArgs
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.notification.center.redesign.items.NotificationsDateItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.util.SearchInput
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_activitys.*
import kotlinx.android.synthetic.main.fragment_map_new.*
import onBackPressedCallback
import onScrollStateChanged
import onScrolled
import onTextChanged
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


class ActivitiesFragment : BaseFragmentNew<FragmentActivitysBinding>(), ActivitiesContract.View {

    override fun layout(): Int = R.layout.fragment_activitys

    @InjectPresenter
    lateinit var presenter: ActivitiesPresenter

    @Inject
    lateinit var presenterProvider: Provider<ActivitiesPresenter>

    private var mCanChangeDay = false

    @ProvidePresenter
    fun providePresenter(): ActivitiesPresenter = presenterProvider.get().apply {
        eventId = ActivitiesFragmentArgs.fromBundle(requireArguments()).eventId.toString()
        tagsNew = ActivitiesFragmentArgs.fromBundle(requireArguments()).tags?.map {
            val t = Tag.EventTag(it.id, it.name)
            t.isSelected = it.isSelected
            t
        }
    }

    private val tagsSection = Section()
    private val calendarSection = Section()
    private val eventsSection = Section()

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
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
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) = presenter.onSubEventClick(subEvent)
        override fun onAddToScheduleClick(subEvent: EventActivityModel) = presenter.onAddToScheduleClick(subEvent)
        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) = presenter.onRemoveFromScheduleClick(subEvent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("tags_fragment") { _, bundle ->
            presenter.tagsNew = bundle.getParcelableArrayList<NewTags>("tags")?.map {
                val t = Tag.EventTag(it.id, it.name)
                t.isSelected = it.isSelected
                t
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            activitiesList.apply {
                adapter = groupAdapter
                val mLayoutManager = this.layoutManager as LinearLayoutManager
                onScrolled { _, _ ->
                    val lastItem = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        if (groupAdapter.getItem(lastItem) is EventActivityDateItem) {
                            val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                            val now = presenter.createCalendarDay(item.date)
                            changeDayWhenScrollDown(now)
                            if (!mCanChangeDay) changeDay(now)
                        }
                    } catch (e: Exception) {
                    }
                }
                onScrollStateChanged { _, newState ->
                    mCanChangeDay =
                        newState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                }
            }
            calendarPager.apply {
                adapter = calendarAdapter
                offscreenPageLimit = 3
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            etSearch.apply {
                SearchInput(this).apply {
                    setOnTextChange {
                        presenter.onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        presenter.onSearchTextSubmit(it)
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

    override fun setContentPlaceholder() {
        calendarSection.updateItem(PlaceholderItem(PlaceholderItem.Type.ACTIVITY_CALENDAR))
        eventsSection.updateItem(PlaceholderItem(PlaceholderItem.Type.ACTIVITY_LIST))
    }

    override fun setSchemeButton(show: Boolean) {
        mBinding.btnGoToScheme.apply {
            isVisible = show
            setOnClickListener {
                presenter.onSchemeClick()
            }
        }
    }

    override fun setDays(days: List<List<EventScheduleCalendarDay>>) {
        calendarSection.update(
            days.mapIndexed { index, d ->
                CalendarHorizontalListItem(index, d) { day ->
                    presenter.onDaySelected(day)
                }
            }
        )
        mBinding.clSearch.isVisible = true
    }

    override fun setTags(tags: List<Tag>?) {
        if (!tags.isNullOrEmpty()) {
            tagsSection.updateItem(TagsHorizontalListItem(tags) { presenter.onTagSelected() })
        }
    }

    override fun setSubEvents(isApproved: Boolean, data: List<SubEventsData>) {
        eventsSection.update(
            data.map {
                Section().apply {
                    if (!it.titleDate.isNullOrEmpty())
                        add(EventActivityDateItem(it.titleDate, it.getDateInLong()))

                    add(
                        EventActivityItem(
                            presenter.eventId,
                            it.subEvent,
                            it.selectedTags,
                            onSubEventClickListener,
                            isApproved
                        )
                    )
                }
            }
        )
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
//        var mPosition = 0
//        Handler().post(Runnable {
//            selectDay(day)
//            val item =
//                calendarSection.findItemBy<CalendarHorizontalListItem> { it.scrollToDay(day) }
//            if (item != null) {
//                mPosition = calendarSection.getPosition(item)
//                calendarPager?.setCurrentItem(mPosition, true)
//            }
//        })
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

    override fun scrollContent(day: EventScheduleCalendarDay) {
        val date = defaultServerDateFormatter.format(day.millis)
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDay() == date }
        if (group != null) {
            val position = groupAdapter.getAdapterPosition(group)
            val mLayoutManager = mBinding.activitiesList.layoutManager as LinearLayoutManager
            mSmoothScroller.targetPosition = position
            mLayoutManager.startSmoothScroll(mSmoothScroller)
        }
    }


    override fun showEmptyEventPlaceholder() {
        calendarSection.update(emptyList())
        tagsSection.update(emptyList())
        eventsSection.updateItem(
            NoEventItem(
                "Нет результатов",
                "По заданным параметрам нет подходящих событий"
            )
        )
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }


    override fun showScheme(eventId: String) {
        val args = DestinationSchemeFragmentArgs.Builder(eventId).build().toBundle()
        findNavController().navigate(R.id.destination_scheme_fragment, args)
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        eventsSection.findItemBy<EventActivityItem> { it -> it.id == idLong }
            ?.notifyChanged(subEvent)
    }


    private val mSmoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }
}