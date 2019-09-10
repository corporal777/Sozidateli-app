package com.example.ui.auth.loginEmail

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_login_email.*
import javax.inject.Inject
import javax.inject.Provider

class LoginEmailFragment : BaseFragment(), LoginEmailContract.View {

    @InjectPresenter
    lateinit var presenter: LoginEmailPresenter

    @Inject
    lateinit var presenterProvider: Provider<LoginEmailPresenter>

    @ProvidePresenter
    fun providePresenter(): LoginEmailPresenter = presenterProvider.get().apply {
        arguments?.apply {
            email = getString("email") ?: ""
            password = getString("password") ?: ""
            showConfirmationOnStart = getBoolean("confirm")
            showRecoveryOnStart = getBoolean("recovery")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRegister.setOnClickListener { presenter.onClickRegister() }
        btnLogin.setOnClickListener { presenter.onClickLogin() }
        ivClose.setOnClickListener { presenter.onClickBack() }

        etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangeEmailText(charSequence.toString())
        })

        etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangePasswordText(charSequence.toString())
        })
        tvRecoverPassword.setOnClickListener { presenter.onClickRecoverPassword() }
    }

    override fun setEmailAndPassword(email: String, password: String) {
        etEmail.setText(email)
        etPassword.setText(password)
    }

    override fun showEmailConfirmDialog(email: String) {
        showDialog(getString(R.string.auth_register_confirm_email_title), getString(R.string.auth_register_confirm_email_message).format(email))
    }

    override fun showEmailRecoveryDialog(email: String) {
        showDialog(null, getString(R.string.recovery_confirm_email_message).format(email))
    }

    override fun showRecoveryPassword(email: String) {
        findNavController().navigate(LoginEmailFragmentDirections.loginEmailToRecoveryAction(email))
    }

    override fun showRegister() {
        findNavController().navigate(LoginEmailFragmentDirections.loginEmailToRegisterAction(null))
    }

    override fun enableLoginBtn(isEnable: Boolean) {
        btnLogin.isEnabled = isEnable
    }

    override fun layout() = R.layout.fragment_login_email
}
