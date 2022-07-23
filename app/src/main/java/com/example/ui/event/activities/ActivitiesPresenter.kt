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
import com.example.extensions.*
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.getDaysFromDateToDate
import com.example.util.getDaysFromMondayToSundayNew
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
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

    protected var currentDay: EventScheduleCalendarDay? = null
    protected var tags: List<Tag> = emptyList()
    protected var mSearchWord = ""
    var tagsNew: List<Tag.EventTag>? = null
    lateinit var eventId: String
    var daysSize = 0
    var canDoActions = false

    var firstAttach = true
    private lateinit var mFirstDay: EventScheduleCalendarDay
    private lateinit var mLastDay: EventScheduleCalendarDay

    private val mSubEventsMap = mutableMapOf<String, LinkedList<EventActivityModel>>()
    private var mShortSubEventsMap = mapOf<String, List<EventActivityModel>>()

    @RequiresApi(Build.VERSION_CODES.O)
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
                canDoActions = userEvent.eventInfo.event.binds?.currentUserRegistration != null
                invalidateData()
                viewState.setSchemeButton(userEvent.eventInfo.event.destinationScheme)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (firstAttach) {
            getEventData()
            firstAttach = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllDates(): ArrayList<String> {

        val subEvents =
            userEvent.eventInfo.event.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
        val mStartEventDate = subEvents?.get(0)?.holdingDate?.from ?: ""
        val mEndEventDate = subEvents?.last()?.holdingDate?.from ?: ""
        //return getDaysFromMondayToSundayNew(mStartEventDate, mEndEventDate)

        return getDaysFromDateToDate(mStartEventDate, mEndEventDate)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun invalidateData() {
        viewState.apply {
            tags = userEvent.activity.groups.plus(userEvent.activity.tags)
            var mDays = arrayListOf<EventScheduleCalendarDay>()
            compositeDisposable += Completable.fromAction {
                val eventDays = userEventData.createCalendarDays(userEvent.activity.dates.map {
                    defaultServerDateFormatter.parse(it.date).time
                })

                getAllDates().let { allDates ->

                    mDays = userEventData.createCalendarDaysNew(allDates.map {
                        Log.e("DATE", it)
                        val mList = LinkedList<EventActivityModel>()
                        val date = defaultServerDateFormatter.parse(it)
                        mList.addAll(userEvent.activity.activities.filter { x ->
                            date == defaultServerDateFormatter.parse(
                                x.holdingDate?.from
                            )
                        })
                        mSubEventsMap[it] = mList

                        defaultServerDateFormatter.parse(it).time
                    })
                }

                mDays.forEachIndexed { index, day ->
                    eventDays.forEach { eventDay ->
                        if (day.millis == eventDay.millis) {
                            mDays[index] = eventDay
                        }
                    }
                }
                mFirstDay = mDays[0]
                mLastDay = mDays.last()

                mShortSubEventsMap = userEvent.activity.activities.groupBy { event ->
                    event.holdingDate?.from?.split(" ")?.get(0) ?: ""
                }

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
                    daysSize = mDays?.size!!
                    setDays(mDays)

                    setTags(tags)
                    currentDay?.let { day ->
                        selectDay(day)
                        scrollToDay(day)
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
        //invalidateDay()
    }

    @SuppressLint("LogNotTimber")
    private fun updateSubEventsByTagOrText() {
        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }

        val mOnlySubEventsMap = mutableMapOf<String, LinkedList<EventActivityModel>>()

        compositeDisposable += Completable.fromAction {
            mShortSubEventsMap.map { map ->
                val mList = LinkedList<EventActivityModel>()
                map.value.forEach { event ->
                    mList.add(event)
                    val emptyEvent = EventActivityModel(hide = true, mNoEvent = true)
                    if (!mSearchWord.isNullOrEmpty()) {
                        if (!isEventHasParams(mSearchWord, event)) {
                            mList.remove(event)
                            if (!mList.contains(emptyEvent) && mList.isNullOrEmpty()) {
                                mList.add(emptyEvent)
                            }
                        }
                    }
                    if (!selectedTags.isNullOrEmpty()) {
                        selectedTags.forEach { tag ->
                            if (!filterTagsNew(event, tag)) {
                                mList.remove(event)
                                if (!mList.contains(emptyEvent) && mList.isNullOrEmpty()) {
                                    mList.add(emptyEvent)
                                }
                            }
                        }
                    }
                }
                mOnlySubEventsMap.put(map.key, mList)
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onComplete = {
                    val canShow =
                        userEvent.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                    viewState.apply {
                        Log.e("ActivitiesFragment", "Events: " + mOnlySubEventsMap.size)
                        updateSubEventsNew(canShow, mOnlySubEventsMap, selectedTags)
                    }
                })
    }

    @SuppressLint("LogNotTimber")
    private fun invalidateDay() {

        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }
        var mFilteredMap = mutableMapOf<String, LinkedList<EventActivityModel>>()

        compositeDisposable += Completable.fromAction {
            if (selectedTags.isNullOrEmpty()) {
                mFilteredMap = mSubEventsMap
            }else {
                mSubEventsMap.forEach {
                    val mList = LinkedList<EventActivityModel>()
                    it.value.forEach { event ->
                        mList.add(event)
                        val emptyEvent = EventActivityModel(hide = true, mNoEvent = true)
                        selectedTags.forEach { tag ->
                            if (!filterTagsNew(event, tag)) {
                                mList.remove(event)
                                if (!mList.contains(emptyEvent) && mList.isNullOrEmpty()) {
                                    mList.add(emptyEvent)
                                }
                            }
                        }
                    }
                    mFilteredMap[it.key] = mList
                }

            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val canShow =
                    userEvent.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED

                viewState.apply {
                    Log.e("ActivitiesFragment", "Events: " + mFilteredMap.size)
                    setSubEvents(canShow, day, mFilteredMap, selectedTags)
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

    protected open fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            //.withLoadingDialog(viewState)
            .subscribeSimple { viewState.updateSubEvent(subEvent) }
    }


    private fun daySubEventsError() {
        viewState.apply {
            Log.e("ActivitiesFragment", "Activities Date error")
            showEmptyEventPlaceholder()
        }
    }

    private fun filterTags(event: EventActivityModel, selectedTags: List<Tag>): Boolean {
        if (!mustFilterTags() || selectedTags.isEmpty()) return true
        return selectedTags.any { tag ->
            event.tag?.any { eventTag -> eventTag.toString() == tag.id } ?: false
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

    fun filterSubEvent(subEvent: EventActivityModel): Boolean = true
    fun mustFilterTags(): Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSearchTextChange(text: String) {
        onSearchTextSubmit(text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTagSelectedListChange() {
        updateSubEventsByTagOrText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    override fun onShowAllTagsClick() {
    }

    private fun getMonday(day: Int): Int {
        var mDay = 0
        if (day != 2 && day != 1) {
            mDay = day - 2
        }
        if (day == 1) {
            mDay = day + 5
        }
        return mDay
    }

    fun getLastDay(): EventScheduleCalendarDay {
        return mLastDay
    }

}


