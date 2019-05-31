package com.example.ui.event.schedule

import com.example.data.UserEventData
import com.example.data.database.Db
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.SubEvent
import com.example.data.models.Tag
import com.example.extensions.*
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.UserEventLoadingHelper
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import timber.log.Timber
import withLoadingDialog
import java.util.*

abstract class EventSchedulePresenter
constructor(
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData,
        private val db: Db,
        private val connectivity: Observable<Boolean>
) : BasePresenter<EventScheduleContract.View>(), EventScheduleContract.Presenter {

    protected val event = userEventData.event!!

    protected var currentDay: EventScheduleCalendarDay? = null
    protected var selectedTags: List<Tag> = emptyList()

    private val loadingCompleteAction = Action {
        viewState.apply {
            userEventData.apply {
                setDays(days)
                setTags(tags as List<Tag>)
                currentDay?.let { day ->
                    selectDay(day)
                    scrollToDay(day)
                }
            }
        }

        invalidateDay()
    }

    private val loadingErrorConsumer = Consumer<Throwable> {
        it.printStackTrace()
        viewState.apply { showEmptyEventPlaceholder() }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += loadData(userEventData.isDataFromLocalStorage)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe(loadingCompleteAction, loadingErrorConsumer)
    }

    private fun loadData(forceLoading: Boolean = false): Completable {
        return loadEventScheduleStaticData(forceLoading)
                .andThen(findNearestDayFromEventDays(System.currentTimeMillis()))
    }

    private fun loadEventScheduleStaticData(forceLoading: Boolean): Completable {
        return if (forceLoading || userEventData.isStaticDataLoaded) Completable.complete()
        else UserEventLoadingHelper(userEventData, eventRepository, db.userEventDao()).load(event)
                .flatMapCompletable { Completable.complete() }
    }

    private fun findNearestDayFromEventDays(date: Long): Completable {
        return Completable.fromAction {
            val days = userEventData.days ?: emptyList()
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                it.hasEvents &&
                        (dateCalendar.isSameDay(other.apply { timeInMillis = it.millis }) || it.millis - date > 0)
            }
        }
    }

    private fun invalidateDay() {
        val day = currentDay ?: return daySubEventsError()
        val subEvents = userEventData.subEvents?.let {
            it.filter { event ->
                val date = defaultServerDateTimeFormatter.parse(event.start)
                filterSubEvent(event)
                        && (if (selectedTags.isNotEmpty()) event.tags.any { tag -> selectedTags.contains(tag) } else true)
                        && date.time > day.millis.startOfDay() && date.time < day.millis.endOfDay()
            }
        } ?: return daySubEventsError()

        viewState.apply {
            if (userEventData.isDataFromLocalStorage) showDataFormCacheMessage(userEventData.dataLoadingDate.calendar().formatToDefaultTime())
            else hideDataFormCacheMessage()

            setSubEvents(subEvents, selectedTags)
            if (subEvents.isEmpty()) {
                showEmptyDayPlaceholder()
                hideCurrentDay()
            } else {
                currentDay?.let { day -> showCurrentDay(day) }
                hidePlaceholder()
            }
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

    override fun onTagSelectedListChange(tags: List<Tag>) {
        selectedTags = tags
        invalidateDay()
    }

    override fun onDayChanged(date: Long) {
        val currentDayDate = currentDay?.millis ?: return
        if (date.calendar().isSameDay(currentDayDate.calendar())) invalidateDay()
    }

    protected open fun processChangeEventInCalendarStatusRequest(request: Completable) {
        compositeDisposable += ReactiveNetwork.checkInternetConnectivity()
                .flatMapCompletable {
                    if (it) request
                    else Completable.error(NO_NETWORK_CONNECTION_ERROR)
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    invalidateDay()
                }, {
                    if (it == NO_NETWORK_CONNECTION_ERROR) viewState.showNoInternetDialog()
                })

    }

    override fun onSubEventClick(subEvent: SubEvent) {
        compositeDisposable += ReactiveNetwork.checkInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    if (it) {
                        viewState.showSubEvent(event.id, subEvent.id)
                    } else {
                        viewState.showNoInternetDialog()
                    }
                }, {})
    }

    override fun onAddToScheduleClick(subEvent: SubEvent) {
        processChangeEventInCalendarStatusRequest(
                eventRepository.addEventToCalendar(event.id, subEvent.id)
                        .andThen(Completable.fromAction { subEvent.isInCalendar = true })
        )
    }

    override fun onRemoveFromScheduleClick(subEvent: SubEvent) {
        processChangeEventInCalendarStatusRequest(
                eventRepository.removeEventFromCalendar(event.id, subEvent.id)
                        .andThen(Completable.fromAction { subEvent.isInCalendar = false })
        )
    }

    abstract fun filterSubEvent(subEvent: SubEvent): Boolean

    companion object {

        private val NO_NETWORK_CONNECTION_ERROR = Throwable("No network connection")
    }
}
