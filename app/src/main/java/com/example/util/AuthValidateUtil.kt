package com.example.util

import android.util.Log
import java.util.regex.Pattern


object AuthValidateUtil {

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\@\\#\\$\\_\\&\\-\\+\\(\\)\\/\\*\\\"\\'\\:\\;\\!\\?\\,\\.\\~\\`\\|\\÷\\×\\^\\=\\{\\}\\%\\<\\>]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private const val cyrillic = "\\p{Cyrillic}\\u0400-\\u04FF"
    private val NEW_EMAIL_PATTERN = Pattern.compile(
        "^[_A-Za-z\\($cyrillic)0-9-+]" +
                "+(\\.[_A-Za-z\\($cyrillic)0-9-+]+)*@[A-Za-z\\($cyrillic)0-9-]" +
                "+(\\.[A-Za-z\\($cyrillic)0-9-]+)*(\\.[A-Za-z‌​\\($cyrillic)]{2,})$"
    )

    private val PHONE_PATTERN = Pattern.compile("^[0-9\\+]+$")
    private val PASSWORD_PATTERN = Pattern.compile("^.{6,}\$")

    fun isValidEmail(email: CharSequence): Boolean {
        //return EMAIL_PATTERN.matcher(email).matches()
        return NEW_EMAIL_PATTERN.matcher(email).matches()
    }

    fun isDigits(email: CharSequence) = PHONE_PATTERN.matcher(email).matches()

    fun isValidPassword(password: CharSequence) = PASSWORD_PATTERN.matcher(password).matches()

    fun isValidPhone(phone: String) = Utils.isNewPhoneIsValid(phone)

    fun isCorrectEmail(email: String?): Boolean {
        var isValid = true
        val login = StringBuilder(email ?: "")

        if (login.isNullOrEmpty()) isValid = false
        else {
            if (!login.contains("@")) isValid = false
            if (login.contains("@")) {
                val secondStr = login.substring(login.indexOf("@"), login.length)
                if (!secondStr.contains(".")) isValid = false
                else {
                    val thirdStr = secondStr.substring(secondStr.indexOf("."))
                    if (thirdStr.length <= 2) isValid = false
                }
            }
        }

        return isValid
    }
}

