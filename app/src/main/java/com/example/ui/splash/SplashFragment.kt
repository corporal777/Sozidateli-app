package com.example.ui.splash

import android.os.Bundle
import android.view.View
import com.example.R
import com.example.databinding.FragmentSplashBinding
import com.example.ui.base.BaseFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class SplashFragment : BaseFragment<FragmentSplashBinding>(),
    //BackgroundImageFragment,
    SplashContract.View {


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
    //override val isLightStatus: Boolean = false
    //override fun getFragmentBackgroundDrawable(): Drawable = AuthBackground.get(resources)

}