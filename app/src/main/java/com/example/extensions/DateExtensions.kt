package com.example.extensions

import com.example.util.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val defaultDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

val defaultServerDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

val defaultServerDateTimeFormatter: DateFormat
    get() = SimpleDateFormat(DATE_TIME_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

val defaultTimeFormatter: DateFormat
    get() = SimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.getDefault())

val defaultDAteTimeFormatterNoYear: DateFormat
    get() = SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT_NO_YEAR, Locale.getDefault())

fun String.formatToDefaultDate(): String? {
    val serverDate = try {
        defaultServerDateFormatter.parse(this)
    } catch (e: ParseException) {
        return null
    }

    return defaultDateFormatter.format(serverDate)
}

fun String.formatDefaultServerTimeToDefaultTimeInterval(to: String, formatter: DateFormat = defaultServerDateTimeFormatter): String? {
    val start = formatter.parse(this)
    val finish = formatter.parse(to)
    return start.calendar().formatToDefaultTimeInterval(finish.calendar())
}

fun Calendar.formatToDefaultTimeInterval(to: Calendar): String {
    val formatter = if (this.isSameYear(to)) {
        if (this.isSameDay(to)) defaultTimeFormatter
        else defaultDAteTimeFormatterNoYear
    } else defaultDateFormatter

    return "${formatter.format(this.time)} - ${formatter.format(to.time)}"
}

fun Long.calendar(): Calendar = Calendar.getInstance().apply { timeInMillis = this@calendar }

fun Date.calendar(): Calendar = Calendar.getInstance().apply { time = this@calendar }

fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) &&
            this.get(Calendar.YEAR) == other.get(Calendar.YEAR)
}

fun Calendar.isSameYear(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR)
}