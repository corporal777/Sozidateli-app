package com.example.ui.eventsTabs

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_events_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventsTabsFragment : BaseFragment(), EventsTabsContract.View {

    @InjectPresenter
    lateinit var presenter: EventsTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventsTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventsTabsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> presenter.onRecommendedTabClick()
                        1 -> presenter.onSubscriptionsClick()
                        2 -> presenter.onEventsClick()
                    }
                }
            })

            addTab(newTab().apply { setText(R.string.tab_recommended_title) }, true)
            addTab(newTab().apply { setText(R.string.tab_subscriptions_title) })
            addTab(newTab().apply { setText(R.string.tab_events_title) })
        }
    }

    override fun selectRecommendedTab() {
        tabLayout.setScrollPosition(0, 0f, true)
        findNavController().navigate(R.id.recommendations_fragment)
    }

    override fun selectSubscriptionsTab() {
        tabLayout.setScrollPosition(1, 0f, true)
        findNavController().navigate(R.id.subscriptions_fragment)
    }

    override fun selectEventsTab() {
        tabLayout.setScrollPosition(2, 0f, true)
        findNavController().navigate(R.id.my_events_fragment)
    }

    private fun findNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    override fun layout() = R.layout.fragment_events_tabs
}
