package com.example.ui.event.my.schedule.calendar

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.views.calendarView.CalendarDay
import java.util.*

class CalendarBottomSheetContract {

    interface View : MvpView {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCalendarData(minDate: CalendarDay?, maxDate: CalendarDay?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCurrentDate(date: CalendarDay?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEventDates(dates: List<CalendarDay>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setDateSelected(cal : Calendar)

    }

    interface Presenter : BaseContract.Presenter {
        fun onDateSelected(date: CalendarDay)
    }

}