package com.example.ui.event.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.Event
import com.example.extensions.dp
import com.example.holders.EventItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*

abstract class EventListFragment<P : EventListContract.Presenter> : BaseFragment(), EventListContract.View {

    abstract var presenter: P

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                presenter.onItemTake(position)
            }
        })
    }

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

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(events: List<Event?>) {
        adapter.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventItem(
                    it,
                    { presenter.onEventClick(it) },
                    { presenter.onGoToEventClick(it) }
            )
        })
        placeholderUtil.isDataLoad = true
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

    override fun layout() = R.layout.layout_list_with_placeholder
}
