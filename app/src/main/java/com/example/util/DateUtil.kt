package com.example.util

import com.example.common.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.example.common.DATE_FORMAT_SERVER_TIMESTAMP
import java.text.SimpleDateFormat
import java.util.*

fun validateEndDate(mStart: String?, mFinish: String?): Boolean {
    return if (!mStart.isNullOrBlank() && !mFinish.isNullOrBlank()) {
        val start = serverDateToMilliseconds(mStart ?: "", DATE_FORMAT_SERVER_TIMESTAMP)
        val end = serverDateToMilliseconds(mFinish ?: "", DATE_FORMAT_SERVER_TIMESTAMP)
        start > end
    } else {
        false
    }
}

fun serverDateToMilliseconds(date: String, format: String): Long =
    SimpleDateFormat(format, Locale.getDefault()).parse(date).time

fun profileDateFormat(year: Int, month: Int, day: Int) =
    formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS, year, month, day).capitalize()

fun formatDate(format: String, year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
    }

    return formatDate(format, calendar.timeInMillis)
}

fun formatDate(format: String, date: Long): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(date)
}

fun formatDateYear(date: Date): String {
    return formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS, date.time)
}