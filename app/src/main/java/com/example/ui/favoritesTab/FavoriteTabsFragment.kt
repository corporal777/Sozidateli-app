package com.example.ui.favoritesTab

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.R
import com.example.app.databinding.FragmentFavoriteBinding
import com.example.extensions.onPageSelected
import com.example.extensions.setChildSelected
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

    private val pageChangeListener = onPageSelected { position -> selectTab(position) }
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
                doOnPreDraw { startPostponedEnterTransition() }
                registerOnPageChangeCallback(pageChangeListener)
                adapter = object : FragmentStateAdapter(this@FavoriteTabsFragment) {
                    override fun getItemCount(): Int = fragments.size
                    override fun createFragment(position: Int): Fragment = fragments[position]
                }
                selectTab(currentItem)
            }
            btnTabEvents.setOnClickListener { viewPager.currentItem = 0 }
            btnTabOrganizations.setOnClickListener { viewPager.currentItem = 1 }
            btnTabUsers.setOnClickListener { viewPager.currentItem = 2 }
        }

    }

    private fun selectTab(position: Int) = mBinding.clTabs.setChildSelected(position)

    override fun layout() = R.layout.fragment_favorite
    override fun animationType(): AnimType = AnimType.AXIS
    override fun binding() = FragmentFavoriteBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.profile_favorite) }
}
