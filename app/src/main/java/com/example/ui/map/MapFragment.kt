package com.example.ui.map

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseNestedNavigationFragment
import javax.inject.Inject
import javax.inject.Provider

class MapFragment : BaseNestedNavigationFragment(), MapContract.View {

    @InjectPresenter
    lateinit var presenter: MapPresenter

    @Inject
    lateinit var presenterProvider: Provider<MapPresenter>

    @ProvidePresenter
    fun providePresenter(): MapPresenter = presenterProvider.get()

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_map
}
