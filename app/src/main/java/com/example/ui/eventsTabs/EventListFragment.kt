package com.example.ui.eventsTabs

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.TabsFragmentAdapter
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.my.MyEventsFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.organizations.favorites.FavoriteOrganizationsFragment
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.toolbar.ToolbarButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import kotlinx.android.synthetic.main.fragment_events_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventListFragment : BaseFragment(), EventListContract.View, ToolbarFragment {

    override val title: String
        get() = getString(R.string.events_tabs_title)

    @InjectPresenter
    lateinit var presenter: EventListPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventListPresenter>

    @ProvidePresenter
    fun providePresenter(): EventListPresenter = presenterProvider.get()

    private val tabsContent by lazy {
        listOf(
                RecommendationsFragment() to getString(R.string.tab_recommended_title),
                FavoriteOrganizationsFragment() to getString(R.string.tab_subscriptions_title),
                MyEventsFragment() to getString(R.string.tab_events_title)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = TabsFragmentAdapter(tabsContent, childFragmentManager)

        viewPager.run {
            offscreenPageLimit = tabsContent.size
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun showChat() {
        findNavController().navigate(EventListFragmentDirections.actionEventsTabsFragmentToChatListTabsFragment())
    }

    override fun showSearch() {
        findNavController().navigate(EventListFragmentDirections.mainToSearch())
    }

    override fun showAccount() {
        findNavController().navigate(EventListFragmentDirections.mainToProfile())
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        toolbarContentActionBar.apply {
            addLeftView(ChatView(requireContext()).also { it.setOnClickListener { presenter.onMenuChatClick() } })
            addRightView(AccountView(requireContext()).also { it.setOnClickListener { presenter.onMenuAccountClick() } })
            addRightView(ToolbarButton(requireContext()).apply {
                setImageResource(R.drawable.ic_search)
                setOnClickListener { presenter.onMenuSearchClick() }
            })
        }
    }

    override fun layout() = R.layout.fragment_events_tabs
}
