package com.example.util

import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.R
import kotlinx.android.synthetic.main.fragment_login_email.*
import java.util.regex.Pattern


object AuthUtil {


    fun enableButton(button: Button,isEnable:Boolean){
        button.apply {
            isEnabled = isEnable

            val background: Int
            val textColor: Int
            if (isEnable) {
                background = R.drawable.background_btn_auth
                textColor = Color.WHITE
            } else {
                background = R.drawable.background_disabled_btn_login
                textColor = ContextCompat.getColor(context, R.color.disabled_color)
            }

            setBackgroundResource(background)
            setTextColor(textColor)
        }
    }

    fun colorTextPasswordChecker(textView: TextView, has: Boolean) {
        val colorRed = Color.RED
        val colorGreen = ContextCompat.getColor(textView.context, R.color.auth_accept_green)

        textView.setTextColor(if (has) colorGreen else colorRed)
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"
        val pattern: Pattern
        pattern = Pattern.compile(emailRegEx)
        val matcher = pattern.matcher(email)
        return matcher.find()
    }

    fun isValidPassword(password: String): Boolean {
//        val regex = "^(?=.*[A-Z])(?=.*[@#\$%^&+=]).{6,}\$"
//        val pattern = Pattern.compile(regex)
//        val matcher = pattern.matcher(password)
//        return matcher.find()
        return password.length >= 5
    }

//    fun isPasswordHasSix(password: String): Boolean {
//        return password.length >= 6
//    }
//
//    fun isPasswordHasOneCap(password: String): Boolean {
//        val regex = "^(?=.*[A-Z])"
//        val pattern = Pattern.compile(regex)
//        val matcher = pattern.matcher(password)
//        return matcher.find()
//    }
//
//    fun isPasswordHasSymbol(password: String): Boolean {
//        val regex = "^(?=.*[@#\$%^&+=])"
//        val pattern = Pattern.compile(regex)
//        val matcher = pattern.matcher(password)
//        return matcher.find()
//    }
}

