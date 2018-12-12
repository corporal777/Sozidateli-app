package com.example.ui.mapTabs

import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseNestedNavigationFragment
import javax.inject.Inject
import javax.inject.Provider

class MapTabsFragment : BaseNestedNavigationFragment(), MapTabsContract.View {

    @InjectPresenter
    lateinit var presenter: MapTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MapTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): MapTabsPresenter = presenterProvider.get()

    private fun findNestedNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    override fun layout() = R.layout.fragment_map_tabs
}
