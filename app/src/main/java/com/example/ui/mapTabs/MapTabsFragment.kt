package com.example.ui.mapTabs

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.ui.mapTabs.buildingScheme.BuildingSchemeFragment
import com.example.ui.mapTabs.map.MapFragment
import kotlinx.android.synthetic.main.fragment_favorite.*
import javax.inject.Inject
import javax.inject.Provider

class MapTabsFragment : BaseNestedNavigationFragment(), MapTabsContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = "MapTabsPresenter")
    lateinit var presenter: MapTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MapTabsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "MapTabsPresenter")
    fun providePresenter(): MapTabsPresenter = presenterProvider.get()

    private val pageChangeListener = object : androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            presenter.onPageSelected(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.apply {
            addOnPageChangeListener(pageChangeListener)
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun initPages() {
        val fragments by lazy {
            listOf<Pair<androidx.fragment.app.Fragment, String>>(
                    MapFragment() to getString(R.string.event_map_tab_how_to_get),
                    BuildingSchemeFragment() to getString(R.string.event_map_tab_building_scheme)
            )
        }

        viewPager.apply {
            adapter = TabsAdapter(fragments, childFragmentManager)
            offscreenPageLimit = fragments.size
        }
    }

    override fun selectPageAtPosition(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    override fun layout() = R.layout.fragment_map_tabs

    private inner class TabsAdapter(
            private val fragments: List<Pair<androidx.fragment.app.Fragment, String>>,
            fragmentManager: androidx.fragment.app.FragmentManager
    ) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int) = fragments[position].first

        override fun getPageTitle(position: Int) = fragments[position].second

        override fun getCount() = fragments.size
    }
}
