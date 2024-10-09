package com.example.ui.views.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.app.databinding.BottomSheetRecordVoiceBinding
import com.example.app.databinding.BottomSheetUpdateAppBinding
import com.example.ui.support.newQuestion.SupportQuestionContract
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class VoiceBottomSheetDialog(context: Context) : BottomSheetDialog(context) {
    private val mBinding = BottomSheetRecordVoiceBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: () -> Unit = {}

    init {
        setContentView(mBinding.root)
        setCancelable(true)

        mBinding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
            progressView.playAnimation()
        }

    }
}