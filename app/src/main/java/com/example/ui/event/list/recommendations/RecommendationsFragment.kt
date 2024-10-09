package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.data.models.EventNew
import com.example.app.databinding.FragmentRecommendationsBinding
import com.example.extensions.dp
import com.example.extensions.findGroupBy
import com.example.extensions.updateGroup
import com.example.extensions.updateItem
import com.example.ui.event.list.EventListFragment
import com.example.ui.event.list.recommendations.items.RecommendationItemsGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.profile.ProfileFragmentArgs
import com.example.util.pagination.PaginationGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.offsetChangedListener
import com.example.extensions.setOnClickListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class RecommendationsFragment : EventListFragment<RecommendationsPresenter, FragmentRecommendationsBinding>(),
    RecommendationsContract.View {

    @InjectPresenter
    override lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>

    @ProvidePresenter
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get().apply {
        try {
            val args = RecommendationsFragmentArgs.fromBundle(requireArguments())
            this.onShowSavedEventOrProfile(args.isOpenProfile)
        } catch (_: Exception) { }
    }

    private val dataGroup = Section()
    private val groupAdapter = PaginationGroupAdapter<GroupieViewHolder>().apply {
        add(dataGroup)
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
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            etSearch.setOnClickListener { presenter.onSearchClick() }
            btnLogin.setOnClickListener { presenter.onAuthorizationClick() }
            appBarLayout.offsetChangedListener { appBarLayout, i ->
                updateAppBarViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
            }
        }
    }

    override fun setAuthorizationButton(isTemporary: Boolean) {
        mBinding.btnLogin.isVisible = isTemporary
    }

    override fun setData(events: List<EventNew?>, isNeedUpdateApp: Boolean?) {
        val group = dataGroup.findGroupBy<RecommendationItemsGroup> { true }
        if (group == null)
            dataGroup.updateGroup(
                RecommendationItemsGroup(
                    events,
                    presenter.isTemporaryUser(),
                    isNeedUpdateApp,
                    onEventClickListener
                )
            )
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

    override fun scrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }


    override fun showSearch() {
        findNavController().navigate(R.id.search_tabs_fragment)
    }

    override fun showUserProfile() {
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