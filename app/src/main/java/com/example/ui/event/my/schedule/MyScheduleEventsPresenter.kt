package com.example.ui.event.my.schedule

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.my.schedule.items.EventScheduleData
import com.example.ui.views.calendarView.CalendarDay
import com.example.util.getDaysFromDateToDate
import com.example.util.getMonthName
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject


@InjectViewState
class MyScheduleEventsPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData
) : BasePresenter<MyScheduleEventsContract.View>(appData), MyScheduleEventsContract.Presenter {

    private var mFirstDate = ""
    private var mLastDate = ""
    private val mSubEventsList = arrayListOf<EventActivityModel>()
    private val eventsList = arrayListOf<EventNew>()
    private var mSearchText = ""
    private var isFirstLaunch = true
    private var canScrollContent = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsList()
    }

    override fun attachView(view: MyScheduleEventsContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else {
            viewState.getResultForUpdate()
        }
    }

    fun getEventsList() {
        compositeDisposable += eventRepository.getUserCalendarEvents()
            .doOnSuccess {
                if (!it.isNullOrEmpty()) {
                    eventsList.clear()
                    eventsList.addAll(it)
                }
            }
            .flatMap { events ->
                val subEventsList = arrayListOf<EventActivityModel>()
                events.forEach { subEvent ->
                    subEvent.binds?.activity
                        ?.filter { x -> x.binds?.userCalendar != null }
                        ?.sortedBy { x -> x.holdingDate?.from }
                        ?.forEach { x ->
                            subEventsList.add(x)
                        }
                }
                Maybe.just(subEventsList.sortedBy { x -> x.holdingDate?.from })
            }
            .performOnBackgroundOutOnMain()
            .let {
                if (isFirstLaunch) {
                    it.withProgressBarLoadingDialog(viewState)
                } else it
            }
            .subscribeSimple {
                if (eventsList.isNullOrEmpty()) {
                    viewState.showEmptyListPlaceholder()
                } else {
                    initCalendarDays(it)
                }
            }
    }

    private fun initCalendarDays(list: List<EventActivityModel>) {
        var month = ""
        compositeDisposable += Maybe.fromCallable {
            val nearestDate = findNearestDay(list, System.currentTimeMillis())
            month = getMonthName(nearestDate.millis.calendar())
            Pair(getCalendarDays(list), nearestDate)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { pair ->
                viewState.apply {
                    setMonthCalendar(list, month)
                    setHeaderCalendar(pair.first)
                    scrollToDay(pair.second)
                    initMainDataContent(pair.second)
                }
            }
    }


    private fun initMainDataContent(nearDay: EventScheduleCalendarDay) {
        compositeDisposable += Maybe.fromCallable {
            transformDataToShow(eventsList, mSearchText)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { list ->
                viewState.apply {
                    setContent(list)
                    if (canScrollContent) {
                        scrollContent(nearDay)
                        canScrollContent = false
                    }
                    viewState.hideLoadingAlertDialog()
                }
            }

    }


    override fun onDaySelected(day: EventScheduleCalendarDay) {
        viewState.apply {
            scrollContent(day)
            selectDay(day)
        }
    }

    override fun onAddSubEventToScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.addEventToCalendarWithResult(
                EventCalendarBody(
                    appData.getId(),
                    EventCalendarBodyEntity(
                        EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id
                            ?: 0
                    )
                )
            )
                .flatMapCompletable { subEv ->
                    Completable.fromAction {
                        subEvent.binds?.userCalendar = subEv
                    }
                }
        )
    }

    override fun onRemoveSubEventFromScheduleClick(subEvent: EventActivityModel) {
        viewState.showLoadingAlertDialog()
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    override fun onSearchTextChange(text: String) {
        mSearchText = text
        updateData(text)
    }

    override fun onSearchTextSubmit(text: String) {
        mSearchText = text
        updateData(text)
    }

    private fun updateData(text: String) {
        compositeDisposable += Flowable.fromCallable {
            transformDataToShow(eventsList, text)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { events ->
                if (events.isNullOrEmpty()) {
                    viewState.showEmptyListPlaceholder()
                    viewState.setHeaderCalendar(emptyList())
                } else {
                    viewState.setContent(events)
                    var month = ""
                    Maybe.fromCallable {
                        val list = arrayListOf<EventActivityModel>()
                        events.forEach { it.subEvents.forEach { map -> list.addAll(map.value) } }
                        month =
                            getMonthName(defaultServerDateFormatter.parse(list.first().holdingDate?.from).time.calendar())
                        Pair(getCalendarDays(list), list)
                    }
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple {
                            viewState.setMonthCalendar(it.second, month)
                            viewState.setHeaderCalendar(it.first)
                        }
                }

            }
    }

    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    catchEventError(it)
                },
                onComplete = {
                    if (subEvent.binds?.userCalendar == null) {
                        getEventsList()
                    }
                })
    }

    private fun transformDataToShow(
        eventsList: List<EventNew?>,
        text: String
    ): List<EventScheduleData> {
        val listScheduleEvents = arrayListOf<EventScheduleData>()

        eventsList.forEach { event ->
            val firstDate = event?.binds?.activity
                ?.filter { x -> x.binds?.userCalendar != null }
                ?.sortedBy { x -> x.holdingDate?.from }
                ?.firstOrNull()?.holdingDate?.from?.split(" ")?.get(0) ?: ""

            val list = event?.binds?.activity
                ?.filter { x -> x.binds?.userCalendar != null }
            val eventsMap =
                if (text.isNullOrEmpty()) {
                    list?.groupBy { x -> x.holdingDate?.from?.split(" ")?.get(0) ?: "" }
                        ?.toSortedMap()
                } else {
                    list?.filter { x -> isHasSearchText(text, x) }
                        ?.groupBy { x -> x.holdingDate?.from?.split(" ")?.get(0) ?: "" }
                        ?.toSortedMap()
                }

            if (!eventsMap.isNullOrEmpty()) {
                eventsMap.put("", eventsMap.remove(firstDate)!!)
                listScheduleEvents.add(EventScheduleData(event, firstDate, eventsMap))

            }
        }
        return listScheduleEvents.sortedBy { x -> x.firstDate }
    }

    private fun findNearestDay(
        list: List<EventActivityModel>,
        today: Long
    ): EventScheduleCalendarDay {
        var eventDate = list
            .find { event ->
                val date = defaultServerDateFormatter.parse(event.holdingDate?.from).time
                date == today || date - today > 0
            }?.holdingDate?.from?.split(" ")?.get(0)

        if (eventDate.isNullOrEmpty()) {
            eventDate = list.firstOrNull()?.holdingDate?.from?.split(" ")?.get(0)
        }
        return createCalendarDay(defaultServerDateFormatter.parse(eventDate).time.calendar().timeInMillis)
    }

    fun createCalendarDay(date: Long): EventScheduleCalendarDay {
        val cal = date.calendar()
        return EventScheduleCalendarDay(
            date,
            cal.get(Calendar.WEEK_OF_MONTH),
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ?: "",
            cal.get(Calendar.DAY_OF_MONTH),
            true
        )
    }


    private fun isHasSearchText(text: String, event: EventActivityModel): Boolean {
        var isHas = false
        if (event.description?.contains(text, true) == true
            || event.title?.contains(text, true) == true
        ) {
            isHas = true
        } else {
            if (event.binds?.users?.any { x -> x.fullName.contains(text, true) } == true) {
                isHas = true
            }
        }
        return isHas
    }

    override fun onShowEventClick(eventId: String) {
        viewState.showAboutEvent(eventId)
    }

    override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
    }

    private fun setEventCalendarDays(cal: Calendar): CalendarDay {
        return CalendarDay(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun getCalendarDays(list: List<EventActivityModel>): ArrayList<List<EventScheduleCalendarDay>> {
        val firstDate = list.firstOrNull()?.holdingDate?.from ?: ""
        val lastDate = list.lastOrNull()?.holdingDate?.from ?: ""

        val dates = userEventData.createCalendarDaysForSchedule(
            getDaysFromDateToDate(
                firstDate,
                lastDate
            ).map { defaultServerDateFormatter.parse(it).time },
            list
        )
        return collectDatesToWeeks(dates)
    }

    private fun collectDatesToWeeks(dates: List<EventScheduleCalendarDay>): ArrayList<List<EventScheduleCalendarDay>> {
        var countSize = 0
        val listDays = arrayListOf<EventScheduleCalendarDay>()
        val days = arrayListOf<List<EventScheduleCalendarDay>>()
        dates.forEach {
            listDays.add(it)
            countSize++
            if (listDays.size == 7) {
                val list = arrayListOf<EventScheduleCalendarDay>()
                list.addAll(listDays)
                days.add(list)
                listDays.clear()
            } else {
                if (countSize == dates.size) {
                    val list = arrayListOf<EventScheduleCalendarDay>()
                    list.addAll(listDays)
                    days.add(list)
                }
            }
        }
        return days
    }

    private fun catchEventError(t: Throwable) {
        if (t is HttpException) {
            try {
                val error = Gson().fromJson(
                    t.response()?.errorBody()?.string(),
                    NewErrors::class.java
                )
                when (error.errors[0].message) {
                    "The event has been banned" -> {
                        val eventName = error.errors[0].additionalData?.name
                        val eventId = error.errors[0].additionalData?.id.toString()
                        val message =
                            "Мероприятие «$eventName» заблокировано."
                        viewState.showErrorMessage(eventId, message)
                    }
                    "The event has been cancelled" -> {
                        val eventName = error.errors[0].additionalData?.name
                        val eventId = error.errors[0].additionalData?.id.toString()
                        val message =
                            "Мероприятие «$eventName» было отменено организатором."
                        viewState.showErrorMessage(eventId, message)
                    }
                    else -> {
                        onReceiveError(t)
                    }
                }
            } catch (e: Exception) {

            }
            when (t.code()) {
                403 -> {

                }
            }
        }
    }

}