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

val dateFormatterFullMothFullYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR, Locale.getDefault())

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

fun String?.parseAndFormatOrDefault(parser: DateFormat, formatter: DateFormat, default: String?): String? {
    val date = this ?: return default
    val parsed = with(parser) {
        try {
            parse(date)
        } catch (e: Throwable) {
            null
        }
    } ?: return default

    return formatter.format(parsed)
}

fun String?.formatToInterval(to: String?, parser: DateFormat = defaultServerDateFormatter, withTime: Boolean = false): String? {
    if (this == null || to == null) return null

    val start = parser.parse(this)
    val finish = parser.parse(to)

    return start.calendar().formatToInterval(finish.calendar(), withTime)
}

fun Calendar.formatToInterval(to: Calendar, withTime: Boolean): String {
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

fun String?.formatToEventDatesInterval(finish: String?): String? {
    val start = this

    val startDate = start?.parseToDate(defaultServerDateFormatter)
    val endDate = finish?.parseToDate(defaultServerDateFormatter)
    val startCalendar = startDate?.calendar()
    val endCalendar = endDate?.calendar()

    val startFormatter = if (startCalendar != null && endCalendar != null && startCalendar.isSameYear(endCalendar)) {
        SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT_FULL_MONTH, Locale.getDefault())
    } else if (startCalendar != null) {
        SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR, Locale.getDefault())
    } else {
        null
    }

    val endFormatter = if (endCalendar != null) {
        SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR, Locale.getDefault())
    } else {
        null
    }

    return StringBuilder().apply {
        if (startFormatter != null) {
            append(startFormatter.format(startDate))
            if (endFormatter != null) append(" - ")
        }
        if (endFormatter != null) append(endFormatter.format(endDate))
    }.toString()
}

fun Calendar.formatToDefaultTime(): String {
    val formatter = if (this.isSameYear(Calendar.getInstance())) defaultDateTimeFormatterNoYear
    else defaultDateFormatter

    return formatter.format(this.time)
}

fun Long.calendar(): Calendar = Calendar.getInstance().apply { timeInMillis = this@calendar }

fun Date.calendar(): Calendar = Calendar.getInstance().apply { time = this@calendar }

fun Calendar.isSameDay(other: Calendar): Boolean {
    return isSameYear(other) && this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

fun Calendar.isYesterday(from: Calendar): Boolean {
    return isSameYear(from) && this.get(Calendar.DAY_OF_YEAR) == from.get(Calendar.DAY_OF_YEAR) - 1
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