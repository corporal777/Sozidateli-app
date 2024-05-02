package com.example.ui.views

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.util.ClickableSpan
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

    fun setColorSpanWithLength(color: Int, start: Int, context: Context) {
        val expandColor = ContextCompat.getColor(context, color)
        setSpan(
            ForegroundColorSpan(expandColor),
            start,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun setTextSizeSpan(size: Int, context: Context) {
        val textSize = context.resources.getDimensionPixelSize(size)
        setSpan(AbsoluteSizeSpan(textSize), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun setClickSpanInternal(onClick: () -> Unit){
        val clickableSpan = object : android.text.style.ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ds.linkColor
                ds.isUnderlineText = false
            }
            override fun onClick(widget: View) {
                onClick.invoke()
            }

        }
        if (length < 0) return
        setSpan(clickableSpan, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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

    fun setFontSpan(res: String, context: Context, start: Int? = null, end: Int? = null) {
        val font = Typeface.createFromAsset(context.assets, res)
        setSpan(
            CustomTypefaceSpan("", font), start ?: 0, end ?: length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}