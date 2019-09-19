package com.example.ui.event.list

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Event
import com.example.data.models.EventApprove
import com.example.data.models.StatusEvent
import com.example.extensions.dateFormatterShortMothShortYear
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.dp
import com.example.extensions.parseAndFormat
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.ARG_EVENT
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_event.*
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*

abstract class EventListFragment<P : EventListContract.Presenter> : BaseNestedNavigationFragment(), EventListContract.View {

    abstract var presenter: P

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val adapter: SimplePagingRecyclerViewAdapter<EventApprove> by lazy {
        object : SimplePagingRecyclerViewAdapter<EventApprove>(
                { oldItem, newItem -> oldItem.event.id == newItem.event.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {

            private val backgroundOverlayColor by lazy {
                ResourcesCompat.getColor(resources, R.color.auth_background_overlay, null)
            }

            override fun getItemLayout(itemView: Int) = R.layout.item_event

            override fun onBindItem(viewHolder: ViewHolder, item: EventApprove?, position: Int) {
                val event = item!!.event
                viewHolder.apply {
                    itemContainer.apply {
                        clipToOutline = true
                        alpha = if (event.status === StatusEvent.CONFERENCE_ENDS.code) 0.5f else 1f

                        setOnClickListener {
                            presenter.onEventClick(
                                    event,
                                    ivBackground to "logo",
                                    tvOrganizationLabel to "organizationLabel",
                                    tvEventLabel to "eventLabel",
                                    tvEventDate to "eventDate"
                            )
                        }
                    }

                    ivBackground.apply {
                        Picasso.get().load(event.logo).placeholder(R.mipmap.ic_launcher_background).into(this)
                        setColorFilter(backgroundOverlayColor, PorterDuff.Mode.DARKEN)
                    }

                    tvOrganizationLabel.apply {
                        text = event.organization?.name
                    }
                    tvEventLabel.text = event.name
                    tvEventDate.apply {
                        val start = event.conference_start
                        val finish = event.conference_finish

                        val parser = defaultServerDateFormatter
                        val formatter = dateFormatterShortMothShortYear
                        text = if (start != null && finish != null) {
                            "${start.parseAndFormat(parser, formatter)} - ${finish.parseAndFormat(parser, formatter)}"
                        } else {
                            start?.parseAndFormat(parser, formatter)
                        }
                    }

                    tvShowMore.apply {

                    }

                    when (item.status) {
                        EventApprove.Status.EMPTY -> showRegisterToEvent(viewHolder, event)
                        else -> showApproveStatus(viewHolder, event, item.status)
                    }
                }
            }

            private fun showRegisterToEvent(viewHolder: ViewHolder, event: Event) {
                viewHolder.apply {
                    tvStatus.visibility = View.GONE
                    btnGoToEvent.apply {
                        setOnClickListener { presenter.onGoToEventClick(event) }
                        visibility = View.VISIBLE
                    }
                }
            }

            private fun showApproveStatus(viewHolder: ViewHolder, event: Event, status: EventApprove.Status) {
                viewHolder.apply {
                    btnGoToEvent.apply { visibility = View.GONE }
                    tvStatus.apply {
                        visibility = View.VISIBLE

                        var textColor: Int
                        var textBackground: Int
                        var textRes: Int
                        when (status) {
                            EventApprove.Status.APPROVED -> {
                                textColor = R.color.event_status_approved_text
                                textBackground = R.color.event_status_approved_background
                                textRes = R.string.event_status_approved
                            }
                            EventApprove.Status.DECLINED -> {
                                textColor = R.color.event_status_wait_confirmation_text
                                textBackground = R.color.red
                                textRes = R.string.event_status_decline
                            }
                            else -> {
                                textColor = R.color.event_status_wait_confirmation_text
                                textBackground = R.color.event_status_wait_confirmation_background
                                textRes = R.string.event_status_wait_confirmation
                            }


                        }
                        if (event.status === StatusEvent.CONFERENCE_ENDS.code) {
                            textColor = R.color.event_status_finished_text
                            textBackground = R.color.event_status_finished_background
                            textRes = R.string.event_status_finished
                        }


                        text = getString(textRes)
                        setTextColor(ContextCompat.getColor(context, textColor))
                        setBackgroundColor(ContextCompat.getColor(context, textBackground))
                    }
                }
            }
        }
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

    override fun setData(events: PagedList<EventApprove>) {
        adapter.submitList(events)
        placeholderUtil.isDataLoad = true
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showAboutEvent(event: Event, vararg sharedElements: Pair<View, String>) {
        val extras = FragmentNavigatorExtras(*sharedElements)
        findParentNavigation().navigate(
                R.id.about_event,
                bundleOf("event" to event),
                null,
                extras
        )
    }

    override fun showEventRequest(event: Event) {
        findParentNavigation().navigate(R.id.request_fragment, bundleOf(ARG_EVENT to event))
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}
