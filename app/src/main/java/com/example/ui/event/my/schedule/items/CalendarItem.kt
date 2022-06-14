package com.example.ui.event.my.schedule.items

import android.util.Log
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.CustomCalendarItemBinding
import com.example.extensions.calendar
import com.example.util.getMonthName
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import setOnClickListener
import java.util.*

class CalendarItem(val date: EventScheduleCalendarDay) : BindableItem<CustomCalendarItemBinding>() {

    private val weekDaySection = Section()
    private val daySection = Section()
    private var mCounter = 0
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(weekDaySection)
        add(daySection)
    }

    private var mCalendar = date.millis.calendar()

    init {
        val listWeekDays = listOf("пн", "вт", "ср", "чт", "пт", "сб", "вс")
        weekDaySection.update(
            listWeekDays.map { day ->
                WeekDayItem(day)
            }
        )
        setDays(mCalendar)
    }


    override fun bind(viewBinding: CustomCalendarItemBinding, position: Int) {
        viewBinding.apply {
            calendarList.adapter = groupAdapter
            monthLabel.text =
                getMonthName(mCalendar.get(Calendar.MONTH)) + " " + mCalendar.get(Calendar.YEAR)

            ivNext.setOnClickListener {
                mCounter += 1
                val mCal = Calendar.getInstance()
                mCal.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) + mCounter)
                setDays(mCal)
                monthLabel.text =
                    getMonthName(mCal.get(Calendar.MONTH)) + " " + mCal.get(Calendar.YEAR)
                Log.e("MONTH", getMonthName(mCal.time.calendar().get(Calendar.MONTH)))
            }
        }

    }


    private fun setDays(calendar: Calendar) {

        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val list = arrayListOf<DateHolder>()
        for (i in 0 until maxDay) {
            calendar[Calendar.DAY_OF_MONTH] = i + 1
            val dayOfMonth = calendar.time.calendar().get(Calendar.DAY_OF_MONTH)
            list.add(DateHolder(dayOfMonth, calendar.time))
        }
        val firstDate = list[0].date?.calendar()?.get(Calendar.DAY_OF_WEEK)
        if (firstDate != 2) {
            if (firstDate == 1){
                for (i in 0 until 6) {
                    list.add(0, DateHolder(0, null))
                }
            }else {
                var prevDays = 7 - firstDate!!
                for (i in prevDays until 7 - 2) {
                    list.add(0, DateHolder(0, null))
                }
            }

        }
        daySection.update(
            list.map {
                MonthDayItem(it)
            }
        )
    }

    override fun getLayout(): Int = R.layout.custom_calendar_item
}