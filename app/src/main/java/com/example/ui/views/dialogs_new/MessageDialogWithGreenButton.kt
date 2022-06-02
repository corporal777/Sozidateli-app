package com.example.ui.views.dialogs_new

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.DialogMessageWithGrayButtonBinding
import com.example.ui.views.ChangePasswordDialog
import com.example.ui.views.ChangePasswordModel

class MessageDialogWithGreenButton(val context: Context, val message: String) {

    private val mBinding = DialogMessageWithGrayButtonBinding.inflate(LayoutInflater.from(context))

    private var onActionClick: () -> Unit = {}


    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBinding.btnAction.background =
            ContextCompat.getDrawable(context, R.drawable.custom_btn_green_selectable)

        mBinding.tvMessage.apply {
            text = message
        }
        mBinding.btnAction.setOnClickListener {
            onActionClick.invoke()
            mAlertDialog.dismiss()
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: () -> Unit): MessageDialogWithGreenButton {
        onActionClick = block
        return this
    }
}