package com.example.ui.profile.favoritesTab

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.organizations.favorites.FavoriteOrganizationsFragment
import com.example.ui.speakers.favorite.FavoriteSpeakersFragment
import kotlinx.android.synthetic.main.fragment_favorite.*
import java.util.*
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
        fragments.add(FavoriteSpeakersFragment())
        fragments.add(FavoriteOrganizationsFragment())

        viewPager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun getPageTitle(position: Int) = resources.getStringArray(R.array.favorite_tab_name_array)[position]
        }

        viewPager.run {
            offscreenPageLimit = fragments.size
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun layout() = R.layout.fragment_favorite
}
