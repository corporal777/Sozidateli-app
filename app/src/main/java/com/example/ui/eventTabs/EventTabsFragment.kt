package com.example.ui.eventTabs

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.OnBackPressedListener
import com.example.ui.base.BaseFragment
import com.example.util.BottomNavigationViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_event_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventTabsFragment : BaseFragment(), EventTabsContract.View, OnBackPressedListener {

    @InjectPresenter
    lateinit var presenter: EventTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventTabsPresenter = presenterProvider.get()

    private val graphIdToTagMap = SparseArray<String>()
    private lateinit var currentNavController: NavController

    private val bottomNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        presenter.apply {
            when (it.itemId) {
                R.id.tab_event_my_schedule -> onMyScheduleTabSelected()
                R.id.tab_event_schedule -> onScheduleTabSelected()
                R.id.about_event -> onAboutSelected()
                R.id.tab_event_map -> onMapTabsSelected()
                R.id.to_list -> onToListSelected()
                else -> return@OnNavigationItemSelectedListener false
            }
        }

        return@OnNavigationItemSelectedListener true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomNavigationViewHelper(bottomNavigation).removeShiftMode()
        setHasOptionsMenu(true)
        bottomNavigation.apply { setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener) }
    }

    override fun initialNavigationSetup() {
        setupFragments()
    }

    override fun showMyScheduleTab() = selectTab(R.id.tab_event_my_schedule)

    override fun showScheduleTab() = selectTab(R.id.tab_event_schedule)

    override fun showAboutTab() = selectTab(R.id.about_event)

    override fun showMapTab() = selectTab(R.id.tab_event_map)

    override fun setCurrentDestinationOnStart() {
        val startDestination = currentNavController.graph.startDestination
        if (currentNavController.currentDestination?.id != startDestination)
            currentNavController.popBackStack(startDestination, false)
    }

    override fun onBackPressed(): Boolean {
        if (!currentNavController.navigateUp()) presenter.onClickBackWhenCurrentNavigationOnTop()
        return true
    }

    override fun showChat() {
        findNavController().navigate(EventTabsFragmentDirections.actionEventTabsFragmentToChatListTabsFragment())
    }

    override fun showSearch() {
        findNavController().navigate(EventTabsFragmentDirections.mainToSearch())
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

    override fun finish() {
        activity?.finish()
    }

    override fun setLabel(label: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = label
    }

    private fun selectTab(id: Int) {
        val newTabTag = graphIdToTagMap[id]
        val selectedFragment = childFragmentManager.findFragmentByTag(newTabTag)
                as NavHostFragment

        childFragmentManager.beginTransaction()
                .attach(selectedFragment)
                .setPrimaryNavigationFragment(selectedFragment)
                .apply {
                    // Detach all other Fragments
                    graphIdToTagMap.forEach { _, fragmentTag ->
                        if (fragmentTag != newTabTag) {
                            detach(childFragmentManager.findFragmentByTag(fragmentTag)!!)
                        }
                    }
                }
                .setCustomAnimations(
                        R.anim.nav_default_enter_anim,
                        R.anim.nav_default_exit_anim,
                        R.anim.nav_default_pop_enter_anim,
                        R.anim.nav_default_pop_exit_anim)
                .setReorderingAllowed(true)
                .commit()

        currentNavController = selectedFragment.navController
        bottomNavigation.apply {
            setOnNavigationItemSelectedListener(null)
            selectedItemId = id
            setOnNavigationItemSelectedListener(bottomNavigationItemSelectedListener)
        }
    }

    private fun setupFragments() {
        val navGraphIds = listOf(
                R.navigation.tab_event_my_schedule,
                R.navigation.tab_event_schedule,
                R.navigation.about_event_navigation,
                R.navigation.tab_event_map
        )

        navGraphIds.forEachIndexed { index, navGraphId ->
            val fragmentTag = getFragmentTag(index)

            // Find or create the Navigation host fragment
            val navHostFragment = obtainNavHostFragment(
                    childFragmentManager,
                    fragmentTag,
                    navGraphId,
                    R.id.nav_host_container
            )

            // Obtain its id
            val graphId = navHostFragment.navController.graph.id

            // Save to the map
            graphIdToTagMap[graphId] = fragmentTag
        }
    }

    private fun obtainNavHostFragment(
            fragmentManager: FragmentManager,
            fragmentTag: String,
            navGraphId: Int,
            containerId: Int
    ): NavHostFragment {
        // If the Nav Host fragment exists, return it
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
        existingFragment?.let { return it }

        // Otherwise, create it and return it.
        val navHostFragment = NavHostFragment.create(navGraphId)
        fragmentManager.beginTransaction()
                .add(containerId, navHostFragment, fragmentTag)
                .detach(navHostFragment)
                .commitNow()
        return navHostFragment
    }

    private fun getFragmentTag(index: Int) = "bottomNavigation#$index"

    override fun layout() = R.layout.fragment_event_tabs
}
