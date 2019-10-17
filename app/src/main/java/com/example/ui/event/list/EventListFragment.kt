package com.example.ui.event.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.Event
import com.example.extensions.dp
import com.example.holders.EventItem
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.util.ARG_EVENT
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*

abstract class EventListFragment<P : EventListContract.Presenter> : BaseNestedNavigationFragment(), EventListContract.View {

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
    }

    override fun setData(events: List<Event>) {
        adapter.update(events.map {
            EventItem(
                    it,
                    { presenter.onEventClick(it) },
                    { presenter.onGoToEventClick(it) }
            )
        })
        placeholderUtil.isDataLoad = true
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showAboutEvent(event: String) {
        findParentNavigation().navigate(R.id.about_event, AboutEventFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: Event) {
        findParentNavigation().navigate(R.id.request_fragment, bundleOf(ARG_EVENT to event))
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}
