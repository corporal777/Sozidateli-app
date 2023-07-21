package com.example.ui.favoritesTab

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentFavoriteBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.favoritesTab.events.FavoriteEventsFragment
import com.example.ui.favoritesTab.organizations.FavoriteOrganizationsFragment
import com.example.ui.favoritesTab.users.FavoriteUsersFragment
import com.example.ui.views.toolbar.ToolbarContent
import onPageChanged
import javax.inject.Inject
import javax.inject.Provider

class FavoriteTabsFragment : BaseFragmentNew<FragmentFavoriteBinding>(true),
    FavoriteTabsContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: FavoriteTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteTabsPresenter = presenterProvider.get()

    private val pageChangeListener = onPageChanged { position ->
        presenter.currentPosition = position
        selectTab(position)
    }

    private val fragments by lazy {
        listOf(
            FavoriteEventsFragment(),
            FavoriteOrganizationsFragment(),
            FavoriteUsersFragment()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewPager.run {

                isSaveEnabled = false
                addOnPageChangeListener(pageChangeListener)
                adapter = object : FragmentStatePagerAdapter(
                    childFragmentManager,
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                ) {
                    override fun getItem(position: Int) = fragments[position] as Fragment
                    override fun getCount() = fragments.size
                }
                selectTab(currentItem)
                doOnPreDraw { startPostponedEnterTransition() }
            }
            btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
            btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
            btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
        }

    }

    private fun selectTab(position: Int) {
        mBinding.clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }

    override fun setCurrentFragment(position: Int) {
        mBinding.viewPager.apply {
            if (currentItem == position) return
            else {
                currentItem = position
                selectTab(position)
            }
        }
    }

    override fun layout() = R.layout.fragment_favorite
    override val title: CharSequence by lazy { getString(R.string.profile_favorite) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
