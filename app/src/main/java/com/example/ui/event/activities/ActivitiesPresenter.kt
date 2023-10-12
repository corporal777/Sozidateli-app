package com.example.ui.event.activities

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
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
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

    private var currentDay: EventScheduleDay? = null
    private var searchWord = ""
    var tagsNew: List<Tag.EventTag>? = null
    lateinit var eventId: String

    private var isFirstAttach = true

    private var groupedEventList = arrayListOf<SubEventsData>()
    private var eventTags: List<Tag> = emptyList()
    private var eventDates = mapOf<Int, List<EventScheduleDay>>()
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
                eventDates = userEventData.collectDates(userEventData.createEventScheduleDays(dates))
            }
            Maybe.just(groupedList)
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
            val days = eventDates.values.flatten()
            val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
            val other = Calendar.getInstance()
            currentDay = days.find {
                        (dateCalendar.isSameDay(other.apply {
                            timeInMillis = it.millis
                        }) || it.millis - date > 0)
            } ?: days.lastOrNull()
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = {
                    if (currentDay != null) {
                        viewState.scrollContent(currentDay!!)
                        viewState.selectDay(currentDay!!)
                    }
                }
            )
    }

    override fun onDaySelected(day: EventScheduleDay) {
        currentDay = day
        viewState.apply {
            scrollContent(day)
            selectDay(day)
        }
    }

    override fun onSubEventClick(subEvent: EventActivityModel) = viewState.showSubEvent(eventId, subEvent.id.toString())

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
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple { viewState.updateSubEvent(subEvent) }
    }


    override fun onSearchTextChange(text: String) = onSearchTextSubmit(text)
    override fun onTagSelected() = initContent()
    override fun onSearchTextSubmit(text: String) {
        searchWord = text
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
        return !tags.filter { x -> event.tag?.any { e -> e.id == x.id.toInt() } == true }.isNullOrEmpty()
    }


    private fun checkParams(
        list: List<EventActivityModel>,
        selectedTags: List<Tag>
    ): List<EventActivityModel> {
        return if (list.isNullOrEmpty()) return emptyList()
        else if (!searchWord.isNullOrBlank() && !selectedTags.isNullOrEmpty())
            list.filter { x -> isEventHasTag(x, selectedTags) && isEventHasParams(searchWord, x) }
        else if (!searchWord.isNullOrEmpty()) list.filter { x -> isEventHasParams(searchWord, x) }
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


