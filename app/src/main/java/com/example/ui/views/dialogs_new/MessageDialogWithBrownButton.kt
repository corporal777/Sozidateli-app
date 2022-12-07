package com.example.ui.views.dialogs_new

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.R
import com.example.databinding.DialogMessageWithGrayButtonBinding
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class MessageDialogWithBrownButton(
    val context: Context,
    val message: CharSequence,
    val isCancelable: Boolean = true
) {

    private val mBinding =
        DialogMessageWithGrayButtonBinding.inflate(LayoutInflater.from(context))

    private var clickAction: () -> Unit = {}

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(isCancelable)

        mBinding.tvMessage.apply {
            text = message
            movementMethod = BetterLinkMovementMethod.getInstance()
        }
        mBinding.btnAction.setOnClickListener {
            clickAction.invoke()
            mAlertDialog.dismiss()
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: () -> Unit): MessageDialogWithBrownButton {
        clickAction = block
        return this
    }

}