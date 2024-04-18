package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.DialogTextButtonBinding
import com.example.databinding.DialogTextButtonsBinding
import com.example.extensions.removeUrlUnderline
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class MessageDialogWithTextButton(
    val context: Context,
    val message: CharSequence,
    val positiveText: String? = null,
    val isCancelable: Boolean = false
) {

    private val mBinding = DialogTextButtonBinding.inflate(LayoutInflater.from(context))

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
        mBinding.tvAction.apply {
            if (!positiveText.isNullOrEmpty()) text = positiveText
            setOnClickListener {
                clickAction.invoke()
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        mAlertDialog.window?.setBackgroundDrawable(back)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: () -> Unit): MessageDialogWithTextButton {
        clickAction = block
        return this
    }

}