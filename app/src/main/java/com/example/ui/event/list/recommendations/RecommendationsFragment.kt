package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.holders.*
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.list.EventListFragment
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.tabs.SearchTabsFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.ChangeStateDialog
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.StateType
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.notifications.NotificationsView
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.IS_EXPANDED
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_recommendations.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_list.recyclerView
import kotlinx.android.synthetic.main.layout_list.swipeToRefresh
import javax.inject.Inject
import javax.inject.Provider

class RecommendationsFragment : BaseFragment(), RecommendationsContract.View {

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
        override fun onActionCancel(event: String, registrationId: String?) = presenter.onActionCancel(event, registrationId)
        override fun onShowEventClick(view: View, event: String) {
            eventToShowView = view
            presenter.onShowEventClick(event)
        }
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            adapter = this@RecommendationsFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }

        initCollapseLabel()
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        etSearch.setOnClickListener {
            presenter.onSearchClick()
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
        Log.e("EventsList", "start")
        Log.e("EventsList", "size: " + events.size)
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventGroupNew(
                it,
                onEventClickListener,
            )
        })
        Log.e("EventsList", "finish")
        swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        swipeToRefresh.isRefreshing = false
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    private fun initCollapseLabel() {
        appBarLayout.addOnOffsetChangedListener(
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
                        TO_COLLAPSED -> {
                            IS_EXPANDED = false
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
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (IS_EXPANDED){
            tvLabelLarge.visibility = View.VISIBLE
            tvLabelSmall.visibility = View.GONE
        }else {
            tvLabelLarge.visibility = View.GONE
            tvLabelSmall.visibility = View.VISIBLE
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