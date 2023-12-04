package com.example.ui.event.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AbsListView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleDay
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.databinding.FragmentActivitysBinding
import com.example.extensions.calendar
import com.example.extensions.findItemBy
import com.example.extensions.getMonthName
import com.example.extensions.updateItem
import com.example.holders.EventDaysListItem
import com.example.holders.PlaceholderItem
import com.example.holders.TagsHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.event.location.buildingScheme.DestinationSchemeFragmentArgs
import com.example.ui.event.my.schedule.calendar.CalendarHorizontalDaysItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.util.SearchInput
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onFocusChanged
import onScrollStateChanged
import onScrolled
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider


class ActivitiesFragment : BaseFragment<FragmentActivitysBinding>(), ActivitiesContract.View {

    override fun layout(): Int = R.layout.fragment_activitys

    @InjectPresenter
    lateinit var presenter: ActivitiesPresenter

    @Inject
    lateinit var presenterProvider: Provider<ActivitiesPresenter>

    @ProvidePresenter
    fun providePresenter(): ActivitiesPresenter = presenterProvider.get().apply {
        eventId = ActivitiesFragmentArgs.fromBundle(requireArguments()).eventId.toString()
        tagsNew = ActivitiesFragmentArgs.fromBundle(requireArguments()).tags?.map {
            val t = Tag.EventTag(it.id, it.name)
            t.isSelected = it.isSelected
            t
        }
    }

    private var oldPosition = 0
    private val listManager by lazy { LinearLayoutManager(requireContext()) }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            activitiesList.apply {
                layoutManager = listManager
                adapter = groupAdapter
                onScrolled { _, _ ->
                    val lastItem = listManager.findFirstCompletelyVisibleItemPosition()
                    try {
                        if (groupAdapter.getItem(lastItem) is EventActivityDateItem) {
                            val item = groupAdapter.getItem(lastItem) as EventActivityDateItem
                            if (!presenter.isJumping) scrollPageContent(item.getDate())
                        }
                    } catch (e: Exception) {
                    }
                }
            }
            calendarPager.apply {
                adapter = calendarAdapter
                offscreenPageLimit = 3
            }
            ivBack.setOnClickListener { findNavController().navigateUp() }
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { etSearch.text = null }
            }
            etSearch.apply {
                SearchInput(this).apply {
                    setOnFocusChange { hasFocus ->
                        clSearch.setBackgroundResource(
                            if (hasFocus) R.drawable.background_search_field_rounded_focused
                            else R.drawable.background_search_field_rounded_normal
                        )
                    }
                    setOnTextChange {
                        btnClear.isVisible = !it.isNullOrEmpty()
                        presenter.onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        presenter.onSearchTextSubmit(it)
                        hideKeyboard()
                    }
                }

            }
        }
    }

    override fun setContentPlaceholder() {
        calendarSection.updateItem(PlaceholderItem(PlaceholderItem.Type.ACTIVITY_CALENDAR))
        tagsSection.updateItem(PlaceholderItem(PlaceholderItem.Type.ACTIVITY_TAGS))
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

    override fun setDays(days: List<List<EventScheduleDay>>) {
        calendarSection.update(
            days.mapIndexed { i, l ->
                CalendarHorizontalDaysItem(i, l) { day -> presenter.onDaySelected(day) }
            }
        )
        mBinding.clSearch.isInvisible = false
    }

    override fun setTags(tags: List<Tag>?) {
        if (!tags.isNullOrEmpty()) {
            tagsSection.updateItem(TagsHorizontalListItem(tags) { presenter.onTagSelected() })
        } else tagsSection.update(emptyList())
    }

    override fun setSubEvents(isApproved: Boolean, data: List<SubEventsData>) {
        eventsSection.update(
            data.map {
                Section().apply {
                    if (it.titleDate != null) add(EventActivityDateItem(it.titleDate, it.titleDate?.millis))
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
            mBinding.calendarPager.setCurrentItem(position, true)
            val item = calendarSection.getItem(position)
            if (item is CalendarHorizontalDaysItem) item.selectDay(day)
        })
    }

    override fun scrollListContent(day: EventScheduleDay?) {
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDate() == day } ?: return
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
        eventsSection.findItemBy<EventActivityItem> { it -> it.id == idLong }?.notifyChanged(subEvent)
    }

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
}