package com.example.ui.auth.login

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.layout_login_sn_register_confirmation.*
import onTextChanged
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
        etEmail.onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text) } }
        etPassword.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordText(text) } }

        btnForgotPassword.setOnClickListener { presenter.onClickRecoverPassword() }
        ibLogin.setOnClickListener {
            presenter.onClickLogin(etEmail.text?.toString() ?: "", etPassword.text?.toString()
                    ?: "")
        }
        ibVk.setOnClickListener { presenter.authVk() }
        ibFacebook.setOnClickListener { presenter.authFb() }
        ibOk.setOnClickListener { presenter.authOk() }
        ibClose.setOnClickListener { presenter.onClickBack() }
    }

    override fun setEmailAndPassword(email: String, password: String) {
        etEmail.setText(email)
        etPassword.setText(password)
    }

    override fun enableLoginBtn(isEnable: Boolean) {
        ibLogin.isEnabled = isEnable
    }

    override fun showEmailRegistration() {
        findNavController().navigate(LoginFragmentDirections.loginToRegisterEmailAction())
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(LoginFragmentDirections.loginToRegisterSnAction(snUser))
    }

    override fun showRecoveryPassword(email: String) {
        findNavController().navigate(LoginFragmentDirections.loginToRecoveryAction(email))
    }

    override fun showEmailError(show: Boolean) {
        tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showPasswordError(show: Boolean) {
        tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password
        ) else null
    }

    override fun layout() = R.layout.fragment_login
}
