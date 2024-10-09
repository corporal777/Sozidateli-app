package com.example.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.example.app.databinding.BottomSheetAboutAdditionalInfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class AboutAdditionalInfoBottomSheet(context: Context) : BottomSheetDialog(context) {

    private val mBinding =
        BottomSheetAboutAdditionalInfoBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(true)

        mBinding.apply {

            ivBack.setOnClickListener {
                dismiss()
            }
            btnSave.apply {
                setOnClickListener {
                    dismiss()
                }
            }
        }
    }
}