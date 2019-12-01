package com.example.ui.event.list

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.Event
import com.example.extensions.dp
import com.example.holders.EventItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*

abstract class EventListFragment<P : EventListContract.Presenter> : BaseFragment(), EventListContract.View {

    abstract var presenter: P

    val headGroup = Section()
    val dataGroup = Section()

    val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        add(headGroup)
        add(dataGroup)
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                if (position < headGroup.itemCount) return
                presenter.onItemTake(position)
            }
        })
    }

    private val onEventClickListener = object : EventItem.OnEventClickListener {
        override fun onActionRegister(event: Event) = presenter.onActionRegister(event)
        override fun onActionShowEvent(event: Event) = presenter.onShowEventClick(event)
        override fun onActionCancel(event: Event) = presenter.onActionCancel(event)
        override fun onActionWriteToOrganization(event: Event) = presenter.onActionWriteToOrganization(event)
        override fun onShowEventClick(event: Event) = presenter.onShowEventClick(event)
        override fun onShowFilterClick(event: Event) = presenter.onShowFilterClick(event)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@EventListFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })

            clipToPadding = false
            setPadding(0, 16.dp, 0, 0)
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(events: List<Event?>) {
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventItem(it, onEventClickListener)
        })
        swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        swipeToRefresh.isRefreshing = false
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event.id).build().toBundle())
    }

    override fun layout() = R.layout.layout_list
}
