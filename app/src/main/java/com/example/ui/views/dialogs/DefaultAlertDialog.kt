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
import com.example.databinding.DialogAlertDefaultBinding
import com.example.databinding.DialogMessageWithGrayButtonBinding

class DefaultAlertDialog(
    private val context: Context,
    private val title : String?,
    private val message: String,
    private val positiveText : String?,
    private val negativeText : String?,
    private val withCancel: Boolean = false
) {

    private val mBinding = DialogAlertDefaultBinding.inflate(LayoutInflater.from(context))

    private var onActionClick: () -> Unit = {}


    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBinding.tvTitle.apply {
            isVisible = !title.isNullOrEmpty()
            text = title
        }
        mBinding.tvMessage.apply {
            text = message
        }
        mBinding.tvOk.apply {
            if (!positiveText.isNullOrEmpty()) text = positiveText
            setOnClickListener {
                onActionClick.invoke()
                mAlertDialog.dismiss()
            }
        }

        mBinding.tvCancel.apply {
            isVisible = withCancel
            if (!negativeText.isNullOrEmpty()) text = negativeText
            setOnClickListener {
                mAlertDialog.dismiss()
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
}