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
import com.example.ui.search.SearchInterface
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import java.text.SimpleDateFormat
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

    private var mStartEventDate = ""
    private var mEndEventDate = ""

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
                mStartEventDate = userEvent.eventInfo.event.holdingDate?.from ?: ""
                mEndEventDate = userEvent.eventInfo.event.holdingDate?.to ?: ""
                if (currentDay == null) {
                    findDay()
                }
                canDoActions = userEvent.eventInfo.event.binds?.currentUserRegistration != null
                invalidateData()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun attachView(view: ActivitiesContract.View?) {
        super.attachView(view)

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
        val mStartDate = defaultServerDateFormatter.parse(mStartEventDate).time
        val mEndDate = defaultServerDateFormatter.parse(mEndEventDate).time

        var mDayOfWeek = mStartDate.calendar().get(Calendar.DAY_OF_WEEK)
        var mStartDay = mStartDate.calendar().get(Calendar.DAY_OF_MONTH) - 1


        val mDates = arrayListOf<String>()
        val calStart = mStartDate.calendar()
        val calEnd = mEndDate.calendar()

        //val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val maxDayStart = calStart.getActualMaximum(Calendar.DAY_OF_MONTH)

        if (mDayOfWeek == 2) {
            for (i in mStartDay until maxDayStart) {
                calStart[Calendar.DAY_OF_MONTH] = i + 1
                mDates.add(df.format(calStart.time))
            }
        } else {
            when {
                getMonday(mDayOfWeek) < mStartDay || getMonday(mDayOfWeek) == mStartDay -> {
                    mStartDay -= getMonday(mDayOfWeek)
                    for (i in mStartDay until maxDayStart) {
                        calStart[Calendar.DAY_OF_MONTH] = i + 1
                       mDates.add(df.format(calStart.time))
                    }

                }
                else -> {

                    val calStartDatePrev = Calendar.getInstance()
                    var firstMondayPrevDate = 0
                    var lastDayOfPrevDate = 0
                    calStartDatePrev.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    calStartDatePrev.set(Calendar.DAY_OF_WEEK_IN_MONTH, 5);
                    calStartDatePrev.set(Calendar.MONTH, calStart.get(Calendar.MONTH) - 1);
                    calStartDatePrev.set(Calendar.YEAR, calStart.get(Calendar.YEAR));

                    if (calStartDatePrev.get(Calendar.MONTH) != calStart.get(Calendar.MONTH)) {
                        firstMondayPrevDate = calStartDatePrev.get(Calendar.DATE)
                        lastDayOfPrevDate = calStartDatePrev.getActualMaximum(Calendar.DAY_OF_MONTH)
                    } else {
                        calStartDatePrev.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        calStartDatePrev.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);
                        calStartDatePrev.set(Calendar.MONTH, calStart.get(Calendar.MONTH) - 1);
                        calStartDatePrev.set(Calendar.YEAR, calStart.get(Calendar.YEAR));
                        firstMondayPrevDate = calStartDatePrev.get(Calendar.DATE)
                        lastDayOfPrevDate = calStartDatePrev.getActualMaximum(Calendar.DAY_OF_MONTH)
                    }

                    for (i in firstMondayPrevDate - 1 until lastDayOfPrevDate) {
                        calStartDatePrev[Calendar.DAY_OF_MONTH] = i + 1
                        if (!mDates.contains(df.format(calStartDatePrev.time))) {
                            mDates.add(df.format(calStartDatePrev.time))
                        }

                    }
                    for (i in 0 until maxDayStart) {
                        calStart[Calendar.DAY_OF_MONTH] = i + 1
                        if (!mDates.contains(df.format(calStart.time))) {
                            mDates.add(df.format(calStart.time))
                        }
                    }

                }
            }
        }

        val mEndMonth = mEndDate.calendar().get(Calendar.MONTH)
        var mEndDayOfWeek = mEndDate.calendar().get(Calendar.DAY_OF_WEEK)
        var mEndDay = mEndDate.calendar().get(Calendar.DAY_OF_MONTH)

        if (mEndDayOfWeek == 1) {
            for (i in 0 until mEndDay) {
                calEnd[Calendar.DAY_OF_MONTH] = i + 1
                if (!mDates.contains(df.format(calEnd.time)))
                    mDates.add(df.format(calEnd.time))
            }
        } else {
            val maxDayEnd = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 0 until maxDayEnd) {
                calEnd[Calendar.DAY_OF_MONTH] = i + 1
                if (!mDates.contains(df.format(calEnd.time)))
                   mDates.add(df.format(calEnd.time))

                if (calEnd[Calendar.DAY_OF_MONTH] > mEndDay && calEnd.get(Calendar.DAY_OF_WEEK) == 1) {
                    break
                }
                if (calEnd[Calendar.DAY_OF_MONTH] == maxDayEnd && calEnd.get(Calendar.DAY_OF_WEEK) != 1) {
                    val currentDW = (7 - calEnd.get(Calendar.DAY_OF_WEEK)) + 1
                    val calNext = Calendar.getInstance()
                    calNext.set(Calendar.MONTH, calEnd.get(Calendar.MONTH) + 1)
                    for (k in 0 until currentDW) {
                        calNext[Calendar.DAY_OF_MONTH] = k + 1
                        if (!mDates.contains(df.format(calNext.time)))
                           mDates.add(df.format(calNext.time))
                    }
                }
            }
        }

        return mDates
    }


    //private val onLoadingComplete: () -> Unit = {
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
            selectDay(day)
            scrollContent(day)
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
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onComplete = {
                    viewState.apply {
                        Log.e("ActivitiesFragment", "Events: " + mOnlySubEventsMap.size)
                        updateSubEventsNew(mOnlySubEventsMap, selectedTags)
                    }
                })
    }

    @SuppressLint("LogNotTimber")
    private fun invalidateDay() {

        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }
        var mFilteredMap = mSubEventsMap

        viewState.apply {
            Log.e("ActivitiesFragment", "Events: " + mFilteredMap.size)
            setSubEvents(day, mFilteredMap, selectedTags)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDayChanged(date: Long) {
//        val currentDayDate = currentDay?.millis ?: return
//        if (date.calendar().isSameDay(currentDayDate.calendar())) invalidateDay()
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

    fun getTagsList() = userEvent.activity.groups.plus(userEvent.activity.tags)
        .map { NewTags(it.id, it.name, it.isSelected) }


    fun getFirstDay(): EventScheduleCalendarDay {
        return mFirstDay
    }

    fun getLastDay() :EventScheduleCalendarDay {
        return mLastDay
    }

    fun getCurrentDay(day: EventScheduleCalendarDay): EventScheduleCalendarDay{
        return day
    }

}


