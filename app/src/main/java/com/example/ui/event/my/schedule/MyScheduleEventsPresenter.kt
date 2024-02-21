package com.example.ui.event.my.schedule

import android.util.Log
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleData
import com.example.data.models.EventScheduleDay
import com.example.extensions.calendar
import com.example.extensions.getMonthName
import com.example.extensions.isSameDay
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.activities.SubEventsData
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withDelay
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class MyScheduleEventsPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData
) : BasePresenter<MyScheduleEventsContract.View>(appData), MyScheduleEventsContract.Presenter {

    private val timerCompositeDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }

    private var eventsList = listOf<EventNew>()
    private var isFirstLaunch = true

    private var eventDates = listOf<List<EventScheduleDay>>()
    private var currentDay: EventScheduleDay? = null
    private var currentMonth = ""
    private var searchText = ""

    var isJumping = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsList()
    }

    override fun attachView(view: MyScheduleEventsContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else viewState.getResultForUpdate()
    }

    fun getEventsList() {
        compositeDisposable += eventRepository.getUserCalendarEvents()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.showEmptyListPlaceholder() },
                onSuccess = {
                    eventsList = it ?: emptyList()
                    if (eventsList.isEmpty()) viewState.showEmptyListPlaceholder()
                    else initContent()
                }
            )
    }

    private fun initContent() {
        compositeDisposable += Maybe.defer {
            val groupedList = groupData(eventsList)
            val dates = groupedList.map { x -> x.eventDates }.flatten()
            eventDates = userEventData.collectDatesToWeeks(dates)
            currentDay = userEventData.findNearestDay(dates)
            currentMonth = getMonthName(currentDay?.millis?.calendar())
            Maybe.just(groupedList)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    if (it.isNullOrEmpty()) viewState.showEmptyListPlaceholder()
                    else viewState.apply {
                        setCalendar(eventDates, currentMonth)
                        setContent(it)
                        if (currentDay != null) {
                            scrollPageContent(currentDay!!, true)
                            scrollListContent(currentDay!!)
                        }
                        hideLoadingAlertDialog()
                    }
                })

    }


    override fun onDaySelected(day: EventScheduleDay) {
        currentDay = day
        viewState.apply {
            selectDay(day)
            scrollListContent(day)
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
        if (searchText == text) return
        searchText = text
        initContent()
    }


    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { catchEventError(it) },
                onComplete = { if (subEvent.binds?.userCalendar == null) getEventsList() }
            )
    }


    private fun groupData(eventsList: List<EventNew?>): List<EventScheduleData> {
        return arrayListOf<EventScheduleData>().apply {
            eventsList.forEach {
                val groupedList = groupSubEvents(it)
                if (groupedList.isNotEmpty()) {
                    add(
                        EventScheduleData(
                            it,
                            groupedList.first().titleDate,
                            groupedList.mapNotNull { x -> x.titleDate },
                            groupedList.apply { first().titleDate = null },
                        )
                    )
                }
            }
        }.sortedBy { x -> x.titleDate?.date }
    }

    private fun groupSubEvents(eventNew: EventNew?): List<SubEventsData> {
        return arrayListOf<SubEventsData>().apply {
            var titleDate: String? = ""
            eventNew?.binds?.activity
                ?.filter { x -> x.binds?.userCalendar != null }
                .let {
                    if (searchText.isNullOrEmpty()) it
                    else it?.filter { x -> userEventData.isHasEventSearchText(searchText, x) }
                }
                ?.sortedBy { x -> x.holdingDate?.from }
                ?.forEach { subEvent ->
                    val subEventDate = subEvent.holdingDate?.getShortDate()
                    titleDate = if (titleDate == subEventDate) null else subEventDate
                    add(SubEventsData(userEventData.createEventScheduleDay(titleDate), subEvent))
                    titleDate = subEventDate
                }
        }
    }

    override fun onShowEventClick(eventId: String) = viewState.showAboutEvent(eventId)
    override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) =
        viewState.showSubEvent(eventId, subEvent.id.toString())


    fun findPositionFromList(day: EventScheduleDay?): Int? {
        val position = eventDates.indexOf(eventDates.find { x -> x.contains(day) })
        return if (position == -1) null
        else position
    }

    fun getCurrentDay() = currentDay
    fun setCurrentDay(day: EventScheduleDay?) {
        currentDay = day
    }

    fun startJumpingTimer() {
        isJumping = true
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1, TimeUnit.SECONDS)
            .subscribeSimple {
                if (it >= 2) {
                    isJumping = false
                    timerCompositeDisposable.clear()
                }
            }
    }

    private fun catchEventError(t: Throwable) {
        t.printStackTrace()
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
        }
    }

}