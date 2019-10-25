package com.example.extensions

import android.text.Spannable
import android.text.Spanned
import android.text.style.UnderlineSpan
import androidx.core.text.toSpannable

fun CharSequence.substringToWholeWord(maxLength: Int = this.length): CharSequence {
    return if (length > maxLength) {
        val maxString = subSequence(0, maxLength - 1).trim()
        val spaceIndex = maxString.indexOfLast { it.isWhitespace() }
        if (spaceIndex > 0) maxString.subSequence(0, spaceIndex) else maxString
    } else {
        this
    }
}

fun CharSequence.setRequired(isRequired: Boolean): CharSequence {
    return if (isRequired) "$this*" else this
}

fun String.setUnderlineSpan(start: Int = 0, finish: Int = length): Spannable {
    return toSpannable().apply { setSpan(UnderlineSpan(), start, finish, Spanned.SPAN_INCLUSIVE_INCLUSIVE) }
}