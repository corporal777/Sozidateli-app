package com.example.data

import com.example.data.database.UserEventDao
import com.example.data.models.*
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.repository.EventRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.CompletableSubject
import java.util.*

class UserEventData(
        private val eventRepository: EventRepository,
        private val userEventDao: UserEventDao
) {

    var days: List<EventScheduleCalendarDay>? = null
        private set
    var userEvent: UserEvent? = null
        private set

    var isDataFromLocalStorage = false
    var dataLoadingDate = 0L

    private val compositeDisposable = CompositeDisposable()
    private val onDataUpdateListeners: MutableList<OnDataUpdateListener> = mutableListOf()

    fun load(eventId: String): Completable {
        compositeDisposable.clear()
        return CompletableSubject.create().apply {
            compositeDisposable += loadInternal(eventId)
                    .doOnComplete { performOnDataUpdate() }
                    .subscribe({
                        onComplete()
                    }, {
                        onError(it)
                    })
        }
    }

    private fun loadInternal(eventId: String): Completable {
        val event = eventRepository.getEventDetails(eventId)
        val formats = eventRepository.getEventFormatsList(mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0))
        return Maybe.zip(event, formats, BiFunction<EventNew, List<NewEventFormat>, UserEvent> { event, formats ->
            event.format?.name = formats.firstOrNull { f -> f.id == event.format?.value }?.name
            UserEvent(event.id.toString(),
                    EventInfo(event, event.binds?.partner?: arrayListOf(), event.binds?.page?: arrayListOf(),
                            event.binds?.userRegister, event.state?.rating?.askDelay, event.binds?.form),
                    EventActivity(event.binds?.activity?: arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf()), System.currentTimeMillis())
        })
                .doOnSuccess { userEventDao.insert(it) }
                .onErrorResumeNext(loadEventCache(eventId).toMaybe())
                .doOnSuccess {
                    val dateFormat = defaultServerDateFormatter
                    days = createCalendarDays(it.activity.dates.map { dateFormat.parse(it.date).time })
                    userEvent = it
                    isDataFromLocalStorage = it.isDataFromLocalStorage
                    dataLoadingDate = it.updatedAt
                }
                .ignoreElement()
        /*val eventInfo = eventRepository.getEventInfo(eventId)
        val eventActivity = eventRepository.getEventActivity(eventId)
        return Maybe.zip(eventInfo, eventActivity, BiFunction<EventInfo, EventActivity, UserEvent> { info, activity ->
            UserEvent(eventId, info, activity, System.currentTimeMillis())
        })
                .doOnSuccess { userEventDao.insert(it) }
                .onErrorResumeNext(loadEventCache(eventId).toMaybe())
                .doOnSuccess {
                    val dateFormat = defaultServerDateFormatter
                    days = createCalendarDays(it.activity.dates.map { dateFormat.parse(it.date).time })
                    userEvent = it
                    isDataFromLocalStorage = it.isDataFromLocalStorage
                    dataLoadingDate = it.updatedAt
                }
                .ignoreElement()*/
    }

    private fun loadEventCache(event: String): Single<UserEvent> {
        return userEventDao.getById(event)
                .doOnSuccess { it.isDataFromLocalStorage = true }
    }

    private fun createCalendarDays(dates: List<Long>): List<EventScheduleCalendarDay> {
        if (dates.isEmpty()) return emptyList()
        val sortedDates = dates.sorted()
//        val firsDate = sortedDates.first().calendar()
//        val lastDate = sortedDates.last().calendar()
//        val inDatesCalendar = Calendar.getInstance()
//
//        val datesInRange = mutableListOf<EventScheduleCalendarDay>()
//        while (firsDate.before(lastDate) || firsDate.isSameDay(lastDate)) {
//            val dateInDates = sortedDates.find { firsDate.isSameDay(inDatesCalendar.apply { timeInMillis = it }) }
//
//            val eventDay = EventScheduleCalendarDay(
//                    firsDate.timeInMillis,
//                    firsDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
//                            ?: "",
//                    firsDate.get(Calendar.DAY_OF_MONTH),
//                    dateInDates != null
//            )
//
//            datesInRange.add(eventDay)
//            firsDate.add(Calendar.DATE, 1)
//        }
//
//        return datesInRange

        return sortedDates.map {
            val cal = it.calendar()
            EventScheduleCalendarDay(
                    it,
                    cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                            ?: "",
                    cal.get(Calendar.DAY_OF_MONTH),
                    true
            )
        }
    }

    fun clear() {
        days = null
        userEvent = null
        isDataFromLocalStorage = false
        dataLoadingDate = 0L
        onDataUpdateListeners.clear()
    }

    private fun performOnDataUpdate() {
        onDataUpdateListeners.forEach { it.onDataUpdated() }
    }

    fun addOnDataUpdateListener(onDataUpdateListener: OnDataUpdateListener) {
        if (!onDataUpdateListeners.contains(onDataUpdateListener))
            onDataUpdateListeners.add(onDataUpdateListener)
    }

    fun removeOnDataUpdateListener(onDataUpdateListener: OnDataUpdateListener) {
        onDataUpdateListeners.remove(onDataUpdateListener)
    }

    interface OnDataUpdateListener {
        fun onDataUpdated()
    }
}