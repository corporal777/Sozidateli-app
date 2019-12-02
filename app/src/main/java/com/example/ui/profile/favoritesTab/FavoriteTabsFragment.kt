package com.example.ui.profile.favoritesTab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.TabsFragmentAdapter
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.organizations.list.OrganizationsFragment
import com.example.ui.users.favorite.FavoriteUsersFragment
import kotlinx.android.synthetic.main.fragment_favorite.*
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragments = listOf<Pair<Fragment, String>>(
                FavoriteUsersFragment() to getString(R.string.favorites_users),
                OrganizationsFragment() to getString(R.string.favorites_organizations)
        )

        viewPager.run {
            adapter = TabsFragmentAdapter(fragments, childFragmentManager)
            offscreenPageLimit = fragments.size
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun layout() = R.layout.fragment_favorite
}
