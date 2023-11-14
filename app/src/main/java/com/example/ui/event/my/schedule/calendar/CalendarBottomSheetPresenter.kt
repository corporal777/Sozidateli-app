package com.example.ui.event.my.schedule.calendar

import com.example.data.AppData
import com.example.data.models.EventScheduleDay
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.ui.views.calendarView.CalendarDay
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import java.util.*
import javax.inject.Inject

@InjectViewState
class CalendarBottomSheetPresenter @Inject constructor(
    private val appData: AppData
) : MvpPresenter<CalendarBottomSheetContract.View>(), CalendarBottomSheetContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    var eventDays: List<EventScheduleDay> = emptyList()
    var selectedDay: EventScheduleDay? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        var currentDate: CalendarDay? = null
        var minDate: CalendarDay? = null
        var maxDate: CalendarDay? = null
        compositeDisposable += Maybe.fromCallable {
            eventDays.map {
                val cal = defaultServerDateFormatter.parse(it.date).time.calendar()
                setEventCalendarDays(cal)
            }
        }
            .doOnSuccess {
                if (selectedDay != null) {
                    val calendar = selectedDay!!.millis.calendar()
                    currentDate = CalendarDay(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                }

            }
            .doOnSuccess {
                if (!it.isNullOrEmpty()) {
                    minDate = CalendarDay(it.first().year, it.first().month, 1)
                    maxDate = CalendarDay(
                        it.last().year,
                        it.last().month,
                        it.last().date.month.length(it.last().date.isLeapYear)
                    )
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                viewState.apply {
                    setEventDates(it)
                    setCurrentDate(currentDate)
                    setCalendarData(minDate, maxDate)
                }
            }
    }

    override fun onDateSelected(date: CalendarDay) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, date.year)
        cal.set(Calendar.MONTH, date.month - 1)
        cal.set(Calendar.DAY_OF_MONTH, date.day)
        viewState.setDateSelected(cal)
    }

    private fun setEventCalendarDays(cal: Calendar): CalendarDay {
        return CalendarDay(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}