package com.example.ui.firstFragment

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import javax.inject.Inject
import javax.inject.Provider

class FirstFragment : BaseFragment(), FirstFragmentContract.View {

    @InjectPresenter
    lateinit var presenter: FirstFragmentPresenter

    @Inject
    lateinit var presenterProvider: Provider<FirstFragmentPresenter>

    @ProvidePresenter
    fun providePresenter(): FirstFragmentPresenter = presenterProvider.get()


    override fun layout() = R.layout.fragment_first
}
