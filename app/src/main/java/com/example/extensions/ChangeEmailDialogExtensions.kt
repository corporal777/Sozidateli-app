package com.example.extensions

import android.text.SpannableStringBuilder
import android.text.util.Linkify
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import me.saket.bettermovementmethod.BetterLinkMovementMethod

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