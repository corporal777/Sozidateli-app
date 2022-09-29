package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.FragmentRecommendationsBinding
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.StateType
import com.example.util.IS_EXPANDED
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

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
                presenter.onItemTake(position)
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
//                addOnScrollListener(PositionOffsetScrollListener { position, offset ->
//                    presenter.onScrollChange(position, offset)
//                })
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            etSearch.setOnClickListener {
                presenter.onSearchClick()
            }
        }
        initCollapseLabel()
    }

    override fun setData(events: List<EventNew?>) {
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            //else EventGroupNew(
            else EventItemNew(
                it,
                it.id.toString(),
                it.state,
                it.status?.value,
                it.binds?.currentUserRegistration?.status?.value,
                it.binds?.organization?.backgroundColor?.value,
                it.image?.uri,
                it.binds?.eventRegistrationState,
                it.userAgreement?.uri,
                it.binds?.currentUserRegistration?.id.toString(),
                it.name,
                it.address?.getShortAddress(),
                it.holdingDate?.from,
                it.holdingDate?.to,
                onEventClickListener,
            )
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateActionButton(event: EventNew?) {
        val id = event?.id?.toLong()
        dataGroup.findItemBy<EventItemNew> { x -> x.id == id }?.notifyChanged(event)
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.update(emptyList())
        mBinding.noDataPlaceholder.isInvisible = false
//        dataGroup.update(listOf(
//            NoEventItem(
//                getString(R.string.no_result_found_lable),
//                getString(R.string.no_event_with_params_title)
//            )))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun hideEmptyListPlaceholder() {
        mBinding.noDataPlaceholder.isInvisible = true
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

    private fun initCollapseLabel() {
        mBinding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
            })
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
                            IS_EXPANDED = true
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
                            IS_EXPANDED = false
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