package com.example.util

import android.util.Log
import java.util.regex.Pattern


object AuthValidateUtil {
    private const val cyrillic = "\\p{Cyrillic}\\u0400-\\u04FF"
    private const val symbols = "\\@\\#\\\$\\_\\&\\-\\+\\(\\)\\/\\*\\\"\\'\\:\\;\\!\\?\\,\\.\\~\\`\\|\\÷\\×\\^\\=\\{\\}\\%\\<\\>"

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z\\($cyrillic)\\($symbols)0-9]{1,256}" +
                "\\@[a-zA-Z\\($cyrillic)0-9]{0,64}" +
                "(\\.[a-zA-Z\\($cyrillic)0-9]{0,25})+"
    )


    private val NEW_EMAIL_PATTERN = Pattern.compile(
        "^[_A-Za-z\\($cyrillic)0-9-+]" +
                "+(\\.[_A-Za-z\\($cyrillic)0-9-+]+)*@[A-Za-z\\($cyrillic)0-9-]" +
                "+(\\.[A-Za-z\\($cyrillic)0-9-]+)*(\\.[A-Za-z‌​\\($cyrillic)]{2,})$"
    )

    private val PHONE_PATTERN = Pattern.compile("^[0-9\\+]+$")
    private val PASSWORD_PATTERN = Pattern.compile("^.{6,}\$")

    fun isValidEmail(email: CharSequence): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isDigits(email: CharSequence) = PHONE_PATTERN.matcher(email).matches()

    fun isValidPassword(password: CharSequence) = PASSWORD_PATTERN.matcher(password).matches()

    fun isValidPhone(phone: String) = Utils.isNewPhoneIsValid(phone)
}

