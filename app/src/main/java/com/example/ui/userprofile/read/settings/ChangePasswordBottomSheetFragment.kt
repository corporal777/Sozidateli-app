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
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import onTextChanged

class ChangePasswordBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangePasswordBinding? = null
    private val mBinding get() = _binding!!
    private var onNextAction: (password: String) -> Unit = {}
    private var onSaveNewPasswordAction: (newPassword: String) -> Unit = {}
    private var onRecoveryPasswordAction: () -> Unit = {}

    private var newPasswordContentIsShown = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetChangePasswordBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusOnInput(true)
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

    private fun focusOnInput(canShow: Boolean) {
        mBinding.etPassword.apply {
            post {
                showSoftInputOnFocus = canShow
                requestFocus()
                showSoftInputOnFocus = true
                if (canShow) showKeyboard(this)
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}