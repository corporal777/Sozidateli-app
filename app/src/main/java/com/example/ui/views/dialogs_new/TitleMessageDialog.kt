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
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.DialogEventRegistrationAgreementBinding
import com.example.databinding.DialogTitleMessageBinding
import com.example.util.ClickableSpan

class TitleMessageDialog(
    val context: Context,
    val title: String,
    val message: String,
    val btnPositiveText: String = "",
    val btnNegativeText: String = "",
    val canShowCancel: Boolean = false
) {

    private var onSelect: () -> Unit = {}
    private var onCancel: () -> Unit = {}

    private var mBinding = DialogTitleMessageBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(true)

        mBinding.tvTitle.apply {
            isVisible = !title.isNullOrEmpty()
            text = title
        }

        mBinding.tvMessage.apply {
            text = message
        }

        mBinding.btnPositive.apply {
            if (!btnPositiveText.isNullOrEmpty()){
                text = btnPositiveText
            }
            setOnClickListener {
                onSelect.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnNegative.apply {
            if (!btnNegativeText.isNullOrEmpty()){
                text = btnNegativeText
            }
            setOnClickListener {
                onCancel.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnCancel.apply {
            isVisible = canShowCancel
            setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setPositiveSelectCallback(block: () -> Unit): TitleMessageDialog {
        onSelect = block
        return this
    }

    fun setNegativeSelectCallback(block: () -> Unit): TitleMessageDialog {
        onCancel = block
        return this
    }
}