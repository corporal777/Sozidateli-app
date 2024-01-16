package com.example.ui.favoritesTab

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import com.example.R
import com.example.adapters.PagerStateAdapter
import com.example.databinding.FragmentFavoriteBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.favoritesTab.events.FavoriteEventsFragment
import com.example.ui.favoritesTab.organizations.FavoriteOrganizationsFragment
import com.example.ui.favoritesTab.users.FavoriteUsersFragment
import com.example.ui.views.toolbar.ToolbarContent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onPageChanged
import javax.inject.Inject
import javax.inject.Provider

class FavoriteTabsFragment : BaseFragment<FragmentFavoriteBinding>(),
    FavoriteTabsContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: FavoriteTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteTabsPresenter = presenterProvider.get()

    private val pageChangeListener = onPageChanged { position ->
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
                adapter = object : PagerStateAdapter(childFragmentManager){
                    override fun getItem(position: Int) = fragments[position]
                    override fun getCount() = fragments.size
                }
                addOnPageChangeListener(pageChangeListener)
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

    override fun animationType(): AnimType = AnimType.AXIS
    override fun layout() = R.layout.fragment_favorite
    override val title: CharSequence by lazy { getString(R.string.profile_favorite) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
