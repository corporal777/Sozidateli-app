package com.example.ui.event.activities

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
import com.example.data.models.EventScheduleDay
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.databinding.FragmentActivitysBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.EventDaysListItem
import com.example.holders.PlaceholderItem
import com.example.holders.TagsHorizontalListItem
import com.example.holders.redesign.EventActivityDateItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.event.location.buildingScheme.redesign.DestinationSchemeFragmentArgs
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.util.SearchInput
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrollStateChanged
import onScrolled
import onTextChanged
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
                            scrollCalendar(item.getDay())
                            if (!mCanChangeDay) changeDay(item.getDay())
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
                    setOnTextChange { presenter.onSearchTextChange(it) }
                    setOnTextChangeDone {
                        presenter.onSearchTextSubmit(it)
                        hideKeyboard()
                    }
                }

                onTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                }
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

    override fun setDays(days: Map<Int, List<EventScheduleDay>>) {
        calendarSection.update(
            days.map { EventDaysListItem(it.key, it.value){ day -> presenter.onDaySelected(day) } }
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

    override fun selectDay(day: EventScheduleDay) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as EventDaysListItem
            item.selectDay(day)
        }
    }

    private fun changeDay(day: String?) {
        for (i in 0 until calendarSection.itemCount) {
            val item = calendarSection.getItem(i) as EventDaysListItem
            item.changeDay(day)
        }
    }

    private fun scrollCalendar(date: String?) {
        Handler().post(Runnable {
            val item = calendarSection.findItemBy<EventDaysListItem> { it.isHasDay(date) }
            if (item != null) {
                val position = calendarSection.getPosition(item)
                mBinding.calendarPager.setCurrentItem(position, true)
            }
        })
    }

    override fun scrollContent(day: EventScheduleDay) {
        val group =
            groupAdapter.findItemBy<GroupieViewHolder, EventActivityDateItem> { x -> x.getDay() == day.date }
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