package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.FragmentRecommendationsBinding
import com.example.extensions.*
import com.example.ui.event.list.EventListFragment
import com.example.ui.event.list.recommendations.items.RecommendationItemsGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.profile.ProfileFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import offsetChangedListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class RecommendationsFragment :
    EventListFragment<RecommendationsPresenter, FragmentRecommendationsBinding>(),
    RecommendationsContract.View {

    @InjectPresenter
    override lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>


    @ProvidePresenter
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get().apply {
        try {
            val args = RecommendationsFragmentArgs.fromBundle(requireArguments())
            if (args.isOpenProfile) showUserProfile()
        } catch (e: Exception) {

        }
    }

    private val dataGroup = Section()
    private val groupAdapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        add(dataGroup)
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                if (position > 0) presenter.onItemTake(position - 1)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = groupAdapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            etSearch.setOnClickListener { presenter.onSearchClick() }
            appBarLayout.offsetChangedListener { appBarLayout, i ->
                updateAppBarViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
            }
        }
    }

    override fun setData(events: List<EventNew?>, isNeedUpdateApp: Boolean?) {
        val group = dataGroup.findGroupBy<RecommendationItemsGroup> { true }
        if (group == null)
            dataGroup.updateGroup(RecommendationItemsGroup(events, isNeedUpdateApp, onEventClickListener))
        else group.updateItems(events, isNeedUpdateApp)

        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun updateEvent(event: EventNew) {
        dataGroup.findGroupBy<RecommendationItemsGroup> { true }?.updateButtonState(event)
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.updateItem(
            NoScheduleEventItem(
                getString(R.string.no_active_events_found_title),
                padding = 70.dp
            )
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    fun smoothScrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }


    override fun showSearch() {
        findNavController().navigate(R.id.search_tabs_fragment)
    }

    private fun showUserProfile() {
        val args = ProfileFragmentArgs.Builder(true).build().toBundle()
        findNavController().navigate(R.id.profile_fragment, args)
    }

    override fun onExpandedState() {
        mBinding.apply {
            tvLabelSmall.apply {
                alpha = 1F
                animate().setDuration(500).alpha(0.0f)
                visibility = View.GONE
            }
            tvLabelLarge.apply {
                visibility = View.VISIBLE
                alpha = 0F
                animate().setDuration(500).alpha(1.0f)
            }
        }
    }

    override fun onCollapsedState() {
        mBinding.apply {
            tvLabelSmall.apply {
                alpha = 0F
                animate().setDuration(500).alpha(1.0f)
                tvLabelSmall.visibility = View.VISIBLE
            }
            tvLabelLarge.apply {
                alpha = 1F
                animate().setDuration(500).alpha(0.0f)
                visibility = View.GONE
            }
        }
    }

    override fun layout(): Int = R.layout.fragment_recommendations

}