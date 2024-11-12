package com.example.util

import android.util.Log
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

fun getDaysFromMondayToSunday(startDate: String, endDate: String): List<String> {
    val timeLong = System.currentTimeMillis()
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val mDates = arrayListOf<String>()
    val mEventStartDate = defaultServerDateFormatter.parse(startDate).time
    val mCalStartEvent = mEventStartDate.calendar()
    val mWeekOfMonth = mCalStartEvent.get(Calendar.WEEK_OF_MONTH)
    var mDayOfWeek = mCalStartEvent.get(Calendar.DAY_OF_WEEK)

    var mStartDay = 0
    if (mDayOfWeek != 2) {
        val mCalOfMonday = Calendar.getInstance()
        mCalOfMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        //mCalOfMonday.set(Calendar.DAY_OF_WEEK_IN_MONTH, mWeekOfMonth)
        mCalOfMonday.set(Calendar.WEEK_OF_MONTH, mWeekOfMonth)
        mCalOfMonday.set(Calendar.MONTH, mCalStartEvent.get(Calendar.MONTH))
        mCalOfMonday.set(Calendar.YEAR, mCalStartEvent.get(Calendar.YEAR))

        if (mCalOfMonday.get(Calendar.MONTH) == mCalStartEvent.get(Calendar.MONTH)) {
            if (mCalOfMonday.get(Calendar.DATE) > mCalStartEvent.get(Calendar.DATE)) {
                val day = mCalOfMonday.get(Calendar.DATE)
                if ((day - 7) < 0) {
                    mStartDay = 0
                    var diff = 7 - day
                    val prevCal = Calendar.getInstance()
                    prevCal.set(Calendar.MONTH, mCalOfMonday.get(Calendar.MONTH) - 1)
                    val maxDay = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    diff = maxDay - diff
                    for (i in diff - 1 until maxDay) {
                        prevCal[Calendar.DAY_OF_MONTH] = i + 1
                        if (!mDates.contains(df.format(prevCal.time))) {
                            mDates.add(df.format(prevCal.time))
                        }
                    }
                } else {
                    mStartDay = mCalOfMonday.get(Calendar.DATE) - 7
                    mStartDay -= 1
                }
            } else {
                mStartDay = mCalOfMonday.get(Calendar.DATE) - 1
            }
        } else if (mCalOfMonday.get(Calendar.MONTH) < mCalStartEvent.get(Calendar.MONTH)) {
            mStartDay = 0
            val mMaxDays = mCalOfMonday.getActualMaximum(Calendar.DAY_OF_MONTH)
            val day = mCalOfMonday.get(Calendar.DATE) - 1
            for (i in day until mMaxDays) {
                mCalOfMonday[Calendar.DAY_OF_MONTH] = i + 1
                mDates.add(df.format(mCalOfMonday.time))
            }
        }
        val mMaxDays = mCalStartEvent.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in mStartDay until mMaxDays) {
            mCalStartEvent[Calendar.DAY_OF_MONTH] = i + 1
            mDates.add(df.format(mCalStartEvent.time))
        }
    } else {
        val mMaxDays = mCalStartEvent.getActualMaximum(Calendar.DAY_OF_MONTH)
        mStartDay = mCalStartEvent.get(Calendar.DAY_OF_MONTH) - 1
        for (i in mStartDay until mMaxDays) {
            mCalStartEvent[Calendar.DAY_OF_MONTH] = i + 1
            mDates.add(df.format(mCalStartEvent.time))
        }
    }

    val mEventEndDate = defaultServerDateFormatter.parse(endDate).time
    val mCalEndEvent = mEventEndDate.calendar()
    val mWeekOfMonthEnd = mCalEndEvent.get(Calendar.WEEK_OF_MONTH)
    var mDayOfWeekEnd = mCalEndEvent.get(Calendar.DAY_OF_WEEK)
    var mDayOfMonth = mCalEndEvent.get(Calendar.DAY_OF_MONTH)

    val startMonth = mCalStartEvent.get(Calendar.MONTH)
    val endMonth = mCalEndEvent.get(Calendar.MONTH)

    if ((endMonth - startMonth) > 1) {
        for (i in startMonth until endMonth - 1) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, mCalStartEvent.get(Calendar.YEAR))
            cal.set(Calendar.MONTH, i + 1)
            val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            (0 until maxDay).forEach { k ->
                cal[Calendar.DAY_OF_MONTH] = k + 1
                if (!mDates.contains(df.format(cal.time))) {
                    mDates.add(df.format(cal.time))
                }
            }
        }

    }


    var mEndDay = 0
    if (mDayOfWeekEnd != 1) {
        val mCalOfSunday = Calendar.getInstance()
        mCalOfSunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        mCalOfSunday.set(Calendar.DAY_OF_WEEK_IN_MONTH, mWeekOfMonthEnd)
        mCalOfSunday.set(Calendar.MONTH, mCalEndEvent.get(Calendar.MONTH))
        mCalOfSunday.set(Calendar.YEAR, mCalEndEvent.get(Calendar.YEAR))

        if (mCalOfSunday.get(Calendar.MONTH) == mCalEndEvent.get(Calendar.MONTH)) {
            mEndDay = mCalOfSunday.get(Calendar.DATE)
            if (mCalOfSunday.get(Calendar.DATE) < mCalEndEvent.get(Calendar.DATE)) {
                mEndDay = mCalOfSunday.get(Calendar.DATE) + 7
            }
            for (i in 0 until mEndDay) {
                mCalEndEvent[Calendar.DAY_OF_MONTH] = i + 1
                if (!mDates.contains(df.format(mCalEndEvent.time))) {
                    mDates.add(df.format(mCalEndEvent.time))
                }
            }
        } else {
            val mMaxDays = mCalEndEvent.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 0 until mMaxDays) {
                mCalEndEvent[Calendar.DAY_OF_MONTH] = i + 1
                if (!mDates.contains(df.format(mCalEndEvent.time))) {
                    mDates.add(df.format(mCalEndEvent.time))
                }
            }
            mEndDay = mCalOfSunday.get(Calendar.DATE)
            for (i in 0 until mEndDay) {
                mCalOfSunday[Calendar.DAY_OF_MONTH] = i + 1
                if (!mDates.contains(df.format(mCalOfSunday.time))) {
                    mDates.add(df.format(mCalOfSunday.time))
                }
            }
        }
    } else {
        for (i in 0 until mDayOfMonth) {
            mCalEndEvent[Calendar.DAY_OF_MONTH] = i + 1
            if (!mDates.contains(df.format(mCalEndEvent.time))) {
                mDates.add(df.format(mCalEndEvent.time))
            }
        }
    }
    Log.e("TIME OF FUNC", (System.currentTimeMillis() - timeLong).toString())
    return mDates
}

