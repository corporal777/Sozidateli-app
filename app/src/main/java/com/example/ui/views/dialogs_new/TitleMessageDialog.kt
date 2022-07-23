package com.example.ui.views.dialogs_new

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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.R
import com.example.databinding.DialogEventRegistrationAgreementBinding
import com.example.databinding.DialogTitleMessageBinding
import com.example.util.ClickableSpan

class TitleMessageDialog(val context: Context, val title: String, val message: String) {

    private var onSelect: (state : Boolean) -> Unit = {}
    private var onCancel: () -> Unit = {}

    private var mBinding = DialogTitleMessageBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        mBinding.tvTitle.apply {
            text = title
        }

        mBinding.tvMessage.apply {
            text = message
        }

        mBinding.btnPositive.setOnClickListener {
            onSelect.invoke(true)
            mAlertDialog.dismiss()
        }

        mBinding.btnNegative.setOnClickListener {
            onSelect.invoke(false)
            mAlertDialog.dismiss()
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: (state : Boolean) -> Unit): TitleMessageDialog {
        onSelect = block
        return this
    }
}