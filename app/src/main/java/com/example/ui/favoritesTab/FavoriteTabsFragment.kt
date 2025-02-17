package com.example.ui.favoritesTab

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import com.example.adapters.PagerStateAdapter
import com.example.app.R
import com.example.app.databinding.FragmentFavoriteBinding
import com.example.extensions.onPageChanged
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.favoritesTab.events.FavoriteEventsFragment
import com.example.ui.favoritesTab.organizations.FavoriteOrganizationsFragment
import com.example.ui.favoritesTab.users.FavoriteUsersFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class FavoriteTabsFragment : BaseToolbarFragment<FragmentFavoriteBinding>(), FavoriteTabsContract.View {

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

    override fun layout() = R.layout.fragment_favorite
    override fun animationType(): AnimType = AnimType.AXIS
    override fun binding() = FragmentFavoriteBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.profile_favorite) }
}
