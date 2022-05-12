package com.example.extensions

import android.text.Spannable
import android.text.SpannableStringBuilder
import com.example.data.models.EmailAffiliation
import com.example.data.models.EventPhoneModel

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

fun EventPhoneModel.getAffiliationString(underlinedEmail: Boolean = false): Spannable {
    val items = listOfNotNull(
            title?.takeIf { it.isNotBlank() },
            value.takeIf { it?.isNotBlank() == true }?.let {
                if (underlinedEmail) it.setUnderlineSpan()
                else it
            }
    )
    return items.joinTo(buffer = SpannableStringBuilder(), separator = ": ")
}