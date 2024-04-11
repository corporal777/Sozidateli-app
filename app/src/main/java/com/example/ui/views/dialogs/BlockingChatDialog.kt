package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.DialogEventRegistrationRequestBinding

class BlockingChatDialog (
    val context: Context,
    val title: String?,
    val message: String?,
    val btnPositiveText: String?,
    val btnNegativeText: String?,
    val canCancel : Boolean
) {

    private var onSelect: () -> Unit = {}

    private var mBinding = DialogEventRegistrationRequestBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(canCancel)

        mBinding.tvTitle.apply {
            isVisible = !title.isNullOrEmpty()
            text = title
        }

        mBinding.tvMessage.apply {
            isVisible = !message.isNullOrEmpty()
            text = message
        }

        mBinding.btnPositive.apply {
            if (!btnPositiveText.isNullOrEmpty()) text = btnPositiveText
            setOnClickListener {
                onSelect.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.btnNegative.apply {
            if (!btnNegativeText.isNullOrEmpty()) text = btnNegativeText
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

    fun setSelectCallback(block: () -> Unit): BlockingChatDialog {
        onSelect = block
        return this
    }
}