package com.example.ui.profile.favoritesTab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationsFilter
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.favorite.FavoriteEventsFragment
import com.example.ui.organizations.list.OrganizationsFragment
import com.example.ui.organizations.list.OrganizationsFragmentArgs
import com.example.ui.users.favorite.FavoriteUsersFragment
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.btnTabEvents
import kotlinx.android.synthetic.main.fragment_favorite.btnTabOrganizations
import kotlinx.android.synthetic.main.fragment_favorite.btnTabUsers
import kotlinx.android.synthetic.main.fragment_favorite.clTabs
import kotlinx.android.synthetic.main.fragment_favorite.viewPager
import javax.inject.Inject
import javax.inject.Provider

class FavoriteTabsFragment : BaseFragment(), FavoriteContract.View, ToolbarFragment {

    override val title: String
        get() = getString(R.string.profile_favorite)


    @InjectPresenter
    lateinit var presenter: FavoritePresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoritePresenter>

    @ProvidePresenter
    fun providePresenter(): FavoritePresenter = presenterProvider.get()

    private val pageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            selectTab(position)
        }
    }

    private val fragments by lazy {
        listOf(
                FavoriteEventsFragment(),
                OrganizationsFragment().apply {
                    arguments = OrganizationsFragmentArgs.Builder(OrganizationsFilter.FAVORITES_NO_TITLE).build().toBundle()
                },
                FavoriteUsersFragment()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.run {
            addOnPageChangeListener(pageChangeListener)
            adapter = object : FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = fragments[position]

                override fun getCount() = fragments.size
            }
            selectTab(currentItem)
        }

        btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
        btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
        btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
    }

    private fun selectTab(position: Int) {
        clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }

    override fun layout() = R.layout.fragment_favorite
}
