package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.databinding.BottomSheetDialogEventAgreementBinding
import com.example.ui.home.HomeViewModel
import com.example.ui.views.CustomSpannableString
import com.example.util.ClickableSpanNew
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.subjects.SingleSubject

class EventAgreementBottomSheet (
    context: Context,
    val data: String
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetDialogEventAgreementBinding.inflate(LayoutInflater.from(context))
    private var onSelect: (isAccept : Boolean) -> Unit = {}


    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(false)

        mBinding.apply {
            btnClose.setOnClickListener {
                onSelect.invoke(false)
                dismiss()
            }
            btnApply.apply {
                isEnabled = false
                setOnClickListener {
                    onSelect.invoke(true)
                    dismiss()
                }
            }
            viewAgreement.apply {
                getTextView().apply {
                    text = CustomSpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                        setFontSpan("fonts/sf_pro_display_semibold.ttf", context, 11)
                        setClickSpanWithLength(getTextView(), 11, length){
                            showCustomTabsBrowser(context, data)
                        }
                    }
                }
                setOnCheckedListener {
                    btnApply.isEnabled = it
                }
            }
        }
    }

    fun setSelectCallback(block: (isAccept : Boolean) -> Unit): EventAgreementBottomSheet {
        onSelect = block
        return this
    }


}