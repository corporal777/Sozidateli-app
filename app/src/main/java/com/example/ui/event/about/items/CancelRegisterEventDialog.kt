package com.example.ui.event.about.items

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.DialogCancelRegisterEventBinding
import com.example.databinding.DialogChangeAccountBinding
import com.example.extensions.dp
import com.example.extensions.px
import com.example.ui.accountChange.items.ChangeAccountBottomDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import setOnClickListener

class CancelRegisterEventDialog (
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


    fun setCancelRegisterCallback(block: () -> Unit): CancelRegisterEventDialog {
        onCancelClick = block
        return this
    }
}