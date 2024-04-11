package com.example.ui.auth.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.BuildConfig
import com.example.R
import com.example.databinding.FragmentLoginBinding
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.views.dialogs.MessageDialogWithTextButtons
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
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
            snAuth = args.snAuth
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnForgotPassword.apply {
                isVisible = !isRegister
                setOnClickListener { presenter.onClickRecoverPassword() }
            }
            etLogin.initInput {
                presenter.onChangeLoginText(it.toString())
            }
            etPassword.initInput {
                presenter.onChangePasswordText(it.toString())
            }
            btnLogin.setOnClickListener {
                hideKeyboard()
                presenter.onClickLogin(invite ?: -1)
            }
            ibClose.setOnClickListener { navigateUp() }
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
        mBinding.etLogin.showError(show)
    }

    override fun showPasswordError(show: Boolean) {
        mBinding.etPassword.showError(show)
    }

    override fun showRecoveryPassword(email: String) {
        val args = RecoveryPasswordFragmentArgs.Builder(email).build().toBundle()
        findNavController().navigate(R.id.recovery_password_fragment, args)
    }


    override fun showWrongPasswordError() {
        val message = getString(R.string.auth_register_wrong_password_error)
        showErrorMessage(false, message)
    }

    override fun showAccountBlockingDialog() {
        MessageDialogWithTextButtons(
            requireContext(),
            "Ваш аккаунт заблокирован",
            "Превышено количество попыток\n ввода пароля. Обратитесь в\n техническую поддержку, чтобы\n разблокировать аккаунт.",
            "Помощь",
            "Отмена"
        ).setSelectCallback { sendHelpEmail() }
    }


    private fun sendHelpEmail() {
        try {
            val text = getString(R.string.blocked_account_email_text, presenter.login)
            val techInfo = "Техническая информация"
            val os = "OS: Android"
            val api = "API: ${android.os.Build.VERSION.SDK_INT}"
            val appVersion = "App version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
            val message = listOf(text, techInfo, os, api, appVersion).joinToString(separator = "\n")

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Разблокировка аккаунта: " + presenter.login)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun showCustomLoading() = mBinding.btnLogin.run { showProgressLoading(true) }
    override fun hideCustomLoading() = mBinding.btnLogin.run { showProgressLoading(false) }

    override fun layout() = R.layout.fragment_login
}
