package com.example.ui.event.location

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MapInfo
import com.example.data.models.Place
import com.example.ui.base.BaseFragment
import com.example.ui.event.location.buildingScheme.BuildingSchemeFragment
import com.example.ui.event.location.map.MapFragment
import kotlinx.android.synthetic.main.fragment_map_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class EventLocationFragment : BaseFragment(), EventLocationContract.View {


    @InjectPresenter(type = PresenterType.WEAK, tag = "EventLocationPresenter")
    lateinit var presenter: EventLocationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventLocationPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "EventLocationPresenter")
    fun providePresenter(): EventLocationPresenter = presenterProvider.get().apply {
        val args = EventLocationFragmentArgs.fromBundle(requireArguments())
        mapInfo = args.mapInfo
        places = args.places
    }

    private val pageChangeListener = object : androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            selectTab(position)
            presenter.onPageSelected(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.apply {
            addOnPageChangeListener(pageChangeListener)
        }
    }

    override fun initPages(mapInfo: MapInfo?, places: Array<Place>?) {
        val fragments = mutableListOf<Fragment>()
        if (mapInfo != null) fragments.add(MapFragment.newInstance(mapInfo))
        if (places?.isNotEmpty() == true) fragments.add(BuildingSchemeFragment.newInstance(places))

        if (fragments.size == 1) {
            clTabs.isVisible = false
        }

        viewPager.apply {
            adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = fragments[position]
                override fun getCount() = fragments.size
            }
            offscreenPageLimit = fragments.size
            selectTab(currentItem)
        }

        btnTabMap.setOnClickListener { viewPager.currentItem = 0 }
        btnTabScheme.setOnClickListener { viewPager.currentItem = 1 }
    }

    override fun selectPageAtPosition(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    private fun selectTab(position: Int) {
        clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }

    override fun layout() = R.layout.fragment_map_tabs
}
