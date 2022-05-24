package com.example.ui.event.activities

import android.os.Build
import android.os.DropBoxManager
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
import com.example.util.custom.LinkedSet
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.firstInMonth
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import javax.inject.Inject

@InjectViewState
class ActivitiesPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val appData: AppData,
) : BasePresenter<ActivitiesContract.View>(appData), ActivitiesContract.Presenter {

    lateinit var userEvent: UserEvent

    lateinit var searchInterface: SearchInterface

    protected var currentDay: EventScheduleCalendarDay? = null
    protected var tags: List<Tag> = emptyList()
    protected var mSearchWord = ""
    var tagsNew: List<Tag.EventTag>? = null
    lateinit var eventId: String
    var daysSize = 0
    var canDoActions = false

    var firstAttach = true

    private var mStartEventDate = ""
    private var mEndEventDate = ""
    val mSubEventsMap = mutableMapOf<String, LinkedSet<EventActivityModel>>()

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
    private fun getAllDates(): ArrayList<String> {
        val mStartDate = defaultServerDateFormatter.parse(mStartEventDate).time
        val mEndDate = defaultServerDateFormatter.parse(mEndEventDate).time

        var mDayOfWeek = mStartDate.calendar().get(Calendar.DAY_OF_WEEK)
        var mStartDay = mStartDate.calendar().get(Calendar.DAY_OF_MONTH) - 1


        val mDates = arrayListOf<String>()
        val calPrev = Calendar.getInstance()
        val calStart = mStartDate.calendar()
        val calEnd = mEndDate.calendar()

        calPrev.set(Calendar.MONTH, calStart.get(Calendar.MONTH) - 1)

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val maxDayPrev = calPrev.getActualMaximum(Calendar.DAY_OF_MONTH)
        val maxDayStart = calStart.getActualMaximum(Calendar.DAY_OF_MONTH)
        val maxDayEnd = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH)

        val prev = LocalDate.of(
            calStart.get(Calendar.YEAR),
            calStart.get(Calendar.MONTH),
            calStart.get(Calendar.DAY_OF_MONTH)
        )
        val firstMonday = prev.with(firstInMonth(DayOfWeek.MONDAY))
        val firstMondayValue = firstMonday.dayOfWeek.value
        val firstDayOfMonth = prev.with(firstDayOfMonth())
        val dayOfWeekValue = firstDayOfMonth.dayOfWeek.value

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
                    val prev = LocalDate.of(
                        calStart.get(Calendar.YEAR),
                        calStart.get(Calendar.MONTH),
                        calStart.get(Calendar.DAY_OF_MONTH)
                    )
                    val lastDay = prev.with(TemporalAdjusters.lastDayOfMonth())
                    val dayOfWeekValue = lastDay.dayOfWeek.value

                    val startPrev = maxDayPrev - dayOfWeekValue
                    for (i in startPrev until maxDayPrev) {
                        calPrev[Calendar.DAY_OF_MONTH] = i + 1
                        Log.e("DAY PREV", calPrev.get(Calendar.DAY_OF_MONTH).toString())
                        mDates.add(df.format(calPrev.time))
                    }
                    for (i in 0 until maxDayStart) {
                        calStart[Calendar.DAY_OF_MONTH] = i + 1
                        Log.e("DAY", calStart.get(Calendar.DAY_OF_MONTH).toString())
                        mDates.add(df.format(calStart.time))
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
                mDates.add(df.format(calEnd.time))
            }
        } else {
            for (i in 0 until maxDayEnd) {
                calEnd[Calendar.DAY_OF_MONTH] = i + 1
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
            val eventDays = userEventData.createCalendarDays(userEvent.activity.dates.map {
                defaultServerDateFormatter.parse(it.date).time
            })

            var days = userEventData.createCalendarDaysNew(getAllDates().map {
                val list = LinkedSet<EventActivityModel>()
                mSubEventsMap[it] = list

                defaultServerDateFormatter.parse(it).time
            })



            days.forEachIndexed { index, day ->
                eventDays.forEach { eventDay ->
                    if (day.millis == eventDay.millis) {
                        days[index] = eventDay
                    }
                }
            }

            daysSize = days?.size!!
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

    fun getTagsList() = userEvent.activity.groups.plus(userEvent.activity.tags)
        .map { NewTags(it.id, it.name, it.isSelected) }

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDaySelected(day: EventScheduleCalendarDay) {
        currentDay = day
        viewState.apply { selectDay(day) }
        invalidateDay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTagSelectedListChange() {
        invalidateDay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            .subscribeSimple { viewState.updateSubevent(subEvent) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun invalidateDay() {

        val day = currentDay ?: return daySubEventsError()
        val selectedTags = tags.filter { it.isSelected }

        val subEventsMap = mutableMapOf<String, LinkedSet<EventActivityModel>>()
        val filteredMap = mutableMapOf<String, LinkedSet<EventActivityModel>>()

//        val mDates = getAllDates()
//        mDates.forEach {
//            val date = defaultServerDateFormatter.parse(it)
//            val list = arrayListOf<EventActivityModel>()
//            userEvent.activity.activities.filter { x -> date == defaultServerDateFormatter.parse(x.holdingDate?.from) }
//                .forEach { event ->
//                    list.add(event)
//                }
//            subEventsMap[it] = list
//        }

        subEventsMap.clear()
        subEventsMap.putAll(mSubEventsMap)

        subEventsMap
            .filter {
                defaultServerDateTimeFormatter.parse(it.key).time >= day.millis.startOfDay()
            }
            .map {
                val date = defaultServerDateFormatter.parse(it.key)
                val list = arrayListOf<EventActivityModel>()
                userEvent.activity.activities.filter { x ->
                    date == defaultServerDateFormatter.parse(
                        x.holdingDate?.from
                    )
                }
                    .forEach { event ->
                        list.add(event)
                    }
                it.value.clear()
                it.value.addAll(list)

                it.value.forEach { event ->
                    if (!mSearchWord.isNullOrEmpty()) {
                        if (!isEventHasParams(mSearchWord, event)) {
                            val emptyEvent = EventActivityModel(hide = true, mNoEvent = true)
                            it.value.remove(event)
                            if (!it.value.contains(emptyEvent) && it.value.isNullOrEmpty()) {
                                it.value.add(emptyEvent)
                            }
                        }

                    }
                    if (!selectedTags.isNullOrEmpty()) {
                        selectedTags.forEach { tag ->
                            if (!event.tag.isNullOrEmpty() && !event.tag.contains(tag.id.toInt())) {
                                val emptyEvent = EventActivityModel(hide = true, mNoEvent = true)
                                it.value.remove(event)
                                if (!it.value.contains(emptyEvent) && it.value.isNullOrEmpty()) {
                                    it.value.add(emptyEvent)
                                }
                            }
                        }
                    }
                }
                filteredMap.put(it.key, it.value)
            }


//        val filteredSubEvents = subEventsMap.filter {
//            it.value.forEach { event ->
//                if (!mSearchWord.isNullOrEmpty()) {
//                    if (!isEventHasParams(mSearchWord, event)) {
//                        val emptyEvent = EventActivityModel(hide = true, mNoEvent = true)
//                        it.value.remove(event)
//                        if (!it.value.contains(emptyEvent) && it.value.isNullOrEmpty()) {
//                            it.value.add(emptyEvent)
//                        }
//                    }
//
//                }
//                if (!selectedTags.isNullOrEmpty()) {
//                    selectedTags.forEach { tag ->
//                        if (!event.tag.isNullOrEmpty() && !event.tag.contains(tag.id.toInt())) {
//                            val emptyEvent = EventActivityModel(hide = true, mNoEvent = true)
//                            it.value.remove(event)
//                            if (!it.value.contains(emptyEvent) && it.value.isNullOrEmpty()) {
//                                it.value.add(emptyEvent)
//                            }
//                        }
//                    }
//                }
//            }
//
//            defaultServerDateTimeFormatter.parse(it.key).time >= day.millis.startOfDay()
//        }


//        val subEvents = userEvent.activity.activities.let {
//            it.filter { event ->
//                val date = defaultServerDateTimeFormatter.parse(event.holdingDate?.from)
//                filterSubEvent(event)
//                        && filterTags(event, selectedTags)
//                        //&& date.time >= day.millis.startOfDay() && date.time <= day.millis.endOfDay()
//                        && date.time >= day.millis.startOfDay()
//            }
//        }

        viewState.apply {
            //Log.e("ActivitiesFragment", "Events: " + filteredSubEvents.size + " invalidateDay")

            setSubEventsNew(
                filteredMap,
                if (mustFilterTags()) selectedTags else emptyList()
            )
            currentDay?.let { day -> showCurrentDay(day, daysSize) }

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSearchTextChange(text: String) {
        onSearchTextSubmit(text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSearchTextSubmit(text: String) {
        mSearchWord = text
        invalidateDay()
        searchInterface.apply {
            searchText = text
            searchTextCallback?.invoke()
        }
    }


    private fun isSameSpeaker(text: String, event: EventActivityModel): Boolean {
        var isSame = false
        event.binds?.member?.forEach {
            if (!text.isNullOrEmpty() && it.binds?.user?.name?.contains(
                    text,
                    ignoreCase = true
                ) == true
            ) {
                isSame = true
            }
        }
        return isSame

    }


    private fun isEventHasParams(param: String, event: EventActivityModel): Boolean {
        var isHas = false
        if (!event.description.isNullOrEmpty() && !param.isNullOrEmpty()) {
            val desc = event.description
            if (desc.contains(param, true)) {
                isHas = true
            }
        } else if (!event.title.isNullOrEmpty() && !param.isNullOrEmpty()) {
            val title = event.title
            if (title.contains(param, true)) {
                isHas = true
            }
        } else if (isSameSpeaker(param, event)) {
            isHas = true
        }
        return isHas
    }

    override fun onShowAllTagsClick() = viewState.showAllTags()

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

}