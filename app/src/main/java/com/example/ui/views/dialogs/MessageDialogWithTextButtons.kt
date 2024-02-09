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
import com.example.databinding.DialogMessageWithGrayButtonBinding
import com.example.databinding.DialogTextButtonsBinding
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline

class MessageDialogWithTextButtons(
    val context: Context,
    val title: CharSequence,
    val message: CharSequence,
    val positiveText: String,
    val negativeText: String,
    val isCancelable: Boolean = true
) {

    private val mBinding =
        DialogTextButtonsBinding.inflate(LayoutInflater.from(context))

    private var clickAction: () -> Unit = {}

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(isCancelable)

        mBinding.tvTitle.apply {
            isVisible = !title.isNullOrEmpty()
            text = title
        }
        mBinding.tvMessage.apply {
            highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
            text = message
            movementMethod = BetterLinkMovementMethod.getInstance()
            removeUrlUnderline()
        }
        mBinding.tvAction.apply {
            text = positiveText
            setOnClickListener {
                clickAction.invoke()
                mAlertDialog.dismiss()
            }
        }
        mBinding.tvCancel.apply {
            text = negativeText
            setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        mAlertDialog.window?.setBackgroundDrawable(back)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: () -> Unit): MessageDialogWithTextButtons {
        clickAction = block
        return this
    }

}