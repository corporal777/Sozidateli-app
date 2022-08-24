package com.example.ui.event.my.schedule

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.Event
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.di.Connectivity
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.my.schedule.items.MyScheduleEventsData
import com.example.ui.event.my.schedule.items.MyScheduleSubEventsGroup
import com.example.ui.views.calendarView.CalendarDay
import com.example.util.getDaysFromDateToDate
import com.example.util.getMonthName
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


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
    private val mEventsList = arrayListOf<EventNew>()
    private var mSearchText = ""
    private var isFirstLaunch = true
    private var canScrollContent = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showProgressBarLoadingDialog()
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
        val mSubEventsDates = arrayListOf<CalendarDay>()
        val req = eventRepository.getUserCalendarEvents()
            .doOnSuccess { list ->
                if (!list.isNullOrEmpty()) {
                    mEventsList.clear()
                    mEventsList.addAll(list)
                    val subEventsList = arrayListOf<EventActivityModel>()
                    list.forEach { subEvent ->
                        subEvent.binds?.activity
                            ?.filter { x -> x.binds?.userCalendar != null }
                            ?.sortedBy { x -> x.holdingDate?.from }
                            ?.forEach { x ->
                                val cal =
                                    defaultServerDateFormatter.parse(x.holdingDate?.from).time.calendar()
                                mSubEventsDates.add(setEventCalendarDays(cal))
                                subEventsList.add(x)
                            }
                    }
                    mSubEventsList.apply {
                        clear()
                        addAll(subEventsList.sortedBy { x -> x.holdingDate?.from })
                        mFirstDate = firstOrNull()?.holdingDate?.from ?: ""
                        mLastDate = lastOrNull()?.holdingDate?.from ?: ""
                    }
                }
            }
        compositeDisposable +=
            if (isFirstLaunch) {
                req.performOnBackgroundOutOnMain()
                    .withProgressBarLoadingDialog(viewState)
            } else {
                req.performOnBackgroundOutOnMain()
            }
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                    }, onSuccess = {
                        if (it.isNullOrEmpty()) {
                            viewState.showEmptyListPlaceholder()
                        } else {
                            initCalendarDays(mSubEventsDates, it)
                        }
                    })


        /*compositeDisposable += eventRepository.getUserCalendarEvents()
            .doOnSuccess { list ->
                if (!list.isNullOrEmpty()) {
                    mEventsList.clear()
                    mEventsList.addAll(list)
                    val subEventsList = arrayListOf<EventActivityModel>()
                    list.forEach { subEvent ->
                        subEvent.binds?.activity
                            ?.filter { x -> x.binds?.userCalendar != null }
                            ?.sortedBy { x -> x.holdingDate?.from }
                            ?.forEach { x ->
                                val cal =
                                    defaultServerDateFormatter.parse(x.holdingDate?.from).time.calendar()
                                mSubEventsDates.add(setEventCalendarDays(cal))
                                subEventsList.add(x)
                            }
                    }
                    mSubEventsList.apply {
                        clear()
                        addAll(subEventsList.sortedBy { x -> x.holdingDate?.from })
                        mFirstDate = firstOrNull()?.holdingDate?.from ?: ""
                        mLastDate = lastOrNull()?.holdingDate?.from ?: ""
                    }
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onSuccess = {
                    if (it.isNullOrEmpty()) {
                        viewState.showEmptyListPlaceholder()
                    } else {
                        initCalendarDays(mSubEventsDates, it)
                    }
                })

         */

    }

    private fun initBottomSheetCalendarData(
        nearDay: EventScheduleCalendarDay,
        dates: List<CalendarDay>
    ) {
        var firstCalDate: CalendarDay? = null
        var lastCalDate: CalendarDay? = null
        compositeDisposable += Completable.fromAction {
            val sCal = defaultServerDateFormatter.parse(mFirstDate).time.calendar()
            val eCal = defaultServerDateFormatter.parse(mLastDate).time.calendar()
            firstCalDate = CalendarDay(sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, 1)
            lastCalDate = CalendarDay(
                eCal.get(Calendar.YEAR), eCal.get(Calendar.MONTH) + 1,
                eCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            )
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val month = getMonthName(nearDay.millis.calendar())
                viewState.setMonthCalendar(dates, month, firstCalDate, lastCalDate)
            }
    }

    @SuppressLint("LogNotTimber")
    private fun initCalendarDays(subEventDates: List<CalendarDay>, list: List<EventNew?>) {
        val mDays = arrayListOf<EventScheduleCalendarDay>()
        lateinit var mNearestDate: EventScheduleCalendarDay
        if (mSubEventsList.isNullOrEmpty()) {
            Log.e("ERROR", "No events")
            viewState.showEmptyListPlaceholder()
        } else {
            compositeDisposable += Completable.fromAction {
                mDays.addAll(
                    userEventData.createCalendarDaysForSchedule(
                        getDaysFromDateToDate(
                            mFirstDate,
                            mLastDate
                        ).map { defaultServerDateFormatter.parse(it).time },
                        mSubEventsList
                    )
                )
                mNearestDate = findNearestDay(System.currentTimeMillis())
            }
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                    },
                    onComplete = {
                        viewState.apply {
                            setHeaderCalendar(mDays)
                            scrollToDay(mNearestDate)
                            initBottomSheetCalendarData(mNearestDate, subEventDates)
                            initMainDataContent(mNearestDate, list)
                        }
                    })
        }


    }

    private fun initMainDataContent(nearDay: EventScheduleCalendarDay, list: List<EventNew?>) {
        val listReadyEvents = arrayListOf<MyScheduleEventsData>()
        compositeDisposable += Completable.fromAction {
            listReadyEvents.addAll(transformDataToShow(list, mSearchText))
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setSearchContent()
                    setContent(listReadyEvents)
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
        val listEvents = arrayListOf<MyScheduleEventsData>()
        compositeDisposable += Completable.fromAction {
            listEvents.addAll(transformDataToShow(mEventsList, text))
        }.performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!listEvents.isNullOrEmpty()) {
                    viewState.setContent(listEvents)
                } else viewState.showEmptyListPlaceholder()

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
    ): List<MyScheduleEventsData> {
        val listScheduleEvents = arrayListOf<MyScheduleEventsData>()

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

            var canShowPlaceholder = false
            if (!eventsMap.isNullOrEmpty()) {
                eventsMap.put("", eventsMap.remove(firstDate)!!)
                listScheduleEvents.add(
                    MyScheduleEventsData(
                        firstDate,
                        event?.id.toString(),
                        event?.name ?: "",
                        event?.image?.uri ?: "",
                        eventsMap ?: emptyMap(),
                        false
                    )
                )
                //if (!firstDate.isNullOrEmpty())

            } else {
                canShowPlaceholder = true
            }


        }
        return listScheduleEvents.sortedBy { x -> x.firstDate }
    }

    private fun findNearestDay(today: Long): EventScheduleCalendarDay {
        var eventDate = mSubEventsList
            .find { event ->
                val date = defaultServerDateFormatter.parse(event.holdingDate?.from).time
                date == today || date - today > 0
            }?.holdingDate?.from?.split(" ")?.get(0)

        if (eventDate.isNullOrEmpty()) {
            eventDate = mSubEventsList.lastOrNull()?.holdingDate?.from?.split(" ")?.get(0)
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