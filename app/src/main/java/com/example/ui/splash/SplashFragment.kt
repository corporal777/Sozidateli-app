package com.example.ui.splash

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragment
import com.example.util.AuthBackground
import javax.inject.Inject
import javax.inject.Provider

class SplashFragment : BaseFragment(), BackgroundImageFragment, SplashContract.View {

    override val isLightStatus = false

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @Inject
    lateinit var presenterProvider: Provider<SplashPresenter>

    @ProvidePresenter
    fun providePresenter(): SplashPresenter = presenterProvider.get()

    override fun getFragmentBackgroundDrawable() = AuthBackground.get(resources)

    override fun layout() = R.layout.fragment_splash
}