package com.example.ui.auth.welcome

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import javax.inject.Inject
import javax.inject.Provider

class WelcomeFragment : BaseFragment(), WelcomeContract.View {

    @InjectPresenter
    lateinit var presenter: WelcomePresenter

    @Inject
    lateinit var presenterProvider: Provider<WelcomePresenter>

    @ProvidePresenter
    fun providePresenter(): WelcomePresenter = presenterProvider.get()


    override fun showMain() {
        findNavController().navigate(WelcomeFragmentDirections.welcomeToMain())
    }

    override fun layout() = R.layout.fragment_welcome
}
