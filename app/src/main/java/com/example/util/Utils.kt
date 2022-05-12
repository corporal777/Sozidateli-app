package com.example.util

import android.content.Context
import com.example.R
import com.example.data.models.UserDetail
import com.example.ui.state.max.MaxStateScreenType
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


object Utils {

    val defaultServerDateFormatter: DateFormat
        get() = SimpleDateFormat(DATE_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

    fun getDatesInterval(startDate: String?, finishDate: String?): String {
        return getDatesInterval(if (startDate == null) 0 else defaultServerDateFormatter.parse(startDate).time, if (finishDate == null) 0 else defaultServerDateFormatter.parse(finishDate).time)
    }

    fun getDatesInterval(startDate: Long, finishDate: Long): String {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

        val startFormat = if (start.get(Calendar.YEAR) == finish.get(Calendar.YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
        val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
        var formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

        if (finishDate == 0L) formattedFinish = "н.в"

        return "$formattedStart - $formattedFinish"
    }

    fun isPhone(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[0-9]+")
        return regex.containsMatchIn(text)
    }

    fun isContainLetters(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        val regex = Regex(pattern = "[A-Za-z]+")
        return regex.containsMatchIn(text)
    }

    fun newPhoneValidator(context: Context, phone: String): Boolean {
        var isValid = true
        if (phone.contains("+")) {
            if (phone.length == 12) {
                if (phone.substring(0, 3) != "+79") isValid = false
            } else isValid = false
        } else {
            if (phone.length == 11) {
                val firstNumber = phone.substring(0, 2)
                if (firstNumber != "79" && firstNumber != "89") isValid = false
            } else isValid = false
        }
        /*val phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        val parsedPhone = try {
            phoneNumberUtil.parse(phone, null)
        } catch (e: Throwable) {
            return false
        }
        return phoneNumberUtil.isValidNumber(parsedPhone)*/
        return isValid
    }

    fun isNewPhoneIsValid(phone: String?): Boolean {
        var valid = true
        if (!phone.isNullOrEmpty()){
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
        }else {
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

    fun timerFormatter(time: Int, context: Context) : String =
        if (time > 59) {
            val minute = ceil(time.toDouble() / 60).toInt()
            context.resources.getQuantityString(R.plurals.minutes_timer, minute, minute)
        } else context.resources.getQuantityString(R.plurals.seconds_timer, time, time)

    fun maxStateScreen(user: UserDetail): MaxStateScreenType {
        val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }
        val isWorkPhone = if (workPhone?.absent == true) false else workPhone?.value.isNullOrEmpty()
        val sites = user.contactInformation.site
        val isSite = if (sites?.absent == true) false else sites?.values?.isNullOrEmpty()
        val links = user.contactInformation.socialLinks
        val isLinks = if (links?.absent == true) false else links?.values?.isNullOrEmpty()
        val works = user.binds?.workExperience
        val isWork = if (works?.absent == true) false else works?.models?.isNullOrEmpty()
        return if (/*user.binds?.recommendationFile.isNullOrEmpty() ||*/ user.notes?.value.isNullOrEmpty() ||
                (isSite == true) || (isLinks == true) || isWorkPhone || user.image?.uri.isNullOrEmpty()) MaxStateScreenType.BASE
        else if (user.interests.isNullOrEmpty()) MaxStateScreenType.INTERESTS
        else if (isWork == true) MaxStateScreenType.WORK
        else if (user.binds?.education.isNullOrEmpty()) MaxStateScreenType.EDUCATION
        else MaxStateScreenType.DONE
    }
}