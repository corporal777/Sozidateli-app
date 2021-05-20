package com.example.ui.eventTabs

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MapInfo
import com.example.data.models.Place
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.interfaces.NavBarColorFragment
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragment
import com.example.ui.event.about.AboutEventFragment.Companion.ABOUT_FROM_EVENT
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.location.EventLocationFragment
import com.example.ui.event.location.EventLocationFragmentArgs
import com.example.ui.event.schedule.complete.EventCompleteScheduleFragment
import com.example.ui.event.schedule.my.EventMyScheduleFragment
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.notifications.NotificationsView
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.BottomNavigationViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_event_tabs.*
import kotlinx.android.synthetic.main.item_action_button.view.*
import javax.inject.Inject
import javax.inject.Provider

class EventTabsFragment : BaseFragment(), EventTabsContract.View, ToolbarFragment, DoNotCheckConnectionFragment, NavBarColorFragment {

    override val title: String? = null

    override val navBarColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.navBarTabs)
    }

    @InjectPresenter
    lateinit var presenter: EventTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventTabsPresenter = presenterProvider.get()

    private var noInternetDialog: BottomSheetDialog? = null

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

    private val childFragmentCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            val tabId = when (f) {
                is EventMyScheduleFragment -> R.id.tab_event_my_schedule
                is EventCompleteScheduleFragment -> R.id.tab_event_schedule
                is AboutEventFragment -> R.id.about_event
                is EventLocationFragment -> R.id.event_location
                else -> throw IllegalArgumentException("No tab fo fragment $f")
            }

            bottomNavigation.apply {
                val itemLocation = menu.findItem(R.id.event_location)
                //TODO need to fix
                //itemLocation.isVisible = !presenter.userEvent.eventInfo.event.address.isNullOrBlank()
                setOnNavigationItemSelectedListener(null)
                selectedItemId = tabId
                setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener)
            }
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            presenter.onHandleBackCLick()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomNavigationViewHelper(bottomNavigation).removeShiftMode()
        setHasOptionsMenu(true)
        bottomNavigation.apply { setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener) }

        childFragmentManager.registerFragmentLifecycleCallbacks(childFragmentCallback, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    override fun showMyScheduleTab() = selectTab(R.id.tab_event_my_schedule)

    override fun showScheduleTab() = selectTab(R.id.tab_event_schedule)

    override fun showAboutTab(eventId: String) = selectTab(R.id.about_event, AboutEventFragmentArgs.Builder(eventId, ABOUT_FROM_EVENT).build().toBundle())

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

    override fun setBackClickHandlerEnabled(enabled: Boolean) {
        backPressedCallback.isEnabled = enabled
    }

    override fun showChat() {
        findNavController().navigate(EventTabsFragmentDirections.actionEventTabsFragmentToChatListTabsFragment())
    }

    override fun showAccount() {
        findNavController().navigate(EventTabsFragmentDirections.mainToProfile(false))
    }

    override fun showEventList() {
        findNavController().navigate(R.id.recommendations_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun showNotifications() {
        findNavController().navigate(EventTabsFragmentDirections.eventToNotifications())
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        toolbarContentActionBar.apply {
            addRightView(AccountView(requireContext()).also {
                it.canShowBadge = false
                it.setOnClickListener { presenter.onMenuAccountClick() }
            })
            addRightView(ChatView(requireContext()).also {
                it.setOnClickListener { presenter.onMenuChatClick() }
            })
            addRightView(NotificationsView(requireContext()).also {
                it.setOnClickListener { presenter.onMenuNotificationsClick() }
            })
        }
    }

    override fun showNoConnectionMessage(show: Boolean) {
        if (show && noInternetDialog?.isShowing != true) {
            BottomSheetDialog(requireContext()).apply {
                val layout = LayoutInflater.from(requireContext()).inflate(R.layout.layout_no_internet, null).apply {
                    this.btnAction.text = getString(R.string.no_internet_action_to_calendar)
                    this.btnAction.setOnClickListener {
                        presenter.onMyScheduleTabSelected()
                    }
                }
                setContentView(layout)
                setCancelable(false)
                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) requireActivity().finish()
                    true
                }
                noInternetDialog = this
            }.show()
        } else if (!show) {
            noInternetDialog?.dismiss()
        }
    }


    override fun layout() = R.layout.fragment_event_tabs
}
