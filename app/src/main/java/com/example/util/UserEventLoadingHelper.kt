package com.example.util

import com.example.data.UserEventData
import com.example.data.database.UserEventDao
import com.example.data.models.Event
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.UserEvent
import com.example.extensions.calendar
import com.example.extensions.isSameDay
import com.example.repository.EventRepository
import io.reactivex.Maybe
import java.util.*

class UserEventLoadingHelper(
        private val userEventData: UserEventData,
        private val eventRepository: EventRepository,
        private val userEventDao: UserEventDao
) {
    fun load(event: Event): Maybe<Boolean> {
        return Maybe.just(false)
//        return Maybe.zip(eventRepository.getEventInfo(event.id), eventRepository.getEventActivity(event.id), BiFunction<EventInfo, List<SubEvent>, UserEvent> { info, subEvents ->
//            UserEvent(event.id, info, subEvents, System.currentTimeMillis())
//        })
//                .doOnSuccess { userEventDao.insert(it) }
//                .onErrorResumeNext(loadEventCache(event.id))
//                .doOnSuccess {
//                    val dateFormat = defaultServerDateFormatter
//                    userEventData.apply {
//                        days = createCalendarDays(it.eventInfo.dates.map { dateFormat.parse(it.date).time })
//                        tags = it.eventInfo.tags
//                        categories = it.eventInfo.categories
//                        partners = it.eventInfo.partners
//                        this.subEvents = it.subEvents
//
//                        isStaticDataLoaded = true
//                        isDataFromLocalStorage = it.isDataFromLocalStorage
//                        dataLoadingDate = it.updatedAt
//                    }
//                }
//                .map { true }
//                .onErrorReturn { false }
    }

    private fun loadEventCache(event: String): Maybe<UserEvent> {
        return userEventDao.getById(event)
                .doOnSuccess { it.isDataFromLocalStorage = true }
    }

    private fun createCalendarDays(dates: List<Long>): List<EventScheduleCalendarDay> {
        if (dates.isEmpty()) return emptyList()
        val sortedDates = dates.sorted()
        val firsDate = sortedDates.first().calendar()
        val lastDate = sortedDates.last().calendar()
        val inDatesCalendar = Calendar.getInstance()

        val datesInRange = mutableListOf<EventScheduleCalendarDay>()
        while (firsDate.before(lastDate) || firsDate.isSameDay(lastDate)) {
            val dateInDates = sortedDates.find { firsDate.isSameDay(inDatesCalendar.apply { timeInMillis = it }) }

            val eventDay = EventScheduleCalendarDay(
                    firsDate.timeInMillis,
                    firsDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                            ?: "",
                    firsDate.get(Calendar.DAY_OF_MONTH),
                    dateInDates != null
            )

            datesInRange.add(eventDay)
            firsDate.add(Calendar.DATE, 1)
        }

        return datesInRange
    }
}