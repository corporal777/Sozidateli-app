package com.example.ui.views.dialogs_new

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.R
import com.example.databinding.DialogEventRegistrationAgreementBinding
import com.example.util.ClickableSpan
import kotlinx.android.synthetic.main.dialog_event_registration_agreement_form.view.*

class EventAgreementRegisterDialog (val context: Context, val url: String) {

    private var onSelect: () -> Unit = {}

    private var mBinding  = DialogEventRegistrationAgreementBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog : AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        val agreementText = SpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan(drawUnderline = false) {
                showUserAgreement(url)
            }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        mBinding.tvAgree.apply {
            text = agreementText
            movementMethod = LinkMovementMethod.getInstance()
        }

        mBinding.btnOk.apply {
            isEnabled = false
            setOnClickListener {
                onSelect.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnCancel.apply {
            setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        mBinding.cbAgree.setOnCheckedChangeListener { _, checked ->
            mBinding.btnOk.isEnabled = checked
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    private fun showUserAgreement(url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(context, R.string.about_event_agreement_open_error, Toast.LENGTH_LONG).show()
        }
    }

    fun setSelectCallback(block: () -> Unit): EventAgreementRegisterDialog {
        onSelect = block
        return this
    }

}