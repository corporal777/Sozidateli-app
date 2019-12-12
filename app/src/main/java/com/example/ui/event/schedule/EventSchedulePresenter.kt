package com.example.ui.event.schedule

import com.example.data.UserEventData
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.SubEvent
import com.example.data.models.Tag
import com.example.extensions.*
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*

abstract class EventSchedulePresenter
constructor(
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData
) : BasePresenter<EventScheduleContract.View>(), EventScheduleContract.Presenter, UserEventData.OnDataUpdateListener {

    private val userEvent = userEventData.userEvent!!

    protected var currentDay: EventScheduleCalendarDay? = null
    protected var tags: List<Tag> = emptyList()

    private var firstAttach = true

    private val onLoadingComplete: () -> Unit = {
        viewState.apply {
            if (userEventData.isDataFromLocalStorage) showDataFormCacheMessage(userEventData.dataLoadingDate.let { defaultDateFormatter.format(it) })
            else hideDataFormCacheMessage()

            tags = userEvent.activity.groups.plus(userEvent.activity.tags)
            setDays(userEventData.days)
            setTags(tags)
            currentDay?.let { day ->
                selectDay(day)
                scrollToDay(day)
            }
        }

        invalidateDay()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        userEventData.addOnDataUpdateListener(this)
        compositeDisposable += findNearestDayFromEventDays(System.currentTimeMillis())
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onError = {
                            viewState.apply { showEmptyEventPlaceholder() }
                            onReceiveError(it)
                        },
                        onComplete = onLoadingComplete)
    }

    override fun onDataUpdated() {
        compositeDisposable += Completable.complete()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(onComplete = onLoadingComplete)
    }

    override fun attachView(view: EventScheduleContract.View?) {
        super.attachView(view)
        if (firstAttach) firstAttach = false
        else invalidateDay()
    }

    private fun findNearestDayFromEventDays(date: Long): Completable {
        return Completable.fromAction {
            val days = userEventData.days ?: emptyList()
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                it.hasEvents &&
                        (dateCalendar.isSameDay(other.apply { timeInMillis = it.millis }) || it.millis - date > 0)
            } ?: days.lastOrNull()
        }
    }

    private fun invalidateDay() {
        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }
        val subEvents = userEvent.activity.activities.let {
            it.filter { event ->
                val date = defaultServerDateTimeFormatter.parse(event.start)
                filterSubEvent(event)
                        && filterTags(event, selectedTags)
                        && date.time > day.millis.startOfDay() && date.time < day.millis.endOfDay()
            }
        }

        viewState.apply {
            setSubEvents(subEvents, if (mustFilterTags()) selectedTags else emptyList())
            if (subEvents.isEmpty()) {
                showEmptyDayPlaceholder()
                hideCurrentDay()
            } else {
                currentDay?.let { day -> showCurrentDay(day) }
                hidePlaceholder()
            }
        }
    }

    private fun filterTags(event: SubEvent, selectedTags: List<Tag>): Boolean {
        if (!mustFilterTags() || selectedTags.isEmpty()) return true
        return selectedTags.any { tag ->
            event.tags?.any { eventTag -> eventTag.id == tag.id } ?: false
        }
                || selectedTags.any { tag ->
            event.groups?.any { eventTag -> eventTag.id == tag.id } ?: false
        }
    }

    private fun daySubEventsError() {
        viewState.apply {
            hideCurrentDay()
            showEmptyEventPlaceholder()
        }
    }

    override fun onDaySelected(day: EventScheduleCalendarDay) {
        currentDay = day
        viewState.apply { selectDay(day) }
        invalidateDay()
    }

    override fun onTagSelectedListChange() {
        invalidateDay()
    }

    override fun onDayChanged(date: Long) {
        val currentDayDate = currentDay?.millis ?: return
        if (date.calendar().isSameDay(currentDayDate.calendar())) invalidateDay()
    }

    protected open fun processChangeEventInCalendarStatusRequest(request: Completable) {
        compositeDisposable += request
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { invalidateDay() }

    }

    override fun onSubEventClick(subEvent: SubEvent) {
        checkInternetAndRun {
            viewState.showSubEvent(userEvent.eventId, subEvent.id)
        }
    }

    override fun onAddToScheduleClick(subEvent: SubEvent) {
        processChangeEventInCalendarStatusRequest(
                eventRepository.addEventToCalendar(userEvent.eventId, subEvent.id)
                        .andThen(Completable.fromAction { subEvent.isInCalendar = true })
        )
    }

    override fun onRemoveFromScheduleClick(subEvent: SubEvent) {
        processChangeEventInCalendarStatusRequest(
                eventRepository.removeEventFromCalendar(userEvent.eventId, subEvent.id)
                        .andThen(Completable.fromAction { subEvent.isInCalendar = false })
        )
    }

    override fun onShowAllTagsClick() {
        viewState.showAllTags()
    }

    override fun onDestroy() {
        super.onDestroy()
        userEventData.removeOnDataUpdateListener(this)
    }

    abstract fun filterSubEvent(subEvent: SubEvent): Boolean
    abstract fun mustFilterTags(): Boolean
}
