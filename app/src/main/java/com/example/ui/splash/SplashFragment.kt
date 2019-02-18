package com.example.ui.splash

import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import javax.inject.Inject
import javax.inject.Provider

class SplashFragment : BaseFragment(), SplashContract.View {

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @Inject
    lateinit var presenterProvider: Provider<SplashPresenter>

    @ProvidePresenter
    fun providePresenter(): SplashPresenter = presenterProvider.get()

    override fun initWithAuth() = findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToAuthNavigation())

    override fun initWithEventList() = findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainNavigation())

    override fun initWithEvent() = findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToEventTabsNavigation())

    override fun layout() = R.layout.fragment_splash
    override fun isShowToolbar() = false
}
