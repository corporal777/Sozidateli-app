package com.example.ui.userprofile.common.password

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.app.R
import com.example.app.databinding.FragmentCheckPasswordBinding
import com.example.extensions.onTextChanged
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class CheckPasswordFragment() : BaseVBFragment<FragmentCheckPasswordBinding>(),
    CheckPasswordContract.View {

    @InjectPresenter
    lateinit var presenter: CheckPasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<CheckPasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): CheckPasswordPresenter = presenterProvider.get()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvAttemptsLeft.isVisible = presenter.isWarningVisible
            etPassword.onTextChanged {
                presenter.onChangeOldPassword(it.toString())
            }
            btnForgotPassword.setOnClickListener {
                hideKeyboard(it)
                showRecoveryPassword()
            }
            btnChange.setOnClickListener {
                hideKeyboard(it)
                presenter.onCheckPasswordValid()
            }
            btnClose.setOnClickListener {
                hideKeyboard(it)
                navigateUp()
            }
        }
    }


    override fun setPasswordIsNotCorrect(attempts: Int) {
        val warning = if (attempts <= 0) null
        else {
            if (attempts > 1) "Неверный пароль, осталось $attempts попытки"
            else "Неверный пароль, осталась $attempts попытка"
        }
        mBinding.apply {
            tvAttemptsLeft.text = warning
            tvAttemptsLeft.isVisible = !warning.isNullOrEmpty()
            presenter.isWarningVisible = tvAttemptsLeft.isVisible
        }
    }



    override fun showLoginAgainDialog() {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.password_attempts_has_been_exceeded),
            withCancel = false
        ).setSelectCallback { presenter.logoutFromAccount() }
    }


    override fun showRecoveryPassword() {
        val args = RecoveryPasswordFragmentArgs.Builder("").build().toBundle()
        findNavController().navigate(R.id.recovery_password_fragment, args)
    }

    override fun showChangePassword() {
        val options = navOptions { popUpTo(R.id.checkPasswordFragment) { inclusive = true } }
        findNavController().navigate(R.id.resetPasswordFragment, null, options)
    }

    override fun enableBtnChange(enable: Boolean) = mBinding.btnChange.let { it.isEnabled = enable }

    override fun showCustomLoading() = mBinding.btnChange.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnChange.showProgressLoading(false)

    override fun animationType(): AnimType = AnimType.FADE

    override fun binding() = FragmentCheckPasswordBinding::class.java
    override fun layout(): Int = R.layout.fragment_check_password
}