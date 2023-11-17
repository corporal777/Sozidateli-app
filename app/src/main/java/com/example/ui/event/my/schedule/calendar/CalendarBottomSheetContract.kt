package com.example.ui.event.my.schedule.calendar

import com.example.ui.base.BaseContract
import com.pagercalendar.calendar.CalendarDay
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import java.util.*

class CalendarBottomSheetContract {

    interface View : MvpView {
        @OneExecution
        fun setCalendarData(minDate: CalendarDay?, maxDate: CalendarDay?)

        @OneExecution
        fun setCurrentDate(date: CalendarDay?)

        @OneExecution
        fun setEventDates(dates: List<CalendarDay>)

        @OneExecution
        fun setDateSelected(cal: Calendar)

    }

    interface Presenter : BaseContract.Presenter {
        fun onDateSelected(date: CalendarDay)
    }

}