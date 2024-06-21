package com.example.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.example.databinding.BottomSheetEventRegistrationSuccessBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class EventRegistrationSuccessBottomDialog(context: Context) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetEventRegistrationSuccessBinding.inflate(LayoutInflater.from(context))
    private var onSelect: (state : Boolean) -> Unit = {}

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(false)

        mBinding.apply {
            btnApply.setOnClickListener {
                onSelect.invoke(true)
                dismiss()
            }
        }
    }

    fun setSelectCallback(block: (state : Boolean) -> Unit): EventRegistrationSuccessBottomDialog {
        onSelect = block
        return this
    }

}