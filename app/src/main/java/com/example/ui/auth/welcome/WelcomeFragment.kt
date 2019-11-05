package com.example.ui.auth.welcome

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragment
import com.example.util.AuthBackground
import kotlinx.android.synthetic.main.fragment_welcome.*
import javax.inject.Inject
import javax.inject.Provider

class WelcomeFragment : BaseFragment(), BackgroundImageFragment, WelcomeContract.View {

    override val isLightStatus = false

    @InjectPresenter
    lateinit var presenter: WelcomePresenter

    @Inject
    lateinit var presenterProvider: Provider<WelcomePresenter>

    @ProvidePresenter
    fun providePresenter(): WelcomePresenter = presenterProvider.get()

    override fun setUserName(name: String) {
        tvGreeting.text = getString(R.string.welcome_greeting_message, name)
    }

    override fun getFragmentBackgroundDrawable() = AuthBackground.get(resources)

    override fun layout() = R.layout.fragment_welcome
}
