package com.example.ui.views.dialogs

import android.content.Context
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.BottomSheetDialogEventAgreementBinding
import com.example.databinding.BottomSheetEventRegistrationSuccessBinding
import com.example.ui.views.CustomSpannableString
import com.example.util.ClickableSpanNew
import com.example.util.showCustomTabsBrowser
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