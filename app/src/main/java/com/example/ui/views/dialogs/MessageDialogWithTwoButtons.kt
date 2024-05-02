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
import com.example.databinding.DialogMessageWithTwoButtonsBinding
import com.example.extensions.removeUrlUnderline
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class MessageDialogWithTwoButtons(
    val context: Context,
    val title: String?,
    val message: CharSequence,
    val positiveText: String? = null,
    val negativeText: String? = null,
    val isCancelable: Boolean = true
) {

    private val mBinding =
        DialogMessageWithTwoButtonsBinding.inflate(LayoutInflater.from(context))

    private var clickAction: (state : Boolean) -> Unit = {}

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
        mBinding.btnPositive.apply {
            if (!positiveText.isNullOrEmpty()) text = positiveText
            setOnClickListener {
                clickAction.invoke(true)
                mAlertDialog.dismiss()
            }
        }
        mBinding.btnNegative.apply {
            isVisible = !negativeText.isNullOrEmpty()
            if (!negativeText.isNullOrEmpty()) text = negativeText
            setOnClickListener {
                clickAction.invoke(false)
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: (state : Boolean) -> Unit): MessageDialogWithTwoButtons {
        clickAction = block
        return this
    }

}