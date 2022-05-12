package com.example.util

import android.text.TextPaint
import android.text.style.URLSpan

class URLSpanNoUnderline(url: String?, private val color: Int? = null) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = color ?: ds.linkColor
        ds.isUnderlineText = false
    }
}