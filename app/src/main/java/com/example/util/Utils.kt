package com.example.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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
}