package com.example.ui.views.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.BottomSheetDialogEventAgreementBinding
import com.example.extensions.getClickablePrivacyPolitics
import com.example.extensions.removeUrlUnderline
import com.example.extensions.setOnClickListener
import com.example.ui.views.CustomSpannableString
import com.example.util.ClickableSpan
import com.example.util.ClickableSpanNew
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class EventAgreementBottomDialog (
    context: Context,
    val data: String
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetDialogEventAgreementBinding.inflate(LayoutInflater.from(context))
    private var onSelect: () -> Unit = {}

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        setCancelable(false)

        mBinding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
            btnApply.apply {
                isEnabled = false
                setOnClickListener {
                    onSelect.invoke()
                    dismiss()
                }
            }
            viewAgreement.apply {
                getTextView().apply {
                    text = CustomSpannableString(context.getString(R.string.auth_agree_user_agreement)).apply {
                        setFontSpan("fonts/sf_pro_display_semibold.ttf", context, 11)
                        setSpan(ClickableSpanNew(getTextView()) {
                            showCustomTabsBrowser(context, data)
                        }, 11, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                    highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                    movementMethod = LinkMovementMethod.getInstance()
                }
                setOnCheckedListener {
                    btnApply.isEnabled = it
                }
            }
        }
    }

    fun setSelectCallback(block: () -> Unit): EventAgreementBottomDialog {
        onSelect = block
        return this
    }

}