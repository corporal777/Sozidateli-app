package com.example.ui.event.my.schedule

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.di.Connectivity
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.findItemBy
import com.example.holders.redesign.EventActivityDateItem
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.my.schedule.items.MyScheduleEventsData
import com.example.ui.views.calendarView.CalendarDay
import com.example.util.getDaysFromDateToDate
import com.example.util.getMonthName
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import performOnBackground


@InjectViewState
class MyScheduleEventsPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository,
    @Connectivity val connectivity: Observable<Boolean>
) : BasePresenter<MyScheduleEventsContract.View>(appData), MyScheduleEventsContract.Presenter {

    private var mFirstDate = ""
    private var mLastDate = ""
    private val mSubEventsList = arrayListOf<EventActivityModel>()
    private var mSubEventsDates = arrayListOf<CalendarDay>()
    private val mEventsList = arrayListOf<EventNew>()
    private var mSearchText = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsList()
    }

    private fun getEventsList() {
        compositeDisposable += eventRepository.getUserCalendarEvents()
            .doOnSuccess {
                if (!it.isNullOrEmpty()) {
                    mEventsList.addAll(it)
                    it.forEach { subEvent ->
                        subEvent.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
                            ?.forEach { x ->
                                val cal =
                                    defaultServerDateFormatter.parse(x.holdingDate?.from).time.calendar()
                                mSubEventsDates.add(setEventCalendarDays(cal))
                                mSubEventsList.add(x)
                            }
                    }
                    mFirstDate = mSubEventsList.sortedBy { x -> x.holdingDate?.from }
                        .get(0).holdingDate?.from ?: ""
                    mLastDate = mSubEventsList.sortedBy { x -> x.holdingDate?.from }
                        .last().holdingDate?.from ?: ""
                }
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                if (it.isNullOrEmpty()) {
                } else {
                    initCalendarDays(mSubEventsDates, it)
                }
            }
    }

    private fun initCalendarDays(subEventDates: List<CalendarDay>, list: List<EventNew?>) {
        val mDays = arrayListOf<EventScheduleCalendarDay>()
        val listReadyEvents = arrayListOf<MyScheduleEventsData>()
        var firstCalendarDate: CalendarDay? = null
        var lastCalendarDate: CalendarDay? = null
        lateinit var mNearestDate: EventScheduleCalendarDay
        compositeDisposable += Completable.fromAction {
            val startCal = defaultServerDateFormatter.parse(mFirstDate).time.calendar()
            val endCal = defaultServerDateFormatter.parse(mLastDate).time.calendar()
            firstCalendarDate = CalendarDay(
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH) + 1,
                1
            )
            lastCalendarDate = CalendarDay(
                endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH) + 1,
                endCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            )

            mDays.addAll(
                userEventData.createCalendarDays(
                    getDaysFromDateToDate(
                        mFirstDate,
                        mLastDate
                    ).map {
                        defaultServerDateFormatter.parse(it).time
                    })
            )
            listReadyEvents.addAll(transformDataToShow(list))
            mNearestDate = findNearestDay(Calendar.getInstance().timeInMillis)
        }.performOnBackgroundOutOnMain()
            .subscribeSimple {
                //val mEventDate = defaultServerDateFormatter.parse(mFirstDate).time
                val mEventDate = mNearestDate.millis
                val currentYear = System.currentTimeMillis().calendar().get(Calendar.YEAR)

                val mMonth = if (currentYear == mEventDate.calendar().get(Calendar.YEAR)) {
                    getMonthName(mEventDate.calendar().get(Calendar.MONTH))
                } else {
                    getMonthName(
                        mEventDate.calendar().get(Calendar.MONTH)
                    ) + " " + mEventDate.calendar().get(Calendar.YEAR)
                }
//                val mMonth =
//                    getMonthName(mEventDate.calendar().get(Calendar.MONTH))

                val mFirstEventDate =
                    createCalendarDay(mEventDate)
                viewState.apply {
                    setHeaderAndCalendar(
                        subEventDates,
                        mMonth,
                        mDays,
                        firstCalendarDate,
                        lastCalendarDate
                    )
                    setContentNew(listReadyEvents)
                    scrollToDay(mFirstEventDate)
                    scrollContent(mNearestDate)
                    //setContent(list)
                }

            }
    }


    override fun onDaySelected(day: EventScheduleCalendarDay) {
        viewState.apply {
            scrollContent(day)
            selectDay(day)
        }
    }

    override fun findNearestEventDay(day: EventScheduleCalendarDay) {
    }

    override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
    }

    override fun onAddSubEventToScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.addEventToCalendarWithResult(
                EventCalendarBody(
                    appData.getId(),
                    EventCalendarBodyEntity(
                        EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id
                            ?: 0
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

    override fun onRemoveSubEventFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    override fun onSearchTextChange(text: String) {
        mSearchText = text
        updateData(text)
    }

    private fun updateData(text: String) {
        val listEvents = arrayListOf<MyScheduleEventsData>()
        compositeDisposable += Completable.fromAction {
            mEventsList.forEach { event ->
                val list = event.binds?.activity?.filter { x -> isHasSearchText(text, x) }
                val mDate = event.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
                    ?.get(0)?.holdingDate?.from?.split(" ")?.get(0) ?: ""
                val mSubEventsMap = list?.groupBy { subEvent ->
                    subEvent.holdingDate?.from?.split(" ")?.get(0) ?: ""
                }?.toSortedMap()

                if (!mSubEventsMap.isNullOrEmpty()) {
                    mSubEventsMap.put("", mSubEventsMap.remove(mDate))
                }
                val canShowPlaceholder = mSubEventsMap.isNullOrEmpty()
                listEvents.add(
                    MyScheduleEventsData(
                        mDate,
                        event.id.toString(),
                        event.name ?: "",
                        event.image?.uri ?: "",
                        mSubEventsMap ?: emptyMap(),
                        canShowPlaceholder
                    )
                )
            }
        }.performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setContentNew(listEvents)
                //viewState.setContent(eventsList)
            }
    }

    override fun onSearchTextSubmit(text: String) {
        mSearchText = text
        updateData(text)
    }

    override fun onShowEventClick(eventId: String) {
        viewState.showAboutEvent(eventId)
    }

    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.updateSubEvent(subEvent)
            }
    }

    fun createCalendarDay(date: Long): EventScheduleCalendarDay {
        val cal = date.calendar()
        return EventScheduleCalendarDay(
            date,
            cal.get(Calendar.WEEK_OF_MONTH),
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ?: "",
            cal.get(Calendar.DAY_OF_MONTH),
            true
        )
    }

    private fun isHasSearchText(text: String, event: EventActivityModel): Boolean {
        var isHas = false
        if (event.description?.contains(text, true) == true) {
            isHas = true
        }
        return isHas
    }

    private fun setEventCalendarDays(cal: Calendar): CalendarDay {
        return CalendarDay(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun transformDataToShow(list: List<EventNew?>): List<MyScheduleEventsData> {
        val listScheduleEvents = arrayListOf<MyScheduleEventsData>()

        list.forEach { event ->
            val firstDate = event?.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
                ?.get(0)?.holdingDate?.from?.split(" ")?.get(0) ?: ""

            val eventsMap = event?.binds?.activity?.groupBy { x ->
                x.holdingDate?.from?.split(" ")?.get(0) ?: ""
            }?.toSortedMap()

            if (!eventsMap.isNullOrEmpty()) {
                eventsMap.put("", eventsMap.remove(firstDate))
            }
            listScheduleEvents.add(
                MyScheduleEventsData(
                    firstDate,
                    event?.id.toString(),
                    event?.name ?: "",
                    event?.image?.uri ?: "",
                    eventsMap ?: emptyMap(),
                    false
                )
            )
        }
        return listScheduleEvents
    }

    private fun findNearestDay(today: Long): EventScheduleCalendarDay {
        val eventDate = mSubEventsList.sortedBy { x -> x.holdingDate?.from }.find { event ->
            val date = defaultServerDateFormatter.parse(event.holdingDate?.from).time
            date == today || date - today > 0
        }?.holdingDate?.from?.split(" ")?.get(0) ?: ""

        Log.e("NEAR DATE", eventDate?:"")
        return createCalendarDay(defaultServerDateFormatter.parse(eventDate).time.calendar().timeInMillis)
    }

}