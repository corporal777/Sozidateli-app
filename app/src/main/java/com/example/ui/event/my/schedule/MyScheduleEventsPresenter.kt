package com.example.ui.event.my.schedule

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleDay
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.activities.SubEventsData
import com.example.data.models.EventScheduleData
import com.example.util.getMonthName
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
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

    private val timerCompositeDisposable = CompositeDisposable()


    private var eventsList = listOf<EventNew>()
    private var isFirstLaunch = true
    private var canScrollContent = true

    private var eventDates = listOf<List<EventScheduleDay>>()
    var currentDay: EventScheduleDay? = null
    private var currentMonth = ""
    private var searchText = ""

    var isJumping = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable

        viewState.setContentPlaceholder()
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
            .subscribeSimple {
                eventsList = it ?: emptyList()
                if (eventsList.isNullOrEmpty()) viewState.showEmptyListPlaceholder()
                else initContent()
            }
    }

    private fun initContent() {
        compositeDisposable += Maybe.defer {
            val groupedList = groupData(eventsList)
            val dates = groupedList.map { x -> x.eventDates }.flatten()
            eventDates = userEventData.collectDatesToWeeks(userEventData.createEventScheduleDays(dates))
            currentDay = findNearestDay()
            currentMonth = getMonthName(currentDay?.millis?.calendar())
            Maybe.just(groupedList)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    if (it.isNullOrEmpty()) viewState.showEmptyListPlaceholder()
                    else {
                        viewState.setHeaderCalendar(eventDates)
                        viewState.setMonthCalendar(eventDates.flatten(), currentMonth)
                        viewState.setContent(it)

                        viewState.apply {
                            if (canScrollContent) {
                                if (currentDay != null){
                                    scrollToDay(currentDay!!)
                                    scrollContent(currentDay!!)
                                }
                                canScrollContent = false
                            }
                        }
                        viewState.hideLoadingAlertDialog()
                    }
                })

    }


    override fun onDaySelected(day: EventScheduleDay) {
        currentDay = day
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
        searchText = text
        initContent()
    }

    override fun onSearchTextSubmit(text: String) {
        viewState.hideKeyboard()
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
        val eventScheduleList = arrayListOf<EventScheduleData>()
        eventsList.forEach {
            val groupedList = groupSubEvents(it)
            if (!groupedList.isNullOrEmpty()){
                eventScheduleList.add(
                    EventScheduleData(
                        it,
                        groupedList.first().titleDate,
                        groupedList.mapNotNull { x -> x.titleDate },
                        groupedList.apply { first().titleDate = "" },
                    )
                )
            }
        }
        return eventScheduleList.sortedBy { x -> x.titleDate }
    }

    private fun groupSubEvents(eventNew: EventNew?): List<SubEventsData> {
        val list = arrayListOf<SubEventsData>()
        var titleDate: String? = ""
        eventNew?.binds?.activity
            ?.filter { x -> x.binds?.userCalendar != null }
            .let {
                if (searchText.isNullOrEmpty()) it
                else it?.filter { x -> isHasSearchText(searchText, x) }
            }
            ?.sortedBy { x -> x.holdingDate?.from }
            ?.forEach { subEvent ->
                val subEventDate = subEvent.holdingDate?.from?.split(" ")?.get(0) ?: ""
                if (titleDate == subEventDate) titleDate = null
                else titleDate = subEventDate

                list.add(SubEventsData(titleDate, subEvent, emptyList()))
                titleDate = subEventDate
            }

        return list
    }

    private fun findNearestDay(): EventScheduleDay? {
        val date = System.currentTimeMillis()
        val days = eventDates.flatten()
        val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
        val other = Calendar.getInstance()
        return days.find {
            (dateCalendar.isSameDay(other.apply {
                timeInMillis = it.millis
            }) || it.millis - date > 0)
        } ?: days.lastOrNull()
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

    override fun onShowEventClick(eventId: String) = viewState.showAboutEvent(eventId)
    override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) = viewState.showSubEvent(eventId, subEvent.id.toString())
    fun createCalendarDay(date: String?): EventScheduleDay? = userEventData.createEventScheduleDay(date)

    fun startJumpingTimer(){
        isJumping = true
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1, TimeUnit.SECONDS)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    timerCompositeDisposable.clear()
                },
                onNext = {
                    if (it >= 2){
                        isJumping = false
                        timerCompositeDisposable.clear()
                    }
                })
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
            when (t.code()) {
                403 -> {

                }
            }
        }
    }

}