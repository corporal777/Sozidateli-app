package com.example.ui.eventTabs

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_event_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventTabsFragment : BaseFragment(), EventTabsContract.View {

    @InjectPresenter
    lateinit var presenter: EventTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventTabsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNestedNavController()
        bottomNavigation.apply {
            setOnNavigationItemSelectedListener {
                presenter.apply {
                    when (it.itemId) {
                        R.id.my_schedule_fragment -> onMyScheduleTabSelected()
                        R.id.schedule_fragment -> onScheduleTabSelected()
                        R.id.about_event_navigation -> onAboutSelected()
                        R.id.map_tabs_fragment -> onMapTabsSelected()
                        R.id.to_list -> presenter.onToListSelected()
                        else -> return@setOnNavigationItemSelectedListener false
                    }
                }

                return@setOnNavigationItemSelectedListener true
            }
        }

        controller.addOnDestinationChangedListener { _, destination, _ ->
            val itemId = when (destination.id) {
                R.id.my_schedule_fragment -> R.id.my_schedule_fragment
                R.id.schedule_fragment -> R.id.schedule_fragment
                R.id.about_event_fragment -> R.id.about_event_navigation
                R.id.map_tabs_fragment -> R.id.map_tabs_fragment
                else -> null
            }

            itemId?.let { id ->
                if (matchDestination(destination, id)) {
                    bottomNavigation.menu.findItem(id)?.let { it.isChecked = true }
                }
            }
        }
    }

    override fun showMyScheduleTab() = findNestedNavController().navigate(R.id.my_schedule_fragment, null, buildNavOptions())

    override fun showScheduleTab() = findNestedNavController().navigate(R.id.schedule_fragment, null, buildNavOptions())

    override fun showAboutTab() = findNestedNavController().navigate(R.id.about_event_navigation, null, buildNavOptions())

    override fun showMapTab() = findNestedNavController().navigate(R.id.map_tabs_fragment, null, buildNavOptions())

    override fun showEventList() {
        findNavController().apply {
            graph.startDestination = R.id.events_tabs_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.event_tabs_fragment, true)
                    .build()
            navigate(R.id.events_tabs_fragment, null, opts)
        }
    }

    private fun buildNavOptions(popupTo: Int = R.id.my_schedule_fragment): NavOptions {
        val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                .setPopUpTo(popupTo, false)
        return builder.build()
    }

    private fun matchDestination(destination: NavDestination,
                                 @IdRes destId: Int): Boolean {
        var currentDestination: NavDestination? = destination
        while (currentDestination!!.id != destId && currentDestination.parent != null) {
            currentDestination = currentDestination.parent
        }
        return currentDestination.id == destId
    }

    override fun setLabel(label: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = label
    }

    private fun findNestedNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_event_tabs
}
