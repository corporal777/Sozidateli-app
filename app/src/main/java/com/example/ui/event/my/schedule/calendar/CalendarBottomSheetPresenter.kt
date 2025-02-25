package com.example.ui.event.my.schedule.calendar

import com.example.data.AppData
import com.example.data.models.EventScheduleDay
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.pagercalendar.calendar.CalendarDay
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
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
        compositeDisposable += Maybe.defer { Maybe.just(getCalendarDaysFromList()) }
            .doOnSuccess {
                currentDate = CalendarDay.createCalendarDay(selectedDay?.millis?.calendar())
                minDate = it.first().minDate
                maxDate = it.last().maxDate
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
        val foundDate = eventDays.find { x -> x.date == date.dateString }
        viewState.setDateSelected(foundDate)
    }

    private fun getCalendarDaysFromList(): List<CalendarDay> {
        return eventDays.map {
            val cal = defaultServerDateFormatter.parse(it.date)?.time?.calendar()
            if (cal == null) return emptyList()
            else CalendarDay.createCalendarDay(cal)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}