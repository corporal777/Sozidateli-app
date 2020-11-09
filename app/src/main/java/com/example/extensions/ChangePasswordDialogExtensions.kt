package com.example.extensions

import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.R
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import onTextChanged

fun Fragment.showChangePasswordDialog(onConfirm: (oldPassword: String, newPassword: String, newPasswordConfirm: String) -> Unit) {
    val emptyFieldError = getString(R.string.profile_edit_empty_field_error)
    val shortPasswordError = getString(R.string.auth_error_short_password)

    val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
    val tilOldPassword = view.findViewById<TextInputLayout>(R.id.tilOldPassword)
    val etOldPassword = view.findViewById<EditText>(R.id.etOldPassword).apply {
        onTextChanged { tilOldPassword.error = null }
    }
    val tilNewPassword = view.findViewById<TextInputLayout>(R.id.tilNewPassword)
    val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword).apply {
        onTextChanged {
            tilNewPassword.error = if (it != null && !AuthValidateUtil.isValidPassword(it.toString())) shortPasswordError else null
        }
    }
    val tilNewPasswordConfirm = view.findViewById<TextInputLayout>(R.id.tilNewPasswordConfirm)
    val etNewPasswordConfirm = view.findViewById<EditText>(R.id.etNewPasswordConfirm).apply {
        onTextChanged {
            tilNewPasswordConfirm.error = if (etNewPassword.text.toString() != etNewPasswordConfirm.text.toString()) {
                getString(R.string.auth_error_password_do_not_match)
            } else {
                null
            }
        }
    }

    AlertDialog.Builder(requireContext())
            .setTitle(R.string.profile_password_change)
            .setView(view)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).apply {
                        setOnClickListener {
                            var hasError = false
                            val oldPassword = etOldPassword.text?.toString()
                            val newPassword = etNewPassword.text?.toString()
                            val newPasswordConfirm = etNewPasswordConfirm.text?.toString()

                            if (oldPassword.isNullOrEmpty()) {
                                tilOldPassword.error = emptyFieldError
                                hasError = true
                            }

                            if (newPassword != null && !AuthValidateUtil.isValidPassword(newPassword)) {
                                tilNewPassword.error = shortPasswordError
                                hasError = true
                            }

                            if (newPassword != newPasswordConfirm) {
                                tilNewPasswordConfirm.error = getString(R.string.auth_error_password_do_not_match)
                                hasError = true
                            } else {
                                if (newPassword.isNullOrEmpty()) {
                                    tilNewPassword.error = emptyFieldError
                                    hasError = true
                                }
                                if (newPasswordConfirm.isNullOrEmpty()) {
                                    tilNewPasswordConfirm.error = emptyFieldError
                                    hasError = true
                                }
                            }

                            if (!hasError && oldPassword != null && newPassword != null && newPasswordConfirm != null) {
                                onConfirm(oldPassword, newPassword, newPasswordConfirm)
                                dismiss()
                            }
                        }
                    }
                }
            }
            .show()
}

fun Fragment.showPasswordChangeCompleteDialog() {
    AlertDialog.Builder(requireContext())
            .setMessage(R.string.profile_password_change_complete)
            .setPositiveButton(R.string.ok, null)
            .show()
}