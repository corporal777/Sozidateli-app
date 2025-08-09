package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.app.databinding.DialogAlertDefaultBinding
import com.example.common.extensions.removeUrlUnderline
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class DefaultAlertDialog(
    private val context: Context,
    private val title : String?,
    private val message: CharSequence,
    private val positiveText : String? = null,
    private val negativeText : String? = null,
    private val withCancel: Boolean = true
) {

    private val mBinding = DialogAlertDefaultBinding.inflate(LayoutInflater.from(context))

    private var onActionClick: () -> Unit = {}
    private var onCancelClick: () -> Unit = {}


    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(withCancel)

        mBinding.apply {
            tvTitle.apply {
                isVisible = !title.isNullOrEmpty()
                text = title
            }
            tvMessage.apply {
                text = message
                movementMethod = BetterLinkMovementMethod.getInstance()
                removeUrlUnderline()
            }
            tvOk.apply {
                if (!positiveText.isNullOrEmpty()) text = positiveText
                setOnClickListener {
                    onActionClick.invoke()
                    mAlertDialog.dismiss()
                }
            }

            tvCancel.apply {
                isVisible = !negativeText.isNullOrEmpty()
                if (!negativeText.isNullOrEmpty()) text = negativeText
                setOnClickListener {
                    onCancelClick.invoke()
                    mAlertDialog.dismiss()
                }
            }
        }

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setSelectCallback(block: () -> Unit): DefaultAlertDialog {
        onActionClick = block
        return this
    }

    fun setCancelCallback(block: () -> Unit): DefaultAlertDialog {
        onCancelClick = block
        return this
    }
}