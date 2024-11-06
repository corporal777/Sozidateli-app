package com.example.ui.event.my

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.app.databinding.FragmentMyEventsBinding
import com.example.extensions.*
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventListItem
import com.example.ui.event.list.EventListFragment
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.views.filters.event.my.MyEventsFiltersBottomSheetDialog
import com.example.util.SearchInput
import com.example.util.pagination.PaginationGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.offsetChangedListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class MyEventsFragment : EventListFragment<MyEventsPresenter, FragmentMyEventsBinding>(),
    MyEventsContract.View {

    @InjectPresenter
    override lateinit var presenter: MyEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyEventsPresenter = presenterProvider.get()

    private val eventsSection = Section()
    private val groupAdapter = PaginationGroupAdapter<GroupieViewHolder>().apply {
        add(eventsSection)
        setOnItemTakeCallback(object : PaginationGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                presenter.onItemTake(position)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = groupAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            etSearch.apply {
                SearchInput(this).apply {
                    setOnFocusChange { hasFocus ->
                        clSearch.setBackgroundResource(
                            if (hasFocus) R.drawable.background_search_field_rounded_focused
                            else R.drawable.background_search_field_rounded_normal
                        )
                    }
                    setOnAfterTextChange {
                        btnClear.isVisible = !it.isNullOrEmpty()
                        presenter.onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        presenter.onSearchTextChange(it)
                        hideKeyboard()
                    }
                }
            }
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { etSearch.text = null }
            }
            btnFilter.setOnClickListener { presenter.onShowFiltersClick() }
            btnDeclined.setOnCheckedChangeListener { _, isChecked ->
                presenter.onEventStateFiltersClick(isChecked, MyEventsFilter.DECLINED)
            }
            btnApproved.setOnCheckedChangeListener { _, isChecked ->
                presenter.onEventStateFiltersClick(isChecked, MyEventsFilter.APPROVED)
            }
            btnPending.setOnCheckedChangeListener { _, isChecked ->
                presenter.onEventStateFiltersClick(isChecked, MyEventsFilter.PENDING)
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            appBarLayout.offsetChangedListener { appBarLayout, offset ->
                updateAppBarViews(abs(offset / appBarLayout.totalScrollRange.toFloat()))
            }
        }

    }

    override fun setData(data: List<EventNew?>) {
        mBinding.swipeToRefresh.isRefreshing = false
        eventsSection.update(data.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventListItem(it, presenter.isTemporaryUser(), onEventClickListener)
        })
    }

    override fun showFilters() {
        MyEventsFiltersBottomSheetDialog(requireContext(), presenter.searchFilter)
            .setFiltersSelected { presenter.onSearchFiltersClick(it) }
            .show()
    }

    override fun setFiltersChosen(isChosen: Boolean) {
        mBinding.btnFilter.apply {
            if (isChosen) setImageResource(R.drawable.ic_filters_selected)
            else setImageResource(R.drawable.ic_filters_new)
        }
    }

    override fun showEmptyListPlaceholder(isFirst: Boolean) {
        var titlePlaceholder = getString(R.string.no_data_found)
        var textPlaceholder = getString(R.string.no_event_with_params_title)

        if (isFirst) {
            titlePlaceholder = getString(R.string.no_event_schedule_you_have)
            textPlaceholder = getString(R.string.choose_event_and_do_request)
        }
        eventsSection.updateItem(
            NoScheduleEventItem(
                titlePlaceholder,
                textPlaceholder,
                60.dp
            )
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateEvent(event: EventNew) {
        val id = event.id?.toLong()
        eventsSection.findItemBy<EventListItem> { x -> x.id == id }?.notifyChanged(event)
    }

    override fun setShowScheduleEvents(canShow: Boolean) {
        mBinding.toolbar.apply {
            isVisible = true
            mBinding.btnGoToMyTimeTable.setOnClickListener {
                findNavController().navigate(R.id.my_schedule_events_fragment)
            }
        }
    }

    override fun scrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }

    override fun onExpandedState() {
        mBinding.tvLabelLarge.apply {
            visibility = View.VISIBLE
            alpha = 0F
            animate().setDuration(500).alpha(1.0f)
        }
    }

    override fun onCollapsedState() {
        mBinding.tvLabelLarge.apply {
            alpha = 1F
            animate().setDuration(500).alpha(0.0f)
            visibility = View.GONE
        }
    }

    override fun layout(): Int = R.layout.fragment_my_events
}