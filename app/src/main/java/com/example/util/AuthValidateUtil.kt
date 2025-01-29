package com.example.util

import java.util.regex.Pattern


object AuthValidateUtil {
    private const val cyrillic = "\\p{Cyrillic}\\u0400-\\u04FF"
    private const val symbols =
        "\\@\\#\\\$\\_\\&\\-\\+\\(\\)\\/\\*\\\"\\'\\:\\;\\!\\?\\,\\.\\~\\`\\|\\÷\\×\\^\\=\\{\\}\\%\\<\\>"

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

//    private val WEB_SITE_PATTERN =
//        Pattern.compile("^(http(s)?:\\/\\/.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$")

    private val WEB_SITE_PATTERN = Pattern.compile(
        "^(http(s)?:\\/\\/.)?[a-zA-Z\\($cyrillic)0-9@:%._\\+~#=]{2,256}" +
                "\\.[a-z\\($cyrillic)]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$"
    )


    private val PHONE_PATTERN = Pattern.compile("^[0-9\\+]+$")
    private val PASSWORD_PATTERN = Pattern.compile("^.{6,}\$")

    fun isDigits(email: CharSequence) = PHONE_PATTERN.matcher(email).matches()

    fun isValidPassword(password: CharSequence) = PASSWORD_PATTERN.matcher(password).matches()

    fun isValidEmail(email: CharSequence) = EMAIL_PATTERN.matcher(email).matches()

    fun isValidPhone(phone: String) = Utils.isPhoneNumberValid(phone)

    fun isValidSite(site: String?) = WEB_SITE_PATTERN.matcher(site ?: "").matches()
}

