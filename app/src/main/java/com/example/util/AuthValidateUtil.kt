package com.example.util

import java.util.regex.Pattern


object AuthValidateUtil {

    private val EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    )

    private val PHONE_PATTERN = Pattern.compile(
        "^[0-9\\+]+$"
    )

    private val PASSWORD_PATTERN = Pattern.compile("^.{6,}\$")

    fun isValidEmail(email: CharSequence) = EMAIL_PATTERN.matcher(email).matches()
    fun isDigits(email: CharSequence) = PHONE_PATTERN.matcher(email).matches()

    fun isValidPassword(password: CharSequence) = PASSWORD_PATTERN.matcher(password).matches()

    fun isValidPhone(phone : String) = Utils.isNewPhoneIsValid(phone)
}

