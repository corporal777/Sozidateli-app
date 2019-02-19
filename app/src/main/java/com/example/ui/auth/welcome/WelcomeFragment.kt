package com.example.ui.auth.welcome

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_welcome.*
import javax.inject.Inject
import javax.inject.Provider

class WelcomeFragment : BaseFragment(), WelcomeContract.View {

    @InjectPresenter
    lateinit var presenter: WelcomePresenter

    @Inject
    lateinit var presenterProvider: Provider<WelcomePresenter>

    @ProvidePresenter
    fun providePresenter(): WelcomePresenter = presenterProvider.get()

    override fun setUserName(name: String) {
        tvGreeting.text = getString(R.string.welcome_greeting_message, name)
    }

    override fun layout() = R.layout.fragment_welcome
}
