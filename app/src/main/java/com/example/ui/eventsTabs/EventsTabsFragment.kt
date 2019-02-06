package com.example.ui.eventsTabs

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.util.Utils
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
        setHasOptionsMenu(true)
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

            addTab(newTab().apply { setText(R.string.tab_recommended_title) }, false)
            addTab(newTab().apply { setText(R.string.tab_subscriptions_title) }, false)
            addTab(newTab().apply { setText(R.string.tab_events_title) }, false)
        }
    }

    override fun selectRecommendedTab() {
        tabLayout.setScrollPosition(0, 0f, true)
        findNestedNavController().navigate(R.id.recommendations_fragment)
    }

    override fun selectSubscriptionsTab() {
        tabLayout.setScrollPosition(1, 0f, true)
        findNestedNavController().navigate(R.id.subscriptions_fragment)
    }

    override fun selectEventsTab() {
        tabLayout.setScrollPosition(2, 0f, true)
        findNestedNavController().navigate(R.id.my_events_fragment)
    }

    override fun showChat() {
        findNavController().navigate(EventsTabsFragmentDirections.actionEventsTabsFragmentToChatNavigation())
    }

    override fun showSearch() {
        findNavController().navigate(EventsTabsFragmentDirections.mainToSearch())
    }

    override fun showAccount() {
        findNavController().navigate(EventsTabsFragmentDirections.mainToProfile())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        Utils.processMainMenu(menu,{
            presenter.onMenuChatClick()
        },{
            presenter.onMenuAccountClick()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> presenter.onMenuSearchClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun findNestedNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_events_tabs
}
