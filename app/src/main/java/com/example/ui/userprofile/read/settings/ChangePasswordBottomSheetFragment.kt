package com.example.ui.userprofile.read.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.BottomSheetChangePasswordBinding
import com.example.ui.base.BaseBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import onTextChanged

class ChangePasswordBottomSheetFragment :
    BaseBottomSheetFragment<BottomSheetChangePasswordBinding>() {

    private var onNextAction: (password: String) -> Unit = {}
    private var onSaveNewPasswordAction: (newPassword: String) -> Unit = {}
    private var onRecoveryPasswordAction: () -> Unit = {}

    private var newPasswordContentIsShown = false


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
                onNextAction.invoke(password)
            }
            tvForgetPassword.setOnClickListener {
                onRecoveryPasswordAction.invoke()
                dismiss()
            }
        }
    }


    fun setOnNextActionCallback(block: (password: String) -> Unit): ChangePasswordBottomSheetFragment {
        onNextAction = block
        return this
    }

    fun setOnSavePasswordActionCallback(block: (password: String) -> Unit): ChangePasswordBottomSheetFragment {
        onSaveNewPasswordAction = block
        return this
    }

    fun setOnRecoveryPasswordActionCallback(block: () -> Unit): ChangePasswordBottomSheetFragment {
        onRecoveryPasswordAction = block
        return this
    }

    fun setPasswordIsNotCorrect(attempts: Int) {
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

    fun setPasswordIsCorrect() {
        newPasswordContentIsShown = true
        mBinding.apply {
            currentPasswordContainer.isInvisible = true
            newPasswordContainer.isInvisible = false
            var newPassword = ""
            btnNext.apply {
                text = getString(R.string.save)
                isEnabled = false
            }
            passwordView.setPasswordValidCallback {
                newPassword = it.password?:""
                btnNext.isEnabled = it.isValid && !it.password.isNullOrEmpty()
            }
            btnNext.setOnClickListener {
                onSaveNewPasswordAction.invoke(newPassword)
                dismiss()
            }
        }
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_password

}