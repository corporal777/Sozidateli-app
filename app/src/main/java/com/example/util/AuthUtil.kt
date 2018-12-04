package com.example.util

import java.util.regex.Pattern


object AuthUtil {

    fun isValidEmail(email: String): Boolean {
        val emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"
        val pattern: Pattern
        pattern = Pattern.compile(emailRegEx)
        val matcher = pattern.matcher(email)
        return matcher.find()
    }

    fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[@#\$%^&+=]).{6,}\$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }

    fun isPasswordHasSix(password: String): Boolean {
        return password.length >= 6
    }

    fun isPasswordHasOneCap(password: String):Boolean{
        val regex = "^(?=.*[A-Z])"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }

    fun isPasswordHasSymbol(password: String):Boolean{
        val regex = "^(?=.*[@#\$%^&+=])"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }

}

