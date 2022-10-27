package com.example.ui.event.activities

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameDay
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.getDaysFromDateToDate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject

@InjectViewState
class ActivitiesPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val appData: AppData,
) : BasePresenter<ActivitiesContract.View>(appData), ActivitiesContract.Presenter {

    lateinit var userEvent: UserEvent

    private var currentDay: EventScheduleCalendarDay? = null
    private var tags: List<Tag> = emptyList()
    private var mSearchWord = ""
    var tagsNew: List<Tag.EventTag>? = null
    lateinit var eventId: String

    var firstAttach = true
    private lateinit var mLastDay: EventScheduleCalendarDay

    private var mSubEventsMap = sortedMapOf<String, List<EventActivityModel>>()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (firstAttach) {
            getEventData()
            firstAttach = false
        }
    }

    fun getEventData() {
        compositeDisposable += userEventData.loadEventData(eventId)
            .withCheckInternetConnectivity()
            .withProgressBarLoadingDialog(viewState)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                Log.e("ActivitiesFragment", "Events: " + it.activity.activities.size)
                userEvent = it
                if (currentDay == null) {
                    findDay()
                }
                invalidateData()
                viewState.setSchemeButton(userEvent.eventInfo.event.destinationScheme)
            }
    }


    private fun getAllDates(): ArrayList<String> {
        val subEvents =
            userEvent.eventInfo.event.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
        val mStartEventDate = subEvents?.firstOrNull()?.holdingDate?.from ?: ""
        val mEndEventDate = subEvents?.lastOrNull()?.holdingDate?.from ?: ""
        //val mEndEventDate = "2022-06-29 04:40:00"
        //return getDaysFromMondayToSundayNew(mStartEventDate, mEndEventDate)

        return getDaysFromDateToDate(mStartEventDate, mEndEventDate)
    }

    private fun invalidateData() {
        compositeDisposable += Maybe.fromCallable {
            val dates = userEventData.createCalendarDays(userEvent.activity.dates.map {
                defaultServerDateFormatter.parse(it.date).time
            })
            tags = userEvent.activity.groups.plus(userEvent.activity.tags)
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
            collectDatesToWeeks(dates)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { list ->
                viewState.setDays(list)
                viewState.apply {
                    setTags(tags)
                    currentDay?.let { day ->
                        selectDay(day)
                        //scrollToDay(day)
                    }
                }
                invalidateDay()
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
            val days = /*userEventData.days ?: emptyList()*/
                userEventData.createCalendarDays(userEvent.activity.dates.map {
                    defaultServerDateFormatter.parse(it.date).time
                })
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                it.hasEvents &&
                        (dateCalendar.isSameDay(other.apply {
                            timeInMillis = it.millis
                        }) || it.millis - date > 0)
            } ?: days.lastOrNull()
        }
    }


    override fun onDaySelected(day: EventScheduleCalendarDay) {
        currentDay = day
        viewState.apply {
            scrollContent(day)
            selectDay(day)
        }
    }

    private fun updateSubEventsByTagOrText() {
        var dates = listOf<List<EventScheduleCalendarDay>>()
        compositeDisposable += Flowable.fromCallable {
            val eventsList = arrayListOf<EventActivityModel>()
            val selectedTags = tags.filter { it.isSelected }
            eventsList.addAll(userEvent.activity.activities)
            if (!mSearchWord.isNullOrEmpty()) {
                eventsList.forEach { e ->
                    if (!isEventHasParams(mSearchWord, e)) {
                        if (eventsList.contains(e))
                            eventsList.remove(e)
                    }
                }

            }
            if (!selectedTags.isNullOrEmpty()) {
                eventsList.forEach { e ->
                    selectedTags.forEach { tag ->
                        if (!filterTagsNew(e, tag)) {
                            if (eventsList.contains(e))
                                eventsList.remove(e)
                        }
                    }
                }
            }
            dates = collectDatesToWeeks(userEventData.createCalendarDays(eventsList.map { e ->
                defaultServerDateFormatter.parse(e.holdingDate?.from?.split(" ")?.get(0)).time
            }))
            Pair(
                eventsList.groupBy { x -> x.holdingDate?.from?.split(" ")?.get(0) ?: "" }.toSortedMap(),
                selectedTags
            )
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val canShow =
                    userEvent.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                viewState.apply {
                    if (it.first.isNullOrEmpty()) {
                        showEmptyEventPlaceholder()
                        setDays(emptyList())
                    } else {
                        setDays(dates)
                        setSubEvents(canShow, it.first, it.second)
                    }
                }
            }
    }

    private fun invalidateDay() {
        val day = currentDay ?: return daySubEventsError()
        compositeDisposable += Maybe.fromCallable {
            val selectedTags = tags.filter { it.isSelected }
            val list = arrayListOf<EventActivityModel>()
            list.addAll(userEvent.activity.activities)
            if (!selectedTags.isNullOrEmpty()) {
                list.forEach { e ->
                    selectedTags.forEach { tag ->
                        if (!filterTagsNew(e, tag)) {
                            if (list.contains(e))
                                list.remove(e)
                        }
                    }
                }
            }
            Pair(
                list.groupBy { x -> x.holdingDate?.from?.split(" ")?.get(0) ?: "" }.toSortedMap(),
                selectedTags
            )
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { map ->
                val canShow =
                    userEvent.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                viewState.apply {
                    setSubEvents(canShow, map.first, map.second)
                    scrollContent(day)
                }
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
            eventRepository.addEventToCalendarWithResult(
                EventCalendarBody(
                    appData.getId(),
                    EventCalendarBodyEntity(
                        EventCalendarBody.CALENDAR_EVENT_ACTIVITY,
                        subEvent.id ?: 0
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

    override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple { viewState.updateSubEvent(subEvent) }
    }


    private fun daySubEventsError() {
        viewState.apply {
            Log.e("ActivitiesFragment", "Activities Date error")
            showEmptyEventPlaceholder()
        }
    }

    private fun filterTagsNew(event: EventActivityModel, selectedTag: Tag): Boolean {
        var isHas = false
        if (!event.tag.isNullOrEmpty()) {
            if (event.tag.contains(selectedTag.id.toInt())) {
                isHas = true
            }
        } else {
            isHas = false
        }
        return isHas
    }

    override fun onSearchTextChange(text: String) = onSearchTextSubmit(text)
    override fun onTagSelectedListChange() = updateSubEventsByTagOrText()
    override fun onSearchTextSubmit(text: String) {
        mSearchWord = text
        updateSubEventsByTagOrText()
    }


    private fun isSameSpeaker(text: String, event: EventActivityModel): Boolean {
        var isSame = false
        event.binds?.member?.forEach {
            isSame = it.binds?.user?.nameLastName!!.contains(text, ignoreCase = true)
        }
        return isSame

    }


    private fun isEventHasParams(param: String, event: EventActivityModel): Boolean {
        var isHas = false
        if (!event.description.isNullOrEmpty() || !event.title.isNullOrEmpty()) {
            val desc = event.description
            val title = event.title
            if (desc!!.contains(param, true) || title!!.contains(param, true)) {
                isHas = true
            }
        }
        if (isSameSpeaker(param, event)) {
            isHas = true
        }
        return isHas
    }

    fun getLastDay(): EventScheduleCalendarDay {
        return mLastDay
    }

    private fun collectDatesToWeeks(dates: List<EventScheduleCalendarDay>): ArrayList<List<EventScheduleCalendarDay>> {
        var countSize = 0
        val listDays = arrayListOf<EventScheduleCalendarDay>()
        val days = arrayListOf<List<EventScheduleCalendarDay>>()
        dates.forEach {
            listDays.add(it)
            countSize++
            if (listDays.size == 7) {
                val list = arrayListOf<EventScheduleCalendarDay>()
                list.addAll(listDays)
                days.add(list)
                listDays.clear()
            } else {
                if (countSize == dates.size) {
                    val list = arrayListOf<EventScheduleCalendarDay>()
                    list.addAll(listDays)
                    days.add(list)
                }
            }
        }
        return days
    }

}


