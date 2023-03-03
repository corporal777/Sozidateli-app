package com.example.ui.event.registration.items

import android.text.*
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.ItemRegisterEventProfileFooterBinding
import com.example.util.ClickableSpanNew
import com.xwray.groupie.databinding.BindableItem

class RegisterEventProfileHeaderItem(
    val itemId : Long,
    val isValid: Boolean,
    val onProfileClick: () -> Unit
) : BindableItem<ItemRegisterEventProfileFooterBinding>(itemId) {

    private lateinit var mBinding: ItemRegisterEventProfileFooterBinding

    override fun bind(viewBinding: ItemRegisterEventProfileFooterBinding, position: Int) {
        viewBinding.apply {
            mBinding = this
            tvAction.apply {
                decorTextColor(this, isValid)
            }
        }
    }

    private fun decorTextColor(textView: TextView, isValid: Boolean) {
        textView.apply {
            val footerText: SpannableString
            val footerTextColor : Int
            val clickableSpan = ClickableSpanNew(this) {
                onProfileClick.invoke()
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
        if (other !is RegisterEventProfileHeaderItem) return false
        if (isValid != other.isValid) return false
        return true
    }


    override fun getLayout(): Int = R.layout.item_register_event_profile_footer
}