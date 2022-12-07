package com.example.util

import android.content.Context
import android.text.NoCopySpan
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.example.R
import com.example.util.qr_generator.style.Color

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