package com.example.ui.eventTabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MapInfo
import com.example.data.models.Place
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.location.EventLocationFragment
import com.example.ui.event.location.EventLocationFragmentArgs
import com.example.ui.event.schedule.complete.EventCompleteScheduleFragment
import com.example.ui.event.schedule.my.EventMyScheduleFragment
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.BottomNavigationViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_event_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventTabsFragment : BaseFragment(), EventTabsContract.View, ToolbarFragment {

    override val title = ""

    @InjectPresenter
    lateinit var presenter: EventTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventTabsPresenter = presenterProvider.get()

    private val bottomNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        presenter.apply {
            when (it.itemId) {
                R.id.tab_event_my_schedule -> onMyScheduleTabSelected()
                R.id.tab_event_schedule -> onScheduleTabSelected()
                R.id.about_event -> onAboutSelected()
                R.id.event_location -> onMapTabsSelected()
                R.id.to_list -> onToListSelected()
                else -> return@OnNavigationItemSelectedListener false
            }
        }

        return@OnNavigationItemSelectedListener true
    }

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomNavigationViewHelper(bottomNavigation).removeShiftMode()
        setHasOptionsMenu(true)
        bottomNavigation.apply { setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener) }
    }

    override fun initialNavigationSetup() {

    }

    override fun showMyScheduleTab() = selectTab(R.id.tab_event_my_schedule)

    override fun showScheduleTab() = selectTab(R.id.tab_event_schedule)

    override fun showAboutTab(eventId: String) = selectTab(R.id.about_event, AboutEventFragmentArgs.Builder(eventId).build().toBundle())

    override fun showMapTab(eventName: String, mapInfo: MapInfo?, places: Array<Place>?) = selectTab(R.id.event_location, EventLocationFragmentArgs.Builder(eventName, mapInfo, places).build().toBundle())

    private fun selectTab(tabId: Int, args: Bundle? = null) {
        val fragmentTag = generateFragmentTag(tabId)
        val newFragment = childFragmentManager.findFragmentByTag(fragmentTag)
                ?: createTabFragment(tabId, args)

        childFragmentManager.beginTransaction()
                .apply {
                    if (!newFragment.isAdded) add(navHostContainer.id, newFragment, fragmentTag)
                }
                .attach(newFragment)
                .apply {
                    for (fragment in childFragmentManager.fragments) {
                        if (fragment != newFragment) detach(fragment)
                    }
                }
                .setCustomAnimations(
                        R.anim.nav_default_enter_anim,
                        R.anim.nav_default_exit_anim,
                        R.anim.nav_default_pop_enter_anim,
                        R.anim.nav_default_pop_exit_anim)
                .setReorderingAllowed(true)
                .commitNow()
    }

    private fun createTabFragment(tabId: Int, args: Bundle?): Fragment {
        val fragment = when (tabId) {
            R.id.tab_event_my_schedule -> EventMyScheduleFragment()
            R.id.tab_event_schedule -> EventCompleteScheduleFragment()
            R.id.about_event -> AboutEventFragment()
            R.id.event_location -> EventLocationFragment()
            else -> throw IllegalArgumentException("No fragment fo tab $tabId")
        }

        args?.let { fragment.arguments = it }
        return fragment
    }

    private fun generateFragmentTag(tabId: Int): String = "tabFragment#$tabId"

    override fun showChat() {
        findNavController().navigate(EventTabsFragmentDirections.actionEventTabsFragmentToChatListTabsFragment())
    }

    override fun showAccount() {
        findNavController().navigate(EventTabsFragmentDirections.mainToProfile())
    }

    override fun showEventList() {
        findNavController().apply {
            graph.startDestination = R.id.event_list_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.event_tabs_fragment, true)
                    .build()
            navigate(R.id.event_list_fragment, null, opts)
        }
    }

    override fun setLabel(label: String) {
        toolbarContentActionBar.title = label
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
        toolbarContentActionBar.apply {
            addLeftView(ChatView(requireContext()).also { it.setOnClickListener { presenter.onMenuChatClick() } })
            addRightView(AccountView(requireContext()).also { it.setOnClickListener { presenter.onMenuAccountClick() } })
        }
    }

    override fun layout() = R.layout.fragment_event_tabs
}
