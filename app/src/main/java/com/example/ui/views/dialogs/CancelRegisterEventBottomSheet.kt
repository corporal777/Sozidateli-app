package com.example.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.example.R
import com.example.databinding.DialogCancelRegisterEventBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CancelRegisterEventBottomSheet (
    context: Context,
) : BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme) {

    private val mBinding = DialogCancelRegisterEventBinding.inflate(LayoutInflater.from(context))

    private var onCancelClick: () -> Unit = {}

    init {
        setContentView(mBinding.root)
        mBinding.apply {
            cancelRegister.setOnClickListener {
                onCancelClick.invoke()
                dismiss()
            }
            cancel.setOnClickListener {
                dismiss()
            }
        }
    }


    fun setCancelRegisterCallback(block: () -> Unit): CancelRegisterEventBottomSheet {
        onCancelClick = block
        return this
    }
}