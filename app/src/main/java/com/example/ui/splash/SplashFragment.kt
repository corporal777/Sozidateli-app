package com.example.ui.splash

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentSplashBinding
import com.example.ui.base.BaseFragment
import javax.inject.Inject
import javax.inject.Provider


class SplashFragment : BaseFragment<FragmentSplashBinding>(), SplashContract.View {


    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @Inject
    lateinit var presenterProvider: Provider<SplashPresenter>

    @ProvidePresenter
    fun providePresenter(): SplashPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mBinding.splashCl.updatePadding(top = getHeightOfNavbar().dp - getHeightOfStatusBar())
    }

    override fun layout() = R.layout.fragment_splash

}