package com.example.ui.event.activities

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameDay
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.notification.center.redesign.NotificationsSortedData
import com.example.util.getDaysFromDateToDate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
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
    private var mSearchWord = ""
    var tagsNew: List<Tag.EventTag>? = null
    lateinit var eventId: String

    private var isFirstAttach = true
    private lateinit var mLastDay: EventScheduleCalendarDay

    private var groupedEventList = arrayListOf<SubEventsData>()
    private var eventTags: List<Tag> = emptyList()
    private var eventDates = emptyList<List<EventScheduleCalendarDay>>()
    private var isStatusApproved = false


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setContentPlaceholder()
        compositeDisposable += userEventData.loadEventData(eventId)
            .doOnSuccess { e ->
                userEvent = e
                isStatusApproved = e.eventInfo.event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                eventTags = e.activity.groups.plus(e.activity.tags)
                if (tagsNew != null) {
                    eventTags.forEach {
                        val nt = tagsNew?.firstOrNull { t -> t.id == it.id }
                        if (nt != null) it.isSelected = nt.isSelected
                        else it.isSelected = it.isSelected
                    }
                }
            }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                initContent()
                viewState.setSchemeButton(userEvent.isHasBuildingScheme())
            }
    }


    private fun initContent() {
        compositeDisposable += Maybe.defer {
            val selectedTags = eventTags.filter { it.isSelected }
            val list = checkParams(userEvent.activity.activities, selectedTags)
            val groupedList = groupData(selectedTags, list)
            if (!groupedList.isNullOrEmpty()) {
                val dates = groupedList.mapNotNull { x -> x.titleDate }
                eventDates = collectDatesToWeeks(userEventData.createCalendarDays(dates.map { s ->
                    defaultServerDateFormatter.parse(s).time
                }))
            }
            Maybe.just(groupData(selectedTags, list))
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { list ->
                viewState.apply {
                    if (list.isNullOrEmpty()) showEmptyEventPlaceholder()
                    else {
                        setDays(eventDates)
                        setTags(eventTags)
                        setSubEvents(isStatusApproved, list)
                        if (isFirstAttach){
                            if (currentDay == null) findDay()
                            isFirstAttach = false
                        }
                    }
                }
            }
    }


    private fun findDay() {
        compositeDisposable += Completable.fromAction {
            val date = System.currentTimeMillis()
            val days = userEventData.createCalendarDays(
                userEvent.activity.dates.map { defaultServerDateFormatter.parse(it.date).time }
            )
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                it.hasEvents &&
                        (dateCalendar.isSameDay(other.apply {
                            timeInMillis = it.millis
                        }) || it.millis - date > 0)
            } ?: days.lastOrNull()
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = { if (currentDay != null) viewState.scrollContent(currentDay!!) }
            )
    }

    override fun onDaySelected(day: EventScheduleCalendarDay) {
        currentDay = day
        viewState.apply {
            scrollContent(day)
            selectDay(day)
        }
    }

    override fun onSubEventClick(subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(userEvent.eventId, subEvent.id.toString())
        }
    }

    override fun onSchemeClick() = viewState.showScheme(eventId)


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


    override fun onSearchTextChange(text: String) = onSearchTextSubmit(text)
    override fun onTagSelected() = initContent()
    override fun onSearchTextSubmit(text: String) {
        mSearchWord = text
        initContent()
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

    private fun isEventHasTag(event: EventActivityModel, tags: List<Tag>): Boolean {
        return !tags.filter { x -> event.tag?.contains(x.id.toInt()) == true }.isNullOrEmpty()
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

    private fun checkParams(
        list: List<EventActivityModel>,
        selectedTags: List<Tag>
    ): List<EventActivityModel> {
        return if (list.isNullOrEmpty()) return emptyList()
        else if (!mSearchWord.isNullOrBlank() && !selectedTags.isNullOrEmpty())
            list.filter { x -> isEventHasTag(x, selectedTags) && isEventHasParams(mSearchWord, x) }
        else if (!mSearchWord.isNullOrEmpty()) list.filter { x -> isEventHasParams(mSearchWord, x) }
        else if (!selectedTags.isNullOrEmpty()) list.filter { x -> isEventHasTag(x, selectedTags) }
        else list
    }

    private fun groupData(
        tags: List<Tag>,
        list: List<EventActivityModel>
    ): ArrayList<SubEventsData> {
        val subEventsList = arrayListOf<SubEventsData>()
        var titleDate: String? = ""
        if (!list.isNullOrEmpty()) {
            list.forEach { subEvent ->
                val subEventDate = subEvent.holdingDate?.from?.split(" ")?.get(0) ?: ""
                if (titleDate == subEventDate) titleDate = null
                else titleDate = subEventDate

                subEventsList.add(SubEventsData(titleDate, subEvent, tags))
                titleDate = subEventDate
            }
            groupedEventList = subEventsList
        }
        return subEventsList
    }

    fun createCalendarDay(day: String?): EventScheduleCalendarDay {
        val date = defaultServerDateFormatter.parse(day).time
        val cal = date.calendar()
        return EventScheduleCalendarDay(
            date,
            cal.get(Calendar.WEEK_OF_MONTH),
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ?: "",
            cal.get(Calendar.DAY_OF_MONTH),
            false
        )
    }
}

data class SubEventsData(
    var titleDate: String?,
    var subEvent: EventActivityModel,
    var selectedTags: List<Tag>
) {
    fun getDateInLong(): Long? {
        return if (!titleDate.isNullOrEmpty()) defaultServerDateFormatter.parse(titleDate)?.time
        else 0
    }

}


