package com.example.ui.event.registration.items

import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import com.example.R
import com.example.databinding.ItemRegisterEventProfileFooterBinding
import com.example.databinding.ItemRegisterEventProfileMainBinding
import com.example.holders.registerEvent.EventRegistrationTitleItem
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.util.ClickableSpan
import com.example.util.ClickableSpanNew
import com.example.util.firstLetterToUppercase
import com.xwray.groupie.databinding.BindableItem

class RegisterEventProfileFooterItem(
    isValid: Boolean,
    val onGoToProfileClick: () -> Unit
) : BindableItem<ItemRegisterEventProfileFooterBinding>() {

    private var fieldsIsValid = isValid
    private lateinit var mBinding: ItemRegisterEventProfileFooterBinding

    override fun bind(viewBinding: ItemRegisterEventProfileFooterBinding, position: Int) {
        viewBinding.apply {
            mBinding = this
            tvAction.apply {
                decorTextColor(this, fieldsIsValid)
            }
        }
    }

    private fun decorTextColor(textView: TextView, isValid: Boolean) {
        textView.apply {
            val footerText: SpannableString
            val footerTextColor : Int
            val clickableSpan = ClickableSpanNew(this) {
                onGoToProfileClick.invoke()
            }
            if (isValid) {
                footerTextColor = ContextCompat.getColor(context, R.color.register_event_go_to_profile_text_color)
                footerText =
                    SpannableString(context.getString(R.string.register_event_go_to_edit_data)).apply {
                        val linkStart = 80
                        val linkEnd = length
                        setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }
            } else {
                footerTextColor = ContextCompat.getColor(context, R.color.red_new)
                footerText =
                    SpannableString(context.getString(R.string.register_event_go_to_fill_missing_data)).apply {
                        val linkStart = 99
                        val linkEnd = length
                        setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    }
            }
            setTextColor(footerTextColor)
            highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
            text = footerText
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is RegisterEventProfileFooterItem) return false
        if (fieldsIsValid != other.fieldsIsValid) return false
        return true
    }


    fun updateFooterText(payload: Boolean) {
        if (payload != fieldsIsValid) {
            fieldsIsValid = payload
            if (this::mBinding.isInitialized){
                decorTextColor(mBinding.tvAction, fieldsIsValid)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_footer
}