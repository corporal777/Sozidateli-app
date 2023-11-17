package com.example.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentLoginBinding
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.BaseFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class LoginFragment : BaseFragment<FragmentLoginBinding>(), LoginContract.View {

    private var isRegister = false
    private var invite: Int? = null

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var presenterProvider: Provider<LoginPresenter>

    @ProvidePresenter
    fun providePresenter(): LoginPresenter = presenterProvider.get().apply {
        LoginFragmentArgs.fromBundle(requireArguments()).let { args ->
            isRegister = args.isRegistered
            invite = args.inviteId
            login = args.email ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnForgotPassword.apply {
                isVisible = !isRegister
                setOnClickListener { presenter.onClickRecoverPassword() }
            }
            etLogin.onTextChanged {
                it?.toString()?.let { text -> presenter.onChangeLoginText(text) }
            }
            etPassword.onTextChanged {
                it?.toString()?.let { text -> presenter.onChangePasswordText(text) }
            }
            btnLogin.setOnClickListener {
                hideKeyboard()
                presenter.onClickLogin(invite ?: -1)
                //presenter.authVk()
                //presenter.authOk()

            }
            ibClose.setOnClickListener { presenter.onClickBack() }
        }

    }

    override fun setLoginAndPassword(login: String, password: String) {
        mBinding.etLogin.setText(login)
        mBinding.etPassword.setText(password)
    }

    override fun enableLoginBtn(isEnable: Boolean) {
        mBinding.btnLogin.isEnabled = isEnable
    }

    override fun showLoginError(show: Boolean) {
        mBinding.tilLogin.apply {
            if (show) showError(getString(R.string.auth_error_wrong_login))
            else showError(null)
        }
    }

    override fun showPasswordError(show: Boolean) {
        mBinding.tilPassword.apply {
            if (show)
                if (mBinding.etPassword.text.isNullOrEmpty()) showError(getString(R.string.auth_error_no_password))
                else showError(getString(R.string.auth_error_short_password))
            else showError(null)
        }
    }

    override fun showSnRegistration(snUser: SnUser) {

    }

    override fun showRecoveryPassword(email: String) {
        val args = RecoveryPasswordFragmentArgs.Builder(email).build().toBundle()
        findNavController().navigate(R.id.recovery_password_fragment, args)
    }


    override fun showWrongPasswordError() {
        val message = getString(R.string.auth_register_wrong_password_error)
        showErrorMessage(false, message)
    }


    override fun showCustomLoading() {
        mBinding.apply {
            btnLogin.showProgressLoading(true)
        }
    }

    override fun hideCustomLoading(){
        mBinding.apply {
            btnLogin.showProgressLoading(false)
        }
    }

    override fun layout() = R.layout.fragment_login
}
