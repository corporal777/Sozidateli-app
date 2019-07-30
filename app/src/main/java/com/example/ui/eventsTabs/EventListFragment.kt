package com.example.ui.eventsTabs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.my.MyEventsFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.organizations.subscribe.SubscribeOrganizationsFragment
import com.example.ui.views.chatView.ChatView
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
                getString(R.string.tab_recommended_title) to RecommendationsFragment(),
                getString(R.string.tab_subscriptions_title) to SubscribeOrganizationsFragment(),
                getString(R.string.tab_events_title) to MyEventsFragment()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val imageSize = resources.getDimensionPixelSize(R.dimen.toolbar_content_button_size)
        toolbarContentActionBar.apply {
            addLeftView(ChatView(requireContext()).also { it.setOnClickListener { presenter.onMenuChatClick() } })
            addRightView(AppCompatImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(imageSize, imageSize)
                setImageResource(R.drawable.avatar_placeholder)
                setOnClickListener { presenter.onMenuAccountClick() }
            })
        }
    }

    override fun layout() = R.layout.fragment_events_tabs
}
