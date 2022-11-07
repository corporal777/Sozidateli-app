package com.example.ui.userprofile.read.settings.change_password

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetChangePasswordBinding
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.userprofile.read.settings.change_phone.confirm_phone.ConfirmPhoneFragment
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ChangePasswordFragment(
    val fromRecover: Boolean = false,
    val code: String = ""
) : BaseBottomSheetFragment<BottomSheetChangePasswordBinding>(),
    ChangePasswordContract.View {

    private var passwordIsCorrect: () -> Unit = {}

    @InjectPresenter(type = PresenterType.WEAK, tag = CHANGE_PASSWORD_FRAGMENT_TAG)
    lateinit var presenter: ChangePasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangePasswordPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CHANGE_PASSWORD_FRAGMENT_TAG)
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

    override fun showEnterNewPassword() {
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

    override fun setPasswordIsCorrect() {
        passwordIsCorrect.invoke()
        dismiss()
    }

    fun setPasswordIsCorrectCallback(block: () -> Unit): ChangePasswordFragment {
        passwordIsCorrect = block
        return this
    }

    companion object {
        const val CHANGE_PASSWORD_FRAGMENT_TAG = "change_password_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_password

}