package com.example.ui.userprofile.read.settings.change_password

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetChangePasswordBinding
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ChangePasswordFragment(
    val fromRecover: Boolean,
    val code: String
) :
    BaseBottomSheetFragment<BottomSheetChangePasswordBinding>(),
    ChangePasswordContract.View {

    @InjectPresenter
    lateinit var presenter: ChangePasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangePasswordPresenter>

    @ProvidePresenter
    fun providePresenter(): ChangePasswordPresenter = presenterProvider.get().apply {
        isRecover = fromRecover
        recoverCode = code
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusOnInput(mBinding.etPassword, true)
        mBinding.apply {
            var password = ""
            etPassword.apply {
                setText(password)
                onTextChanged {
                    it?.toString()?.let { text -> password = text }
                }
            }
            btnNext.setOnClickListener {
                presenter.checkPasswordValid(password)
            }
            tvForgetPassword.setOnClickListener {
                presenter.onRecoveryPasswordClick()
                dismiss()
            }
        }
    }

    override fun setPasswordIsNotCorrect(attempts: Int) {
        mBinding.apply {
            if (attempts > 0) {
                val warning = if (attempts > 1) {
                    "Неверный пароль, осталось $attempts попытки"
                } else {
                    "Неверный пароль, осталась $attempts попытка"
                }
                tvAttemptsLeft.text = warning
                tvAttemptsLeft.isVisible = true
            } else tvAttemptsLeft.isVisible = false
        }
    }

    override fun showPasswordSuccessUpdated() {
        dismiss()
        Toast.makeText(
            requireContext(),
            getString(R.string.profile_password_change_complete),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun setPasswordIsCorrect() {
        mBinding.apply {
            currentPasswordContainer.isInvisible = true
            newPasswordContainer.isInvisible = false
            var newPassword = ""
            passwordView.setPasswordValidCallback {
                newPassword = it.password ?: ""
                btnNext.isEnabled = it.isValid && !it.password.isNullOrEmpty()
            }
            btnNext.apply {
                text = getString(R.string.save)
                isEnabled = false
                setOnClickListener {
                    presenter.onChangePasswordClickConfirm(newPassword)
                }
            }
        }
    }

    override fun setRecoverPassword(code: String) {
        mBinding.apply {
            currentPasswordContainer.isInvisible = true
            newPasswordContainer.isInvisible = false
            var newPassword = ""
            passwordView.setPasswordValidCallback {
                newPassword = it.password ?: ""
                btnNext.isEnabled = it.isValid && !it.password.isNullOrEmpty()
            }
            btnNext.apply {
                text = getString(R.string.save)
                isEnabled = false
                setOnClickListener {
                    presenter.onRecoverPasswordClickConfirm(code, newPassword)
                }
            }
        }
    }

    override fun showLoginAgainDialog() {
        MessageDialogWithBrownButton(
            requireContext(),
            "Превышено количество попыток ввода пароля. Пожалуйста, авторизуйтесь в приложении заново.",
            false
        ).setSelectCallback {
            presenter.logoutFromAccount()
            dismiss()
        }
    }

    override fun showRecoveryPassword(email: String) {
        findNavController().navigate(
            R.id.recovery_password_fragment,
            RecoveryPasswordFragmentArgs.Builder(email).build().toBundle()
        )
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_password

}