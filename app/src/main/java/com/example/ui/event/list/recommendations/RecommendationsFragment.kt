package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.FragmentRecommendationsBinding
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.StateType
import com.example.util.IS_EXPANDED
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
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

    private var eventToShowView: View? = null
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

        override fun onShowEventClick(view: View, event: String) {
            eventToShowView = view
            presenter.onShowEventClick(event)
        }

        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = this@RecommendationsFragment.adapter
                addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                    presenter.onScrollChange(position, offset)
                })
            }
        }
        initCollapseLabel()
        mBinding.swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        mBinding.etSearch.setOnClickListener {
            presenter.onSearchClick()
        }
        mBinding.fabUp.setOnClickListener {
            smoothScrollToFirstItem()
        }
    }


    override fun showSearch() {
        findNavController().navigate(
            RecommendationsFragmentDirections.recommendationsFragmentToSearchFragment(
                null
            )
        )
    }

    override fun setData(events: List<EventNew?>) {
        mBinding.noDataPlaceholder.isVisible = false
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventGroupNew(
                it,
                onEventClickListener,
            )
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.clear()
        mBinding.noDataPlaceholder.isVisible = true
        //dataGroup.update(listOf(NoScheduleEventItem(getString(R.string.empty_list_placeholder_message))))
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (mBinding.eventsList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
    }


    private fun smoothScrollToFirstItem() {
        val height = mBinding.eventsList.height
        val mSmoothScroller by lazy {
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_END
                }

                override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
                    // return super.calculateDxToMakeVisible(view, snapPreference) - dp2px(500f)
                    return super.calculateDxToMakeVisible(view, snapPreference) - dp2px(height.toFloat())
                }

                override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
                    return super.calculateDyToMakeVisible(view, snapPreference) - dp2px(140f)
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 10f / displayMetrics.densityDpi
                }
            }
        }
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mSmoothScroller.targetPosition = 0
        mLayoutManager.startSmoothScroll(mSmoothScroller)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle()
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

    fun dp2px(dpValue: Float): Int {
        val scale: Float = requireContext().resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}