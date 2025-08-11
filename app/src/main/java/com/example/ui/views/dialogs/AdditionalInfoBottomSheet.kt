package com.example.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.example.app.databinding.BottomSheetAdditionalInfoBinding
import com.example.extensions.initInput
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class AdditionalInfoBottomSheet(
    context: Context,
    data: String?
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetAdditionalInfoBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: (data: String?) -> Unit = {}

    private var additionalData = data

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(true)

        mBinding.apply {
            etData.apply {
                initInput(additionalData) {
                    additionalData = it.toString()
                }
            }

            btnSave.apply {
                setOnClickListener {
                    onActionClick.invoke(additionalData)
                    dismiss()
                }
            }
            tvHowEdit.setOnClickListener {
                AboutAdditionalInfoBottomSheet(mBinding.root.context).show()
            }
        }
    }


    fun setSaveClickCallback(block: (data: String?) -> Unit): AdditionalInfoBottomSheet {
        onActionClick = block
        return this
    }
}