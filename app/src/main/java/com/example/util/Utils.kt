package com.example.util

import java.text.SimpleDateFormat
import java.util.*

object Utils{


    val defaultDataFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())


    fun getDatesInterval(startDate: Long, finishDate: Long):String{
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

        val startFormat = if (start.get(Calendar.YEAR) == finish.get(Calendar.YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
        val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
        val formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

        val result = "$formattedStart - $formattedFinish"

        return result
    }

}