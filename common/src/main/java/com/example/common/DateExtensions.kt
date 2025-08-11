package com.example.common

import com.example.common.constants.DATE_FORMAT_FULL_DAY_FULL_MONTH_NO_YEAR
import com.example.common.constants.DATE_FORMAT_FULL_MONTH_FULL_YEAR
import com.example.common.constants.DATE_FORMAT_FULL_MONTH_NO_YEAR
import com.example.common.constants.DATE_FORMAT_SERVER_TIMESTAMP
import com.example.common.constants.DATE_FORMAT_SHORT_DAY_FULL_MONTH_FULL_YEAR
import com.example.common.constants.DATE_FORMAT_SHORT_DAY_FULL_MONTH_SHORT_YEAR
import com.example.common.constants.DATE_FORMAT_SHORT_DAY_SHORT_MONTH_FULL_YEAR_VK
import com.example.common.constants.DATE_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.common.constants.DATE_FORMAT_SHORT_MONTH_NO_YEAR
import com.example.common.constants.DATE_FORMAT_SHORT_MONTH_SHORT_YEAR
import com.example.common.constants.DATE_TIME_FORMAT_DEFAULT
import com.example.common.constants.DATE_TIME_FORMAT_DEFAULT_NO_YEAR
import com.example.common.constants.DATE_TIME_FORMAT_SERVER_TIMESTAMP
import com.example.common.constants.TIME_FORMAT_DEFAULT
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val defaultDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

val dateFormatterShortMoth: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault())

val dateFormatterShortMonthShortYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_SHORT_YEAR, Locale.getDefault())

val dateFormatterShortMothNoYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_NO_YEAR, Locale.getDefault())

val dateFormatterFullMothNoYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_FULL_MONTH_NO_YEAR, Locale.getDefault())

val dateFormatterFullMothFullYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR, Locale.getDefault())

val dateFormatterFullDayFullMonthNoYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_FULL_DAY_FULL_MONTH_NO_YEAR, Locale.getDefault())

val dateFormatterShortDayFullMothShortYear: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_DAY_FULL_MONTH_SHORT_YEAR, Locale.getDefault())

val defaultServerDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SERVER_TIMESTAMP, Locale.getDefault())

val defaultVkDateFormatter: DateFormat
    get() = SimpleDateFormat(DATE_FORMAT_SHORT_DAY_SHORT_MONTH_FULL_YEAR_VK, Locale.getDefault())

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

fun String.formatToDefaultTime(): String? {
    return parseAndFormat(defaultServerDateTimeFormatter, defaultTimeFormatter)
}

fun String.formatToDefaultDateTime(): String? {
    return parseAndFormat(defaultServerDateTimeFormatter, defaultDateTimeFormatter)
}

fun String.formatFromVkToDefaultDate(): String? {
    return parseAndFormat(defaultVkDateFormatter, defaultDateFormatter)
}

fun String.formatToDefaultServerDate(): String? {
    return parseAndFormat(defaultDateFormatter, defaultServerDateFormatter)
}

fun String.formatToDefaultDayMonthDate(): String? {
    return parseAndFormat(defaultServerDateFormatter, dateFormatterFullMothNoYear)
}

fun String.formatToDefaultDayMonthYearDate(): String? {
    return parseAndFormat(defaultServerDateFormatter, dateFormatterFullMothFullYear)
}

fun String.parseToDate(parser: DateFormat): Date? {
    return try {
        parser.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun longToTime(date: Date): String = SimpleDateFormat(TIME_FORMAT_DEFAULT).format(date)

fun String.parseToLong(parser: DateFormat): Long? = parseToDate(parser)?.let { it.time }

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


private fun Calendar.formatToInterval(to: Calendar, withTime: Boolean): String {
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

fun String?.formatTimeIntervalFromTo(to: String?, parser: DateFormat = defaultServerDateTimeFormatter, withTime: Boolean = false): String? {
    if (this == null || to == null) return null

    val start = parser.parse(this)
    val finish = parser.parse(to)

    return start.calendar().formatTimeIntervalFromTo(finish.calendar(), withTime)
}


private fun Calendar.formatTimeIntervalFromTo(to: Calendar, withTime: Boolean): String {

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
    val endCalendar = endDate?.calendar()?.takeIf { startCalendar?.isSameDay(it) != true }

    val startFormatter = if (startCalendar != null) {
        SimpleDateFormat(DATE_FORMAT_SHORT_DAY_FULL_MONTH_FULL_YEAR, Locale.getDefault())
    } else {
        null
    }

    val endFormatter = if (endCalendar != null) {
        SimpleDateFormat(DATE_FORMAT_SHORT_DAY_FULL_MONTH_FULL_YEAR, Locale.getDefault())
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


fun Long.calendar(): Calendar = Calendar.getInstance().apply { timeInMillis = this@calendar }

fun Date.calendar(): Calendar = Calendar.getInstance().apply { time = this@calendar }

fun String.calendar(formatter: DateFormat): Calendar? = parseToDate(formatter)?.calendar()

fun Calendar.isSameDay(other: Calendar): Boolean {
    return isSameYear(other) && this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

fun Calendar.isYesterday(from: Calendar): Boolean {
    return isSameYear(from) && this.get(Calendar.DAY_OF_YEAR) == from.get(Calendar.DAY_OF_YEAR) - 1
}

fun Calendar.isSameMonth(other: Calendar): Boolean {
    return isSameYear(other) && this.get(Calendar.MONTH) == other.get(Calendar.MONTH)
}

fun Calendar.isSameYear(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR)
}

fun Calendar.getCalendarDay(short : Boolean): String {
    if (short) return this.get(Calendar.DAY_OF_MONTH).toString()
    else {
        val day = this.get(Calendar.DAY_OF_MONTH)
        return if (day >= 10) day.toString()
        else "0$day"
    }
}
fun Calendar.getCalendarMonth(short : Boolean): String {
    if (short) return (this.get(Calendar.MONTH) + 1).toString()
    else {
        val month = (this.get(Calendar.MONTH) + 1)
        return if (month >= 10) month.toString()
        else "0$month"
    }
}
fun Calendar?.getCalendarYear(): Int {
    return this?.get(Calendar.YEAR) ?: 0
}
fun Calendar.getCalendarDayOfWeek() : String?{
    return this.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
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

fun getCurrentYear(): Int = System.currentTimeMillis().calendar().get(Calendar.YEAR)
fun getCurrentMonth(): Int = System.currentTimeMillis().calendar().get(Calendar.MONTH)
fun getCurrentDay(): Int = System.currentTimeMillis().calendar().get(Calendar.DAY_OF_MONTH)

fun daysBetween(d1: Long?, d2: Long): Int {
    var days = 0
    val dateOne = d1 ?: System.currentTimeMillis()
    for (i in dateOne..d2 step 86400000) days++
    return days
}