fun getDaysFromDateToDate(startDate: String, endDate: String): ArrayList<String> {
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val mDates = arrayListOf<String>()

    val mStartDate = defaultServerDateFormatter.parse(startDate).time
    val mEndDate = defaultServerDateFormatter.parse(endDate).time

    for (i in mStartDate..mEndDate step 86400000) {
        val cal = i.calendar()
        val mDate = df.format(cal.time)
        if (!mDates.contains(mDate)) {
            mDates.add(mDate)
        }
    }
    return mDates
}

fun getDaysFromMondayToSundayNew(startDate: String, endDate: String): ArrayList<String> {
    val timeLong = System.currentTimeMillis()
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val mDates = arrayListOf<String>()
    val mCalStartEvent = defaultServerDateFormatter.parse(startDate).time.calendar()
    val mWeekOfMonthStart = mCalStartEvent.get(Calendar.WEEK_OF_MONTH)
    val mStartDayOfWeek = mCalStartEvent.get(Calendar.DAY_OF_WEEK)

    val mCalEndEvent = defaultServerDateFormatter.parse(endDate).time.calendar()
    val mWeekOfMonthEnd = mCalEndEvent.get(Calendar.WEEK_OF_MONTH)
    val mEndDayOfWeek = mCalEndEvent.get(Calendar.DAY_OF_WEEK)

    var mStartDate = defaultServerDateFormatter.parse(startDate).time
    var mEndDate = defaultServerDateFormatter.parse(endDate).time

    if (mStartDayOfWeek != 2) {
        val mCalOfMonday = Calendar.getInstance()
        mCalOfMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        mCalOfMonday.set(Calendar.WEEK_OF_MONTH, mWeekOfMonthStart)
        mCalOfMonday.set(Calendar.MONTH, mCalStartEvent.get(Calendar.MONTH))
        mCalOfMonday.set(Calendar.YEAR, mCalStartEvent.get(Calendar.YEAR))
        mStartDate = mCalOfMonday.timeInMillis
    }
    if (mEndDayOfWeek != 1) {
        val mCalOfSunday = Calendar.getInstance()
        mCalOfSunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        mCalOfSunday.set(Calendar.DAY_OF_WEEK_IN_MONTH, mWeekOfMonthEnd)
        mCalOfSunday.set(Calendar.MONTH, mCalEndEvent.get(Calendar.MONTH))
        mCalOfSunday.set(Calendar.YEAR, mCalEndEvent.get(Calendar.YEAR))

        if (mCalOfSunday.timeInMillis < mCalEndEvent.timeInMillis) {
            mCalOfSunday.set(Calendar.DATE, mCalOfSunday.get(Calendar.DATE) + 7)
        }
        mEndDate = mCalOfSunday.timeInMillis
    }

    for (i in mStartDate..mEndDate step 86400000) {
        val cal = i.calendar()
        val mDate = df.format(cal.time)
        if (!mDates.contains(mDate)) {
            mDates.add(mDate)
        }
    }
    return mDates

}