package com.example.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.R
import com.example.databinding.DialogClearDegreeBinding
import com.example.databinding.DialogTitleMessageBinding

class ClearDegreeDialog(val context: Context) {

    private var onSelect: (isAgree: Boolean) -> Unit = {}

    private var mBinding = DialogClearDegreeBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBinding.apply {
            btnPositive.setOnClickListener {
                onSelect.invoke(true)
                mAlertDialog.dismiss()
            }
            btnNegative.setOnClickListener {
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

    fun setSelectCallback(block: (isAgree: Boolean) -> Unit): ClearDegreeDialog {
        onSelect = block
        return this
    }
}