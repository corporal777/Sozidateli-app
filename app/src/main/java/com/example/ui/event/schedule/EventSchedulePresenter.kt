package com.example.ui.event.schedule

import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
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
        private val userEventData: UserEventData,
        private val appData: AppData
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
                val date = defaultServerDateTimeFormatter.parse(event.holdingDate?.from)
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

    private fun filterTags(event: /*SubEvent*/EventActivityModel, selectedTags: List<Tag>): Boolean {
        if (!mustFilterTags() || selectedTags.isEmpty()) return true
        return selectedTags.any { tag ->
            event.tag?.any { eventTag -> eventTag.toString() == tag.id } ?: false
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

    protected open fun processChangeEventInCalendarStatusRequest(subEvent: /*SubEvent*/EventActivityModel, request: Completable) {
        compositeDisposable += request
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.updateSubevent(subEvent) }
    }

    override fun onSubEventClick(subEvent: /*SubEvent*/EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(userEvent.eventId, subEvent.id.toString())
        }
    }

    override fun onAddToScheduleClick(subEvent: /*SubEvent*/EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
                subEvent,
                eventRepository.addEventToCalendarWithResult(EventCalendarBody(appData.getId(),
                        EventCalendarBodyEntity(EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id?: 0)))
                        .flatMapCompletable { subEv -> Completable.fromAction { subEvent.binds?.userCalendar = subEv } }
        )
    }

    override fun onRemoveFromScheduleClick(subEvent: /*SubEvent*/EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
                subEvent,
                eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                        .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    override fun onShowAllTagsClick() {
        viewState.showAllTags()
    }

    override fun onDestroy() {
        super.onDestroy()
        userEventData.removeOnDataUpdateListener(this)
    }

    abstract fun filterSubEvent(subEvent: /*SubEvent*/EventActivityModel): Boolean
    abstract fun mustFilterTags(): Boolean
}
