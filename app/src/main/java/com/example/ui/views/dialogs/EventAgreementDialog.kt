package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.R
import com.example.databinding.DialogEventRegistrationAgreementBinding
import com.example.util.ClickableSpanNew
import com.example.util.showCustomTabsBrowser


class EventAgreementDialog(val context: Context, val url: String) {

    private var onSelect: () -> Unit = {}

    private var mBinding = DialogEventRegistrationAgreementBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        mBinding.tvAgree.apply {
            text = SpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                val linkStart = 11
                val linkEnd = length
                setSpan(ClickableSpanNew(mBinding.tvAgree) {
                    showCustomTabsBrowser(context, url)
                }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            movementMethod = LinkMovementMethod.getInstance()
        }

        mBinding.btnOk.apply {
            isEnabled = false
            setOnClickListener {
                onSelect.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnCancel.setOnClickListener {
            mAlertDialog.dismiss()
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


    fun setSelectCallback(block: () -> Unit): EventAgreementDialog {
        onSelect = block
        return this
    }

    companion object {
        private const val AGREEMENT_DIALOG_TAG = "agreement_dialog_tag"
    }

}