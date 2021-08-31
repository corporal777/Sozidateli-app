package com.example.ui.event.activities

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.extensions.*
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.schedule.EventScheduleContract
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*
import java.util.Locale.filterTags
import javax.inject.Inject

@InjectViewState
class ActivitiesPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val userEventData: UserEventData,
        private val appData: AppData,
) : BasePresenter<ActivitiesContract.View>(), ActivitiesContract.Presenter {

    lateinit var userEvent: UserEvent

    protected var currentDay: EventScheduleCalendarDay? = null
    protected var tags: List<Tag> = emptyList()
    var tagsNew: List<Tag.EventTag>? = null
    lateinit var eventId: String
    var daysSize = 0
    var canDoActions = false

    var firstAttach = true

    fun getEventData() {
        compositeDisposable += userEventData.loadEventData(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    Log.e("ActivitiesFragment", "Events: " + it.activity.activities.size)
                    userEvent = it
                    if (currentDay == null) {
                        findDay()
                    }
                    canDoActions = userEvent.eventInfo.event.binds?.currentUserRegistration != null
                    invalidateData()
                }
    }

    //private val onLoadingComplete: () -> Unit = {
    private fun invalidateData() {
        viewState.apply {
            tags = userEvent.activity.groups.plus(userEvent.activity.tags)
            val days = userEventData.createCalendarDays(userEvent.activity.dates.map { defaultServerDateFormatter.parse(it.date).time })
            daysSize = days.size
            setDays(days)

            if (tagsNew != null) {
                tags.forEach {
                    val nt = tagsNew?.firstOrNull { t -> t.id == it.id }
                    if (nt != null) {
                        it.isSelected = nt.isSelected
                    } else {
                        it.isSelected = it.isSelected
                    }
                }
            }

            setTags(tags)
            currentDay?.let { day ->
                selectDay(day)
                scrollToDay(day)
            }
        }

        invalidateDay()
    }

    fun getTagsList() = userEvent.activity.groups.plus(userEvent.activity.tags).map { NewTags(it.id, it.name, it.isSelected) }

    override fun attachView(view: ActivitiesContract.View?) {
        super.attachView(view)
        if (firstAttach) {
            getEventData()
            firstAttach = false
        }
    }

    private fun findDay() {
        compositeDisposable += findNearestDayFromEventDays(System.currentTimeMillis())
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onError = {
                            Log.e("ActivitiesFragment", "Date error")
                            viewState.apply { showEmptyEventPlaceholder() }
                            onReceiveError(it)
                        },
                        onComplete = {
                            /*onLoadingComplete*/
                        })
    }

    private fun findNearestDayFromEventDays(date: Long): Completable {
        return Completable.fromAction {
            val days = /*userEventData.days ?: emptyList()*/userEventData.createCalendarDays(userEvent.activity.dates.map { defaultServerDateFormatter.parse(it.date).time })
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                it.hasEvents &&
                        (dateCalendar.isSameDay(other.apply { timeInMillis = it.millis }) || it.millis - date > 0)
            } ?: days.lastOrNull()
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

    override fun onShowAllTagsClick() {
        viewState.showAllTags()
    }

    protected open fun processChangeEventInCalendarStatusRequest(subEvent: EventActivityModel, request: Completable) {
        compositeDisposable += request
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.updateSubevent(subEvent) }
    }

    private fun invalidateDay() {
        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }
        val subEvents = userEvent.activity.activities.let {
            it.filter { event ->
                val date = defaultServerDateTimeFormatter.parse(event.holdingDate?.from)
                filterSubEvent(event)
                        && filterTags(event, selectedTags)
                        && date.time >= day.millis.startOfDay() && date.time <= day.millis.endOfDay()
            }
        }

        viewState.apply {
            Log.e("ActivitiesFragment", "Events: " + subEvents.size + " invalidateDay")
            setSubEvents(subEvents, if (mustFilterTags()) selectedTags else emptyList())
            if (subEvents.isEmpty()) {
                Log.e("ActivitiesFragment", "Activities error")
                showEmptyDayPlaceholder()
                hideCurrentDay()
            } else {
                currentDay?.let { day -> showCurrentDay(day, daysSize) }
                hidePlaceholder()
            }
        }
    }

    private fun daySubEventsError() {
        viewState.apply {
            Log.e("ActivitiesFragment", "Activities Date error")
            hideCurrentDay()
            showEmptyEventPlaceholder()
        }
    }

    private fun filterTags(event: EventActivityModel, selectedTags: List<Tag>): Boolean {
        if (!mustFilterTags() || selectedTags.isEmpty()) return true
        return selectedTags.any { tag ->
            event.tag?.any { eventTag -> eventTag.toString() == tag.id } ?: false
        }
    }

    fun filterSubEvent(subEvent: EventActivityModel): Boolean = true
    fun mustFilterTags(): Boolean = true
}