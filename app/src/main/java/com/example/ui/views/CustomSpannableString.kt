package com.example.ui.views

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.util.ClickableSpanNew

class CustomSpannableString(source: CharSequence?) : SpannableString(source) {


    fun setColorSpan(color: Int, context: Context) {
        val expandColor = ContextCompat.getColor(context, color)
        setSpan(
            ForegroundColorSpan(expandColor),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun setTextSizeSpan(size: Int, context: Context) {
        val textSize = context.resources.getDimensionPixelSize(size)
        setSpan(AbsoluteSizeSpan(textSize), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun setClickSpan(textView: TextView, onClick: () -> Unit) {
        val clickableSpan = ClickableSpanNew(textView) {
            onClick.invoke()
        }
        if (length < 0) return
        setSpan(clickableSpan, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun setClickSpanWithLength(textView: TextView, start: Int, end: Int, onClick: () -> Unit) {
        val clickableSpan = ClickableSpanNew(textView) {
            onClick.invoke()
        }
        if (length < 0) return
        setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun setFontSpan(res: String, context: Context) {
        val font = Typeface.createFromAsset(context.assets, res)
        setSpan(CustomTypefaceSpan("", font), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}