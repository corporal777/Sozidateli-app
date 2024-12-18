package com.example.util

import android.content.Context
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.data.models.UserProfileFieldsModel
import com.example.ui.state.maxNew.MaxStateScreenType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.ceil


object Utils {

    fun formatMobilePhone(phone: String?): String {
        return if (phone.isNullOrEmpty()) ""
        else if (phone.length == 12) {
            StringBuilder(phone)
                .insert(2, " ")
                .insert(6, " ")
                .insert(10, " ")
                .insert(13, " ").toString()
        } else phone
    }

    fun isPhone(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[0-9]+")
        return regex.containsMatchIn(text)
    }

    fun isContainsNumbers(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[0-9]+")
        return regex.containsMatchIn(text)
    }

    fun isContainLetters(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[A-Za-z]+")
        return regex.containsMatchIn(text)
    }

    fun isEmailValid(email: String?): Boolean {
        return if (email.isNullOrEmpty()) false
        else AuthValidateUtil.isValidEmail(email)
    }

    fun isPhoneNumberValid(phone: String?): Boolean {
        var valid = true
        if (!phone.isNullOrEmpty()) {
            if (phone.contains("+")) {
                if (phone.length == 12) {
                    if (phone.substring(0, 3) != "+79") valid = false
                } else valid = false
            } else {
                if (phone.length == 11) {
                    val firstNumber = phone.substring(0, 2)
                    if (firstNumber != "79" && firstNumber != "89") valid = false
                } else valid = false
            }
        } else {
            valid = false
        }
        return valid
    }

    fun validatePhoneBeforeSend(phone: String): String {
        if (phone == "") return ""
        val phoneResult = if (!phone.contains("+")) "+$phone" else phone
        val sb = StringBuilder(phoneResult)
        if (phone.substring(0, 1) == "8")
            sb.setCharAt(1, '7')
        return sb.toString()
    }

    fun timerFormatter(time: Int, context: Context): String =
        if (time > 119) {
            val seconds = time - 60
            val minute = ceil((time - seconds).toDouble() / 60).toInt()
            context.resources.getQuantityString(R.plurals.minutes_timer, minute, minute)
        } else if (time > 59) {
            val minute = ceil(time.toDouble() / 60).toInt()
            context.resources.getQuantityString(R.plurals.minutes_timer, minute, minute)
        } else context.resources.getQuantityString(R.plurals.seconds_timer, time, time)


    fun maxStateScreen(user: UserDetail): MaxStateScreenType {
        val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }
        val isWorkPhone = if (workPhone?.absent == true) false else workPhone?.value.isNullOrEmpty()
        val links = user.contactInformation.socialLinks
        val isLinks = if (links?.absent == true) false else links?.values?.isNullOrEmpty()
        val works = user.binds?.workExperience
        val isWork = if (works?.absent == true) false else works?.models?.isNullOrEmpty()
        return if (user.notes?.value.isNullOrEmpty() || (isLinks == true) || isWorkPhone) MaxStateScreenType.BASE
        else if (!user.isHasInterests()) MaxStateScreenType.INTERESTS
        else if (isWork == true) MaxStateScreenType.WORK
        else if (user.binds?.education.isNullOrEmpty()) MaxStateScreenType.EDUCATION
        else MaxStateScreenType.DONE
    }

    fun maxStateScreenNew(state: UserProfileFieldsModel): MaxStateScreenType {
        val workPhone = state.fields?.find { it.name == "workPhone" }?.filled ?: false
        val contacts = state.fields?.find { it.name == "contactInformation" }?.filled ?: false
        val notes = state.fields?.find { it.name == "notes" }?.filled ?: false

        val interests = state.fields?.find { it.name == "userInterest" }?.filled ?: false
        val workExperience = state.fields?.find { it.name == "userWorkExperience" }?.filled ?: false
        val education = state.fields?.find { it.name == "userEducation" }?.filled ?: false

        return if (!workPhone || !contacts || !notes) MaxStateScreenType.BASE
        else if (!interests) MaxStateScreenType.INTERESTS
        else if (!education) MaxStateScreenType.EDUCATION
        else if (!workExperience) MaxStateScreenType.WORK
        else MaxStateScreenType.DONE
    }
}