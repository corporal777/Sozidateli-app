package com.example.ui.userprofile.edit.password

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.BottomSheetChangePasswordBinding
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragmentArgs
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangePasswordFragment(
    private val isRecover: Boolean,
    val code: String = "",
    val id: String = ""
) : BaseBottomSheetFragment<BottomSheetChangePasswordBinding>(),
    ChangePasswordContract.View {

    @InjectPresenter(tag = CHANGE_PASSWORD_FRAGMENT_TAG)
    lateinit var presenter: ChangePasswordPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangePasswordPresenter>

    @ProvidePresenter(tag = CHANGE_PASSWORD_FRAGMENT_TAG)
    fun providePresenter(): ChangePasswordPresenter = presenterProvider.get().apply {
        recoverCode = code
        fromRecover = isRecover
        userId = id
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusOnInput(mBinding.etPassword, true)
        mBinding.apply {
            btnNext.setOnClickListener {
                presenter.checkPasswordValid(etPassword.text.toString())
            }
            tvForgetPassword.setOnClickListener {
                presenter.onRecoveryPasswordClick()
                dismiss()
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
                setButtonText(getString(R.string.save))
                isEnabled = false
                setOnClickListener {
                    (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                    presenter.onChangePasswordClickConfirm(newPassword)
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

    override fun showPasswordSuccessUpdated() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        dismiss()
        showToast(getString(R.string.profile_password_change_complete))
    }


    override fun showLoginAgainDialog() {
        MessageDialogWithBrownButton(
            requireContext(),
            "Превышено количество попыток ввода пароля. Пожалуйста, авторизуйтесь в приложении заново.",
            false
        ).setSelectCallback {
            (requireActivity() as MainActivity).setIgnoreTokenListener(false)
            presenter.logoutFromAccount()
            dismiss()
        }
    }


    override fun showRecoveryPassword() {
        findNavController().navigate(
            R.id.recovery_password_fragment,
            RecoveryPasswordFragmentArgs.Builder("").build().toBundle()
        )
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, CHANGE_PASSWORD_FRAGMENT_TAG)
    override fun showCustomLoading() = mBinding.btnNext.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnNext.showProgressLoading(false)

    companion object {
        const val CHANGE_PASSWORD_FRAGMENT_TAG = "change_password_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_password

}