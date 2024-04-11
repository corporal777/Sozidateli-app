package com.example.ui.views

import android.app.Activity
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.R
import com.example.ui.views.passwordView.PasswordCustomView
import com.google.android.material.textfield.TextInputLayout
import com.example.extensions.onTextChanged

class ChangePasswordDialog(val activity: Activity) {

    var emptyFieldError = ""
    var shortPasswordError = ""
    var isNewValid = false

    private var onSelect: (data: ChangePasswordModel) -> Unit = {}

    private val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_change_password, null)

    val tilOldPassword = layout.findViewById<TextInputLayout>(R.id.tilOldPassword)
    val etOldPassword = layout.findViewById<EditText>(R.id.etOldPassword).apply {
        onTextChanged { tilOldPassword.error = null }
    }
    val password = layout.findViewById<PasswordCustomView>(R.id.password).apply {
        setShowAgree(false)
        setPasswordValidCallback {
            isNewValid = it.isValid
        }
    }

    private lateinit var alertDialog: AlertDialog
    val builder = AlertDialog.Builder(activity)
            .setTitle(R.string.profile_password_change)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .setCancelable(false)

    init {
        builder.setView(layout)
        shortPasswordError = activity.resources.getString(R.string.auth_error_short_password)
        emptyFieldError = activity.resources.getString(R.string.profile_edit_empty_field_error)
        builder.setTitle(R.string.profile_password_change)
        alertDialog = builder.create()
        alertDialog.apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_POSITIVE).apply {
                    this.setOnClickListener {
                        var hasError = false
                        val oldPassword = etOldPassword.text?.toString()
                        val newPassword = password.etPassword.text?.toString()

                        if (oldPassword.isNullOrEmpty()) {
                            tilOldPassword.error = emptyFieldError
                            hasError = true
                        }
                        if (!hasError && oldPassword != null && isNewValid && newPassword != null) {
                            onSelect(ChangePasswordModel(oldPassword, newPassword, newPassword))
                        }
                    }
                }
            }
        }
        alertDialog.show()
    }

    fun showInvalidCurrentPassword() {
        tilOldPassword.error = activity.resources.getString(R.string.status_profile_check_password_wrong)
    }

    fun closeDialog() {
        alertDialog.dismiss()
    }

    fun setSelectCallback(block: (data: ChangePasswordModel) -> Unit): ChangePasswordDialog {
        onSelect = block
        return this
    }
}

data class ChangePasswordModel(
        val oldPassword: String,
        val newPassword: String,
        val newPasswordConfirm: String
)