package com.example.ui.recommendations

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagedList
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Event
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.PositionOffsetScrollListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_events_list.*
import kotlinx.android.synthetic.main.item_event.*
import setDatesIntervalText
import javax.inject.Inject
import javax.inject.Provider

class RecommendationsFragment : BaseNestedNavigationFragment(), RecommendationsContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = "RecommendationsPresenter")
    lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "RecommendationsPresenter")
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get()

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

                    Picasso.get().load(item.logo).placeholder(R.drawable.ic_launcher_background).into(ivLogo)

                    tvOrganizationLabel.text = item.organization?.name
                    tvEventLabel.text = item.name
                    tvEventDate.setDatesIntervalText(item.conference_start, item.conference_finish)

                    btnGoToEvent.apply {
                        setOnClickListener { presenter.onGoToEventClick(item) }
                        visibility = View.VISIBLE
                    }

                    tvStatus.apply { visibility = View.GONE }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@RecommendationsFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }
    }

    override fun setData(events: PagedList<Event>) {
        adapter.submitList(events)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showAboutEvent(event: Event, vararg sharedElements: Pair<android.view.View, String>) {
        val extras = FragmentNavigatorExtras(*sharedElements)
        findParentNavigation().navigate(
                R.id.about_event_navigation,
                bundleOf("event" to event),
                null,
                extras
        )
    }

    override fun showEventRequest(event: Event) {
        findParentNavigation().navigate(R.id.request_fragment, bundleOf("event" to event))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_events_list
}
