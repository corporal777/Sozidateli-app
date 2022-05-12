package com.example.util

import android.text.NoCopySpan
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

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