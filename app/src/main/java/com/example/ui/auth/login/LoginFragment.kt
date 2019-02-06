package com.example.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject
import javax.inject.Provider

class LoginFragment : BaseFragment(), LoginContract.View {

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var presenterProvider: Provider<LoginPresenter>

    @ProvidePresenter
    fun providePresenter(): LoginPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flFbAuth.setOnClickListener { presenter.onClickFb() }
        flVkAuth.setOnClickListener { presenter.onClickVk() }
        flOkAuth.setOnClickListener { presenter.onClickOk() }
        flEmailAuth.setOnClickListener { presenter.onClickEmail() }
    }

    override fun startSocialNetworkAuthorization() {

    }

    override fun showWelcome() {
        findNavController().navigate(LoginFragmentDirections.loginToWelcome())
    }

    override fun showLogin() {
        findNavController().navigate(LoginFragmentDirections.loginToLoginEmailAction())
    }

    override fun layout() = R.layout.fragment_login
}
