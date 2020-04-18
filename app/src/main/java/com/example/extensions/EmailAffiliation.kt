package com.example.extensions

import android.text.Spannable
import android.text.SpannableStringBuilder
import com.example.data.models.EmailAffiliation

fun EmailAffiliation.getAffiliationString(underlinedEmail: Boolean = false): Spannable {
    val items = listOfNotNull(
            affiliation?.takeIf { it.isNotBlank() },
            email.takeIf { it.isNotBlank() }?.let {
                if (underlinedEmail) it.setUnderlineSpan()
                else it
            }
    )
    return items.joinTo(buffer = SpannableStringBuilder(), separator = ": ")
}