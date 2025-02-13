package com.example.ui.splash

import android.os.Bundle
import android.view.View
import com.example.app.R
import com.example.app.databinding.FragmentSplashBinding
import com.example.ui.base.BaseVBFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class SplashFragment : BaseVBFragment<FragmentSplashBinding>(),
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

    override fun binding() = FragmentSplashBinding::class.java
    override fun layout() = R.layout.fragment_splash
    //override val isLightStatus: Boolean = false
    //override fun getFragmentBackgroundDrawable(): Drawable = AuthBackground.get(resources)

}