package com.example.ui.userprofile.edit.items

import android.content.Context
import android.view.LayoutInflater
import com.example.databinding.BottomSheetAboutAdditionalInfoBinding
import com.example.databinding.BottomSheetAdditionalInfoBinding
import com.example.util.initInput
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