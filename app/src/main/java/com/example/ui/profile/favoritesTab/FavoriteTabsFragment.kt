package com.example.ui.profile.favoritesTab

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.OrganizationsFilter
import com.example.databinding.FragmentFavoriteBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.favorite.FavoriteEventsFragment
import com.example.ui.organizations.favorite.FavoriteOrganizationsFragment
import com.example.ui.users.favorite.FavoriteUsersFragment
import com.example.ui.views.toolbar.ToolbarContent
import onPageChanged
import javax.inject.Inject
import javax.inject.Provider

class FavoriteTabsFragment : BaseFragmentNew<FragmentFavoriteBinding>(true), FavoriteContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: FavoritePresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoritePresenter>

    @ProvidePresenter
    fun providePresenter(): FavoritePresenter = presenterProvider.get()

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
