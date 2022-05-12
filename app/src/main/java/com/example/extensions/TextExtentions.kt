package com.example.extensions

import android.content.Context
import android.text.Spannable
import android.text.Spanned
import android.text.style.UnderlineSpan
import androidx.core.text.toSpannable
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*

fun CharSequence.substringToWholeWord(maxLength: Int = this.length): CharSequence {
    return if (maxLength in 1 until length) {
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

fun String.getFileNameAndExtension(): Pair<String, String> {
    val extDotIndex = lastIndexOf(".")
    val name = if (extDotIndex == -1) this else substring(0, extDotIndex)
    val extension = substring(extDotIndex + 1, length).toLowerCase(Locale.getDefault())
    return name to extension
}

fun String.parsePhone(context: Context, defaultRegion: String = "RU"): String {
    return PhoneNumberUtil.createInstance(context).let {
        val parsed = kotlin.runCatching { it.parse(this, defaultRegion) }.getOrNull()
        if (parsed != null) it.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        else this
    }
}