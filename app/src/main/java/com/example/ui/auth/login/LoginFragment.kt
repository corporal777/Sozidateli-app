package com.example.ui.auth.login

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.auth.authorization.AuthorizationFragmentDirections
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
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
        etLogin.apply {
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
            onTextChanged { it?.toString()?.let { text -> presenter.onChangeLoginText(text) } }
        }
        etPassword.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordText(text) } }

        btnForgotPassword.setOnClickListener { presenter.onClickRecoverPassword() }
        ibLogin.setOnClickListener {
            presenter.onClickLogin(etLogin.text?.toString() ?: "", etPassword.text?.toString()
                    ?: "")
        }
        ibVk.setOnClickListener { presenter.authVk() }
        ibFacebook.setOnClickListener { presenter.authFb() }
        ibOk.setOnClickListener { presenter.authOk() }
        ibClose.setOnClickListener { presenter.onClickBack() }
    }

    override fun setLoginAndPassword(login: String, password: String) {
        etLogin.setText(login)
        etPassword.setText(password)
    }

    override fun enableLoginBtn(isEnable: Boolean) {
        ibLogin.isEnabled = isEnable
    }

    override fun showEmailRegistration() {
        if (BuildConfig.NEW_PROFILE_EDIT) {
            findNavController().navigate(LoginFragmentDirections.loginToRegisterEmailNewAction())
        } else {
            findNavController().navigate(LoginFragmentDirections.loginToRegisterEmailAction())
        }
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(LoginFragmentDirections.loginToRegisterSnAction(snUser))
    }

    override fun showRecoveryPassword(email: String) {
        findNavController().navigate(LoginFragmentDirections.loginToRecoveryAction(email))
    }

    override fun showLoginError(show: Boolean) {
        tilLogin.error = if (show) getString(R.string.auth_error_wrong_login) else null
    }

    override fun showPasswordError(show: Boolean) {
        tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password
        ) else null
    }

    override fun showWrongPasswordError() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_title)
                .setMessage(R.string.auth_register_wrong_password_error)
                .setPositiveButton(R.string.ok) { _, _ ->
                    // do nothing
                }
                .show()
    }

    override fun layout() = R.layout.fragment_login
}
