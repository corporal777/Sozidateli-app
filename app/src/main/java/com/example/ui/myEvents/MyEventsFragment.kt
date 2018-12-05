package com.example.ui.myEvents

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import androidx.navigation.fragment.FragmentNavigatorExtras
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Event
import com.example.data.models.Status
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_events_list.*
import kotlinx.android.synthetic.main.item_event.*
import setDatesIntervalText
import javax.inject.Inject
import javax.inject.Provider

class MyEventsFragment : BaseNestedNavigationFragment(), MyEventsContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = "MyEventsPresenter")
    lateinit var presenter: MyEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "MyEventsPresenter")
    fun providePresenter(): MyEventsPresenter = presenterProvider.get()

    private val adapter: SimplePagingRecyclerViewAdapter<Event> by lazy {
        object : SimplePagingRecyclerViewAdapter<Event>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_event

            override fun onBindItem(viewHolder: ViewHolder, item: Event?, position: Int) {
                item!!
                viewHolder.apply {
                    itemContainer.apply {
                        clipToOutline = true
                        alpha = if (item.status === Status.FINISHED) 0.5f else 1f
                        setOnClickListener {
                            presenter.onEventClick(
                                    item,
                                    ivLogo to "logo",
                                    tvOrganizationLabel to "organizationLabel",
                                    tvEventLabel to "eventLabel",
                                    tvEventDate to "eventDate"
                            )
                        }
                    }

                    Picasso.get().load(item.logo).placeholder(R.drawable.ic_launcher).into(ivLogo)

                    tvOrganizationLabel.text = item.organizationName
                    tvEventLabel.text = item.name
                    tvEventDate.setDatesIntervalText(item.startDate, item.finishDate)

                    btnGoToEvent.apply { visibility = View.GONE }
                    tvStatus.apply {
                        visibility = View.VISIBLE

                        val textColor: Int
                        val textBackground: Int
                        val textRes: Int
                        when (item.status) {
                            Status.APPROVED -> {
                                textColor = R.color.event_status_approved_text
                                textBackground = R.color.event_status_approved_background
                                textRes = R.string.event_status_approved
                            }
                            Status.CONFIRMATION_EXPECTED -> {
                                textColor = R.color.event_status_wait_confirmation_text
                                textBackground = R.color.event_status_wait_confirmation_background
                                textRes = R.string.event_status_wait_confirmation
                            }
                            else -> {
                                textColor = R.color.event_status_finished_text
                                textBackground = R.color.event_status_finished_background
                                textRes = R.string.event_status_finished
                            }
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
            adapter = this@MyEventsFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }
    }

    override fun setData(events: PagedList<Event>) {
        adapter.submitList(events)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showAboutEvent(event: Event, vararg sharedElements: Pair<android.view.View, String>) {
        val extras = FragmentNavigatorExtras(*sharedElements)
        findParentNavigation().navigate(R.id.about_event_navigation, bundleOf("event" to event), null, extras)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_events_list
}
