package com.example.ui.event.activities

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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
            mSubEventsMap = userEvent.activity.activities
                .groupBy { x -> x.holdingDate?.from?.split(" ")?.get(0) ?: "" }
                .toSortedMap()

            val dates = userEventData.createCalendarDays(userEvent.activity.dates.map {
                defaultServerDateFormatter.parse(it.date).time
            })

            collectDatesToWeeks(dates)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { list ->
                viewState.setDays(list)
                compositeDisposable += Completable.fromAction {
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
                }.performOnBackgroundOutOnMain()
                    .subscribeSimple {
                        viewState.apply {
                            setTags(tags)
                            currentDay?.let { day ->
                                selectDay(day)
                                scrollToDay(day)
                            }
                        }
                        invalidateDay()
                    }


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

    @SuppressLint("LogNotTimber")
    private fun updateSubEventsByTagOrText() {
        compositeDisposable += Maybe.fromCallable {
            val selectedTags = tags.filter { it.isSelected }
            val mFilteredEventsMap = mutableMapOf<String, LinkedList<EventActivityModel>>()
            mSubEventsMap.forEach { map ->
                val mList = LinkedList<EventActivityModel>()
                map.value.forEach { event ->
                    mList.add(event)
                    if (!mSearchWord.isNullOrEmpty()) {
                        if (!isEventHasParams(mSearchWord, event)) {
                            if (mList.contains(event))
                                mList.remove(event)
                        }
                    }
                    if (!selectedTags.isNullOrEmpty()) {
                        selectedTags.forEach { tag ->
                            if (!filterTagsNew(event, tag)) {
                                if (mList.contains(event))
                                    mList.remove(event)
                            }
                        }
                    }
                }
                if (!mList.isNullOrEmpty()) {
                    mFilteredEventsMap.put(map.key, mList)
                }
            }
            Pair(mFilteredEventsMap, selectedTags)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {},
                onSuccess = {
                    val canShow =
                        userEvent.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                    viewState.apply {
                        Log.e("ActivitiesFragment", "Events: " + it.first.size)
                        if (it.first.isNullOrEmpty()) {
                            showEmptyEventPlaceholder()
                        } else {
                            setSubEvents(canShow, it.first, it.second)
                        }
                    }
                })
    }

    @SuppressLint("LogNotTimber")
    private fun invalidateDay() {
        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }
        var filteredMap = mutableMapOf<String, List<EventActivityModel>>()

        compositeDisposable += Completable.fromAction {
            if (selectedTags.isNullOrEmpty()) {
                filteredMap = mSubEventsMap
            } else {
                mSubEventsMap.forEach {
                    val mList = LinkedList<EventActivityModel>()
                    it.value.forEach { event ->
                        selectedTags.forEach { tag ->
                            if (filterTagsNew(event, tag)) {
                                mList.add(event)
                            }
                        }
                    }
                    filteredMap[it.key] = mList
                }
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val canShow =
                    userEvent.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED

                viewState.apply {
                    Log.e("ActivitiesFragment", "Events: " + filteredMap.size)
                    setSubEvents(canShow, filteredMap, selectedTags)
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

    private fun collectDatesToWeeks(dates : List<EventScheduleCalendarDay>): ArrayList<List<EventScheduleCalendarDay>> {
        var countSize = 0
        val listDays = arrayListOf<EventScheduleCalendarDay>()
        val days = arrayListOf<List<EventScheduleCalendarDay>>()
        dates.forEach {
            listDays.add(it)
            countSize++
            if (listDays.size == 7){
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


