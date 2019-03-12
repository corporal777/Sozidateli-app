package com.example.ui.eventsTabs

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.myEvents.MyEventsFragment
import com.example.ui.organizations.subscribe.SubscribeOrganizationsFragment
import com.example.ui.recommendations.RecommendationsFragment
import com.example.util.Utils
import kotlinx.android.synthetic.main.fragment_events_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventListFragment : BaseFragment(), EventListContract.View {

    @InjectPresenter
    lateinit var presenter: EventListPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventListPresenter>

    @ProvidePresenter
    fun providePresenter(): EventListPresenter = presenterProvider.get()

    private val tabsContent by lazy {
        listOf(
                getString(R.string.tab_recommended_title) to RecommendationsFragment(),
                getString(R.string.tab_subscriptions_title) to SubscribeOrganizationsFragment(),
                getString(R.string.tab_events_title) to MyEventsFragment()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewPager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(childFragmentManager) {
            override fun getPageTitle(position: Int) = tabsContent[position].first
            override fun getItem(position: Int) = tabsContent[position].second
            override fun getCount() = tabsContent.size
        }

        viewPager.run {
            offscreenPageLimit = tabsContent.size
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun showChat() {
        findNavController().navigate(EventListFragmentDirections.actionEventsTabsFragmentToChatNavigation())
    }

    override fun showSearch() {
        findNavController().navigate(EventListFragmentDirections.mainToSearch())
    }

    override fun showAccount() {
        findNavController().navigate(EventListFragmentDirections.mainToProfile())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        Utils.processMainMenu(menu, {
            presenter.onMenuChatClick()
        }, {
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

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_events_tabs
}
