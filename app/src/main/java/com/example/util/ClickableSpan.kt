package com.example.util

import android.text.NoCopySpan
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

class ClickableSpan(
    private val drawUnderline: Boolean = true,
    private val onClick: () -> Unit
) : ClickableSpan(), NoCopySpan {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = ds.linkColor
        ds.isUnderlineText = drawUnderline
    }

    override fun onClick(widget: View) {
        onClick()
    }
}

class ClickableSpanNew(
    private val textView: TextView,
    private val onClick: () -> Unit
) : ClickableSpan(), NoCopySpan {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = ds.linkColor
        ds.isUnderlineText = false
        textView.invalidate()
    }

    override fun onClick(widget: View) {
        onClick.invoke()
        widget.invalidate()
    }
}

