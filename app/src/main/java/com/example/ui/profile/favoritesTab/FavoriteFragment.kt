package com.example.ui.profile.favoritesTab

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.speakers.SpeakersFragment
import com.example.ui.subscriptions.SubscriptionsFragment
import kotlinx.android.synthetic.main.fragment_favorite.*
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Provider

class FavoriteFragment : BaseFragment(), FavoriteContract.View {

    @InjectPresenter
    lateinit var presenter: FavoritePresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoritePresenter>

    @ProvidePresenter
    fun providePresenter(): FavoritePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragments = ArrayList<androidx.fragment.app.Fragment>()
        fragments.add(SpeakersFragment.newInstance(true))
        fragments.add(SubscriptionsFragment())

        viewPager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun getPageTitle(position: Int) = resources.getStringArray(R.array.favorite_tab_name_array)[position]
        }

        viewPager.run {
            offscreenPageLimit = fragments.size
            addOnPageChangeListener(pageChangeListener)
            tabLayout.setupWithViewPager(this)
        }
    }

    private val pageChangeListener: androidx.viewpager.widget.ViewPager.OnPageChangeListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            presenter.setSelectedTab(position)
        }
    }


    override fun selectTab(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_favorite
}
