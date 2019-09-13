package com.example.ui.auth.authorization

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.base.BaseFragment
import com.example.util.AuthBackground
import kotlinx.android.synthetic.main.fragment_authorization.*
import javax.inject.Inject
import javax.inject.Provider

class AuthorizationFragment : BaseFragment(), BackgroundImageFragment, AuthorizationContract.View {

    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthorizationPresenter>

    @ProvidePresenter
    fun providePresenter(): AuthorizationPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ibFacebook.setOnClickListener { presenter.authFb() }
        ibVk.setOnClickListener { presenter.authVk() }
        ibOk.setOnClickListener { presenter.authOk() }
        ibEmail.setOnClickListener { presenter.onEmailClick() }
        ibLogin.setOnClickListener { presenter.onLoginClick() }
    }

    override fun showLogin() {
        findNavController().navigate(AuthorizationFragmentDirections.loginToLoginEmailAction())
    }

    override fun showRegistration(snUser: SnUser?) {
        findNavController().navigate(AuthorizationFragmentDirections.actionAuthorizationFragmentToRegisterFragment(snUser))
    }

    override fun getFragmentBackgroundDrawable(): Drawable? {
        return AuthBackground.get(resources)
    }

    override fun layout() = R.layout.fragment_authorization
}
