package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.DialogMessageWithGrayButtonBinding
import com.example.extensions.removeUrlUnderline
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
            highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
            text = message
            movementMethod = BetterLinkMovementMethod.getInstance()
            removeUrlUnderline()
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