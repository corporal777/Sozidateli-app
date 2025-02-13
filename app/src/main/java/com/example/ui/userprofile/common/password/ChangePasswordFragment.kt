package com.example.ui.userprofile.common.password

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentChangePasswordBinding
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangePasswordFragment() : BaseVBFragment<FragmentChangePasswordBinding>(),
    ChangePasswordContract.View {

    @InjectPresenter
    lateinit var presenter: ChangePasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangePasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): ChangePasswordPresenter = presenterProvider.get().apply {
        isPasswordChange = ChangePasswordFragmentArgs.fromBundle(requireArguments()).isChange
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnForgotPassword.setOnClickListener {
                showRecoveryPassword()
            }
            btnClose.setOnClickListener {
                navigateUp()
            }
        }
    }

    override fun showEnterCurrentPassword() {
        var currentPassword = ""
        mBinding.apply {
            tvResetTitle.text = getString(R.string.current_password_label)
            passwordView.isVisible = false
            etPassword.initInput {
                currentPassword = it.toString()
                btnChange.isEnabled = !currentPassword.isNullOrEmpty()
            }
            btnChange.apply {
                isEnabled = !currentPassword.isNullOrEmpty()
                setButtonText(getString(R.string.password_confirm_next))
                setOnClickListener {
                    presenter.onCheckPasswordValid(currentPassword)
                    hideKeyboard(it)
                }
            }
        }
    }

    override fun showEnterNewPassword() {
        var newPassword = ""
        mBinding.apply {
            tvResetTitle.text = getString(R.string.create_password_title)
            btnForgotPassword.isVisible = false
            tvAttemptsLeft.isVisible = false
            etPassword.isVisible = false

            passwordView.apply {
                isVisible = true
                setPasswordValidCallback {
                    newPassword = it.password ?: ""
                    btnChange.isEnabled = it.isValid && !newPassword.isNullOrEmpty()
                }
            }
            btnChange.apply {
                isEnabled = !newPassword.isNullOrEmpty()
                setButtonText(getString(R.string.save))
                setOnClickListener {
                    presenter.onChangePasswordClick(newPassword)
                    hideKeyboard(it)
                }
            }
        }
    }


    override fun setPasswordIsNotCorrect(attempts: Int) {
        mBinding.apply {
            if (attempts > 0) {
                val warning = if (attempts > 1) "Неверный пароль, осталось $attempts попытки"
                else "Неверный пароль, осталась $attempts попытка"
                tvAttemptsLeft.text = warning
                tvAttemptsLeft.isVisible = true
            } else tvAttemptsLeft.isVisible = false
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

    override fun showPasswordSuccessChanged() {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.password_success_changed),
            withCancel = false
        ).setSelectCallback { navigateUp() }
    }


    override fun showRecoveryPassword() {
        findNavController().navigate(
            R.id.recovery_password_fragment,
            RecoveryPasswordFragmentArgs.Builder("").build().toBundle()
        )
    }

    override fun showCustomLoading() = mBinding.btnChange.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnChange.showProgressLoading(false)

    override fun binding() = FragmentChangePasswordBinding::class.java
    override fun layout(): Int = R.layout.fragment_change_password

}