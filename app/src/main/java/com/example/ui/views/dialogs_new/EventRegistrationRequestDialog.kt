package com.example.ui.views.dialogs_new

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.databinding.DialogEventRegistrationRequestBinding

class EventRegistrationRequestDialog(
    val context: Context,
    val title: String,
    val message: String,
    val btnPositiveText: String = "",
    val btnNegativeText: String = "",
    val canCancel : Boolean
) {

    private var onSelect: (state: Boolean) -> Unit = {}
    private var onCancel: () -> Unit = {}

    private var mBinding =
        DialogEventRegistrationRequestBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(canCancel)

        mBinding.tvTitle.apply {
            text = title
        }

        mBinding.tvMessage.apply {
            text = message
        }

        mBinding.btnPositive.apply {
            if (!btnPositiveText.isNullOrEmpty())
                text = btnPositiveText
            setOnClickListener {
                onSelect.invoke(true)
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnNegative.apply {
            if (!btnNegativeText.isNullOrEmpty())
                text = btnNegativeText
            setOnClickListener {
                onSelect.invoke(false)
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 10)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: (state: Boolean) -> Unit): EventRegistrationRequestDialog {
        onSelect = block
        return this
    }
}