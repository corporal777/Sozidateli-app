package com.example.util

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.*

class WeekDayValidator() : CalendarConstraints.DateValidator {

    private val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    @JvmField
    val CREATOR: Parcelable.Creator<WeekDayValidator?> =
            object : Parcelable.Creator<WeekDayValidator?> {
                override fun createFromParcel(source: Parcel): WeekDayValidator {
                    return WeekDayValidator()
                }

                override fun newArray(size: Int): Array<WeekDayValidator?> {
                    return arrayOfNulls(size)
                }
            }

    override fun writeToParcel(p0: Parcel, p1: Int) {

    }

    override fun isValid(date: Long): Boolean {
        val selectedDate = Calendar.getInstance()
        selectedDate.add(Calendar.YEAR, -14)
        utc.timeInMillis = date
        return utc.timeInMillis < selectedDate.timeInMillis
    }

    override fun describeContents(): Int {
        return 0
    }



    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any>()
        return hashedFields.contentHashCode()
    }
}