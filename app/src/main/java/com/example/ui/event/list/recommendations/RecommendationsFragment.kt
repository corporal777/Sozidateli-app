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
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventItemNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.list.recommendations.items.RecommendationItemsGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.StateType
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import offsetChangedListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class RecommendationsFragment : BaseFragmentNew<FragmentRecommendationsBinding>(),
    RecommendationsContract.View {

    @InjectPresenter
    lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>

    private var cashCollapseState: Pair<Int, Int>? = null

    @ProvidePresenter
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get().apply {
        try {
            val args = RecommendationsFragmentArgs.fromBundle(requireArguments())
            if (args.isOpenProfile)
                findNavController().navigate(
                    RecommendationsFragmentDirections.recommendationsFragmentToProfileFragment(
                        true
                    )
                )
        } catch (e: Exception) {

        }
    }

    private val dataGroup = Section()
    val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        add(dataGroup)
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                if (position > 0) presenter.onItemTake(position - 1)
            }
        })
    }

    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)

        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = this@RecommendationsFragment.adapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            etSearch.setOnClickListener { presenter.onSearchClick() }
            appBarLayout.offsetChangedListener { appBarLayout, i ->
                updateViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
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

    override fun updateActionButton(event: EventNew?) {
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

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (mBinding.eventsList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
    }


    fun smoothScrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle()
        )
    }

    override fun showSearch() {
        findNavController().navigate(
            RecommendationsFragmentDirections.recommendationsFragmentToSearchFragment(
                null
            )
        )
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }


    private fun updateViews(offset: Float) {

        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED -> {
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
                        TO_COLLAPSED -> {
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
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }
        }
    }

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }

    override fun layout(): Int = R.layout.fragment_recommendations

}