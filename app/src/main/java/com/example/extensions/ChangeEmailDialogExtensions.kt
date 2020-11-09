package com.example.extensions

import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import com.example.R
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import me.saket.bettermovementmethod.BetterLinkMovementMethod

fun Fragment.showChangeEmailDialog(onConfirm: (email: String) -> Unit) {
    val view = layoutInflater.inflate(R.layout.dialog_change_email, null)
    val til = view.findViewById<TextInputLayout>(R.id.tilEmail)
    val et = view.findViewById<EditText>(R.id.etEmail)
    AlertDialog.Builder(requireContext())
            .setTitle(R.string.profile_email_change)
            .setView(view)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).apply {
                        setOnClickListener {
                            val email = et.text.toString()
                            if (AuthValidateUtil.isValidEmail(email)) {
                                onConfirm(email)
                                dismiss()
                            } else til.error = getString(R.string.profile_edit_email_invalid)
                        }
                    }
                }
            }
            .show()
}

fun Fragment.showChangeEmailCompleteDialog(email: String) {
    val supportEmail = getString(R.string.support_email).toSpannable()
    Linkify.addLinks(supportEmail, Linkify.EMAIL_ADDRESSES)

    val message = SpannableStringBuilder(getString(R.string.email_change_msg).format(email))
            .append(" ")
            .append(supportEmail)
            .append(".")

    AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
            .apply {
                findViewById<TextView>(android.R.id.message)?.let {
                    it.movementMethod = BetterLinkMovementMethod.getInstance()
                }
            }
}