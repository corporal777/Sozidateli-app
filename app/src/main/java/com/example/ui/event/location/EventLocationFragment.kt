package com.example.ui.event.location

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.TabsFragmentAdapter
import com.example.data.models.MapInfo
import com.example.data.models.Place
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.location.buildingScheme.BuildingSchemeFragment
import com.example.ui.event.location.map.MapFragment
import kotlinx.android.synthetic.main.fragment_favorite.*
import javax.inject.Inject
import javax.inject.Provider

class EventLocationFragment : BaseFragment(), EventLocationContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = EventLocationFragmentArgs.fromBundle(arguments!!).eventName

    @InjectPresenter(type = PresenterType.WEAK, tag = "EventLocationPresenter")
    lateinit var presenter: EventLocationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventLocationPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "EventLocationPresenter")
    fun providePresenter(): EventLocationPresenter = presenterProvider.get().apply {
        val args = EventLocationFragmentArgs.fromBundle(arguments!!)
        mapInfo = args.mapInfo
        places = args.places
    }

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

    override fun initPages(mapInfo: MapInfo?, places: Array<Place>?) {
        val fragments = mutableListOf<Pair<androidx.fragment.app.Fragment, String>>()
        if (mapInfo != null) fragments.add(MapFragment.newInstance(mapInfo) to getString(R.string.event_map_tab_how_to_get))
        if (places != null) fragments.add(BuildingSchemeFragment.newInstance(places) to getString(R.string.event_map_tab_building_scheme))

        if (fragments.size == 1) {
            tabLayout.isVisible = false
        }

        viewPager.apply {
            adapter = TabsFragmentAdapter(fragments, childFragmentManager)
            offscreenPageLimit = fragments.size
        }
    }

    override fun selectPageAtPosition(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    override fun layout() = R.layout.fragment_map_tabs
}
