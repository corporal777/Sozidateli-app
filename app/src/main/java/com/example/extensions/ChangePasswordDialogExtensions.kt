package com.example.extensions

import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.R
import com.example.ui.views.passwordView.PasswordCustomView
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.fragment_register_email_new.*
import onTextChanged

fun Fragment.showChangePasswordDialog(onConfirm: (oldPassword: String, newPassword: String, newPasswordConfirm: String) -> Unit) {
    val emptyFieldError = getString(R.string.profile_edit_empty_field_error)
    val shortPasswordError = getString(R.string.auth_error_short_password)
    var isNewValid = false

    val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
    val tilOldPassword = view.findViewById<TextInputLayout>(R.id.tilOldPassword)
    val etOldPassword = view.findViewById<EditText>(R.id.etOldPassword).apply {
        onTextChanged { tilOldPassword.error = null }
    }
    val password = view.findViewById<PasswordCustomView>(R.id.password).apply {
        setShowAgree(false)
        setPasswordValidCallback {
            isNewValid = it.isValid
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
                            val newPassword = password.etPassword.text?.toString()

                            if (oldPassword.isNullOrEmpty()) {
                                tilOldPassword.error = emptyFieldError
                                hasError = true
                            }
                            if (!hasError && oldPassword != null && isNewValid && newPassword != null) {
                                onConfirm(oldPassword, newPassword, newPassword)
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