package com.example.extensions

import com.example.util.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val defaultDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

val dateFormatterShortMoth: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

val dateFormatterShortMothShortYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_SHORT_YEAR, Locale.getDefault())

val dateFormatterShortMothNoYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_NO_YEAR, Locale.getDefault())

val dateFormatterFullMothNoYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_FULL_MONTH_NO_YEAR, Locale.getDefault())

val defaultServerDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

val defaultServerDateTimeFormatter: DateFormat
    get() = SimpleDateFormat(DATE_TIME_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

val defaultTimeFormatter: DateFormat
    get() = SimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.getDefault())

val defaultDateTimeFormatter: DateFormat
    get() = SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT, Locale.getDefault())

val defaultDateTimeFormatterNoYear: DateFormat
    get() = SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT_NO_YEAR, Locale.getDefault())

fun String.formatToDefaultDate(): String? {
    return parseAndFormat(defaultServerDateFormatter, defaultDateFormatter)
}

fun String.formatToDefaultServerDate(): String? {
    return parseAndFormat(defaultDateFormatter, defaultServerDateFormatter)
}

fun String.parseToDate(parser: DateFormat): Date? {
    return try {
        parser.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.parseAndFormat(parser: DateFormat, formatter: DateFormat): String? {
    return parseToDate(parser)?.let { formatter.format(it) }
}

fun String.formatDefaultServerTimeToDefaultTimeInterval(to: String, formatter: DateFormat = defaultServerDateTimeFormatter, withTime: Boolean = true): String? {
    val start = formatter.parse(this)
    val finish = formatter.parse(to)
    return start.calendar().formatToDefaultTimeInterval(finish.calendar(), withTime)
}

fun String?.formatServerDateOrDefault(format: String, default: String): String {
    val date = this ?: return default
    val parsed = with(defaultServerDateFormatter) {
        try {
            parse(date)
        } catch (e: Throwable) {
            null
        }
    } ?: return default

    return SimpleDateFormat(format, Locale.getDefault()).format(parsed)
}

fun Calendar.formatToDefaultTimeInterval(to: Calendar, withTime: Boolean): String {
    val formatter = if (withTime) {
        if (isSameYear(to)) {
            if (isSameDay(to)) defaultTimeFormatter
            else defaultDateTimeFormatterNoYear
        } else defaultDateFormatter
    } else {
        if (isSameYear(to)) dateFormatterShortMothNoYear
        else defaultDateFormatter
    }

    return "${formatter.format(this.time)} - ${formatter.format(to.time)}"
}

fun Calendar.formatToDefaultTime(): String {
    val formatter = if (this.isSameYear(Calendar.getInstance())) defaultDateTimeFormatterNoYear
    else defaultDateFormatter

    return formatter.format(this.time)
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

fun Long.startOfDay(): Long {
    return calendar().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
            .timeInMillis
}

fun Long.endOfDay(): Long {
    return calendar().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
            .timeInMillis
}