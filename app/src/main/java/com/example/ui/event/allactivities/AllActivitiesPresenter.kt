package com.example.ui.event.allactivities

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.UserEvent
import com.example.extensions.*
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.activities.ActivitiesContract
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*
import javax.inject.Inject

@InjectViewState
class AllActivitiesPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData,
        private val appData: AppData,
) : BasePresenter<AllActivitiesContract.View>(appData), AllActivitiesContract.Presenter {

    lateinit var userEvent: UserEvent
    lateinit var eventId: String
    protected var currentDay: EventScheduleCalendarDay? = null
    var canDoActions = false
    var isUpdate = false

    private fun getEventData() {
        compositeDisposable += userEventData.loadEventData(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    userEvent = it
                    canDoActions = userEvent.eventInfo.event.binds?.currentUserRegistration != null
                    invalidateDay()
                }
    }

    override fun attachView(view: AllActivitiesContract.View?) {
        super.attachView(view)
        getEventData()
    }

    private fun invalidateDay() {
        val days = userEventData.createCalendarDays(userEvent.activity.dates.map { defaultServerDateFormatter.parse(it.date).time })

        viewState.apply {
            setSubEventsData(userEvent.activity.activities, days)
            hidePlaceholder()
        }
    }

    private fun daySubEventsError() {
        viewState.apply {
            showEmptyEventPlaceholder()
        }
    }

    override fun onSubEventClick(subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(userEvent.eventId, subEvent.id.toString())
        }
    }

    override fun onAddToScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
                subEvent,
                eventRepository.addEventToCalendarWithResult(EventCalendarBody(appData.getId(),
                        EventCalendarBodyEntity(EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id?: 0)))
                        .flatMapCompletable { subEv -> Completable.fromAction { subEvent.binds?.userCalendar = subEv } }
        )
    }

    override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
                subEvent,
                eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                        .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    protected open fun processChangeEventInCalendarStatusRequest(subEvent: EventActivityModel, request: Completable) {
        compositeDisposable += request
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    isUpdate = true
                    /*viewState.updateSubevent(subEvent, date)*/getEventData()
                }
    }
}