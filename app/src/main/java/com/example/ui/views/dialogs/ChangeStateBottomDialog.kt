package com.example.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.example.R
import com.example.databinding.DialogChangeStateBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChangeStateBottomDialog(activity: Context, val type: StateType) : BottomSheetDialog(activity) {

    private var onClick: (state: ClickType) -> Unit = {}
    private val mBinding = DialogChangeStateBinding.inflate(LayoutInflater.from(activity))

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(true)

        when(type) {
            StateType.SUCCESS -> {
                mBinding.tvStateDesc.text = activity.resources.getString(R.string.change_status_description)
                mBinding.btnApply.text = activity.resources.getString(R.string.change_status_button)
            }
            StateType.BASE -> {
                mBinding.tvStateDesc.text = activity.resources.getString(R.string.title_get_base)
                mBinding.btnApply.text = activity.resources.getString(R.string.get_base)
            }
            StateType.MAX -> {
                mBinding.btnApply.text = activity.resources.getString(R.string.get_max)
                mBinding.tvStateDesc.text = activity.resources.getString(R.string.title_get_max)
            }
        }
        mBinding.btnApply.setOnClickListener {
            when (type) {
                StateType.BASE -> onClick(ClickType.BASE)
                StateType.MAX -> onClick(ClickType.MAX)
                else -> onClick(ClickType.INFO)
            }
            dismiss()
        }
        mBinding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    fun setClickCallback(block: (state: ClickType) -> Unit): ChangeStateBottomDialog {
        onClick = block
        return this
    }
}

enum class StateType {
    SUCCESS, BASE, MAX
}

enum class ClickType {
    INFO, BASE, MAX
}