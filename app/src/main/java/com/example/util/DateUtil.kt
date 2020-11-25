package com.example.util

import java.text.SimpleDateFormat
import java.util.*

fun validateEndDate(mStart: String?, mFinish: String?): Boolean {
    return if (!mStart.isNullOrBlank() && !mFinish.isNullOrBlank()) {
        val start = serverDateToMilliseconds(mStart?: "", DATE_FORMAT_SERVER_TIMESTAMP)
        val end = serverDateToMilliseconds(mFinish?: "", DATE_FORMAT_SERVER_TIMESTAMP)
        start >= end
    } else {
        false
    }
}

fun serverDateToMilliseconds(date: String, format: String): Long =
        SimpleDateFormat(format, Locale.getDefault()).parse(date).time