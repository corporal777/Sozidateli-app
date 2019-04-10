package com.example.ui.myEvents

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.paging.PagedList
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Event
import com.example.data.models.EventRegisterResponse
import com.example.data.models.Status
import com.example.extensions.dp
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_event.*
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
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

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val adapter: SimplePagingRecyclerViewAdapter<EventRegisterResponse> by lazy {
        object : SimplePagingRecyclerViewAdapter<EventRegisterResponse>(
                { oldItem, newItem -> oldItem.event_id == newItem.event_id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_event

            override fun onBindItem(viewHolder: ViewHolder, item: EventRegisterResponse?, position: Int) {
                val event = item?.event!!
                viewHolder.apply {
                    itemContainer.apply {
                        clipToOutline = true
                        alpha = if (event.status === Status.CONFERENCE_ENDS.code) 0.5f else 1f
                        setOnClickListener { presenter.onEventClick(event) }
                    }

                    Picasso.get().load(event.logo).placeholder(R.drawable.ic_launcher_background).into(ivLogo)

                    tvOrganizationLabel.text = event.organization?.name
                    tvEventLabel.text = event.name
                    tvEventDate.setDatesIntervalText(event.conference_start, event.conference_finish)

                    btnGoToEvent.apply { visibility = View.GONE }
                    tvStatus.apply {
                        visibility = View.VISIBLE

                        val textColor: Int
                        val textBackground: Int
                        val textRes: Int
                        when (event.status) {
                            Status.APPROVED.code -> {
                                textColor = R.color.event_status_approved_text
                                textBackground = R.color.event_status_approved_background
                                textRes = R.string.event_status_approved
                            }
                            Status.CONFIRMATION_EXPECTED.code -> {
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

            clipToPadding = false
            setPadding(0, 16.dp, 0, 0)
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
    }

    override fun setData(events: PagedList<EventRegisterResponse>) {
        adapter.submitList(events)
        placeholderUtil.isDataLoad = true
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun selectEvent(event: Event) {
        findParentNavigation().apply {
            graph.startDestination = R.id.event_tabs_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.event_list_fragment, true)
                    .build()
            navigate(R.id.event_tabs_fragment, null, opts)
        }
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.layout_list_with_placeholder
}
