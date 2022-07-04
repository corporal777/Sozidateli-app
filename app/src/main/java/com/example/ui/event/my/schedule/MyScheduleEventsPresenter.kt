package com.example.ui.event.my.schedule

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.di.Connectivity
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.my.schedule.items.SortedEvents
import com.example.ui.event.my.schedule.items.SortedSubEvents
import com.example.ui.views.calendarView.CalendarDay
import com.example.util.getDaysFromDateToDate
import com.example.util.getMonthName
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


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
    private val mSubEventsDates = arrayListOf<CalendarDay>()
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
                    mFirstDate = mSubEventsList.get(0).holdingDate?.from ?: ""
                    mLastDate = mSubEventsList.last().holdingDate?.from ?: ""
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
        compositeDisposable += Completable.fromAction {
            mDays.addAll(
                userEventData.createCalendarDays(
                    getDaysFromDateToDate(
                        mFirstDate,
                        mLastDate
                    ).map {
                        defaultServerDateFormatter.parse(it).time
                    })
            )
        }.performOnBackgroundOutOnMain()
            .subscribeSimple {
                val mEventDate = defaultServerDateFormatter.parse(mFirstDate).time
                val mMonth =
                    getMonthName(mEventDate.calendar().get(Calendar.MONTH))
                val mFirstEventDate =
                    createCalendarDay(mEventDate)
                viewState.apply {
                    setHeaderAndCalendar(subEventDates, mMonth, mDays)
                    scrollToDay(mFirstEventDate)
                    setSearchBlock()
                    sortData(list)
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

        lateinit var mNearestEventDate: EventScheduleCalendarDay
        var nearestMonth = ""
        var nearestDate = 0
        compositeDisposable += Completable.fromAction {

            val list = mSubEventsList.sortedBy { x -> x.holdingDate?.from }.map {
                defaultServerDateFormatter.parse(it.holdingDate?.from).time.calendar().timeInMillis
            }

            val lower = TreeSet(list).lower(day.millis)
            val higher = TreeSet(list).higher(day.millis)

            val max = higher - day.millis
            val min = day.millis - lower


            var nearest = if (max.toInt() < min.toInt()) {
                higher
            } else {
                lower
            }
            val cal = nearest.calendar()
            nearestDate = cal.get(Calendar.DAY_OF_MONTH)
            nearestMonth =
                cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
            mNearestEventDate = EventScheduleCalendarDay(
                nearest,
                cal.get(Calendar.WEEK_OF_MONTH),
                cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                    ?: "",
                cal.get(Calendar.DAY_OF_MONTH),
                true
            )
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    val message =
                        "По данному дню нет событий. Выбрано $nearestDate $nearestMonth, ближайший день с событиями к выбранному дню."
                    showMessageDialog(message)
                }
                viewState.scrollContent(mNearestEventDate)
                viewState.selectDay(mNearestEventDate)
                compositeDisposable += Completable.fromAction {
                }
                    .withDelay(1000)
                    .performOnBackgroundOutOnMain()
                    .subscribeSimple {

                    }
            }
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
        Log.e("TEXT", mSearchText)
        updateData()
    }

    private fun updateData() {
        val listEvents = arrayListOf<SortedEvents>()
        compositeDisposable += Completable.fromAction {
            mEventsList.forEach { event ->
                var count = 0
                val list = event.binds?.activity?.filter { x -> isHasSearchText(mSearchText, x) }

                val mDate = event.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
                    //?.get(0)?.holdingDate?.from
                    ?.get(0)?.holdingDate?.from?.split(" ")?.get(0) ?: ""

                val listSubEvents = arrayListOf<SortedSubEvents>()
                val mSubEventsMap = list?.groupBy { subEvent ->
                    subEvent.holdingDate?.from?.split(" ")?.get(0) ?: ""
                }?.toSortedMap()

                Log.e("DATE", mDate?:"")
                Log.e("DATE", mSubEventsMap?.keys.toString())
                if (!mSubEventsMap.isNullOrEmpty()){
                    mSubEventsMap?.put("", mSubEventsMap.remove(mDate))
                }

                mSubEventsMap?.forEach {
                    listSubEvents.add(SortedSubEvents(it.key, it.value))
//                    if (count < 1) {
//                        listSubEvents.add(SortedSubEvents("", it.value))
//                        count++
//                    } else {
//                        listSubEvents.add(SortedSubEvents(it.key, it.value))
//                    }
                }

                val canShowPlaceholder = mSubEventsMap.isNullOrEmpty()

                val mEvent = SortedEvents(
                    mDate ?: "",
                    event?.id.toString(),
                    event?.name ?: "",
                    event?.image?.uri ?: "",
                    listSubEvents,
                    canShowPlaceholder
                )
                listEvents.add(mEvent)
            }


        }.performOnBackgroundOutOnMain()
//            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.setContentNew(listEvents)
                //viewState.setContent(eventsList)
            }
    }

//    private fun updateData() {
//        val listEvents = arrayListOf<SortedEvents>()
//        compositeDisposable += Completable.fromAction {
//            mEventsList.forEach { event ->
//                var count = 0
//                val list = event.binds?.activity?.filter { x -> isHasSearchText(mSearchText, x) }
//
//                val mDate = event.binds?.activity?.sortedBy { x -> x.holdingDate?.from }
//                    //?.get(0)?.holdingDate?.from
//                    ?.get(0)?.holdingDate?.from?.split(" ")?.get(0) ?: ""
//
//                val listSubEvents = arrayListOf<SortedSubEvents>()
//                val mSubEventsMap = list?.groupBy { subEvent ->
//                    subEvent.holdingDate?.from?.split(" ")?.get(0) ?: ""
//                }?.toSortedMap()
//
//                Log.e("DATE", mDate?:"")
//                Log.e("DATE", mSubEventsMap?.keys.toString())
//                if (!mSubEventsMap.isNullOrEmpty()){
//                    mSubEventsMap?.put("", mSubEventsMap.remove(mDate))
//                }
//
//                mSubEventsMap?.forEach {
//                    listSubEvents.add(SortedSubEvents(it.key, it.value))
////                    if (count < 1) {
////                        listSubEvents.add(SortedSubEvents("", it.value))
////                        count++
////                    } else {
////                        listSubEvents.add(SortedSubEvents(it.key, it.value))
////                    }
//                }
//
//                val canShowPlaceholder = mSubEventsMap.isNullOrEmpty()
//
//                val mEvent = SortedEvents(
//                    mDate ?: "",
//                    event?.id.toString(),
//                    event?.name ?: "",
//                    event?.image?.uri ?: "",
//                    listSubEvents,
//                    canShowPlaceholder
//                )
//                listEvents.add(mEvent)
//            }
//
//
//        }.performOnBackgroundOutOnMain()
////            .withProgressBarLoadingDialog(viewState)
//            .subscribeSimple {
//                viewState.setContentNew(listEvents)
//                //viewState.setContent(eventsList)
//            }
//    }

    override fun onSearchTextSubmit(text: String) {
        mSearchText = text
        Log.e("TEXT", mSearchText)
        updateData()
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

    private fun sortData(list: List<EventNew?>) {
        val listEvents = arrayListOf<SortedEvents>()
        list.forEach { event ->
            var count = 0
            val listSubEvents = arrayListOf<SortedSubEvents>()
            val mSubEventsMap = event?.binds?.activity?.groupBy { subEvent ->
                subEvent.holdingDate?.from?.split(" ")?.get(0) ?: ""
            }?.toSortedMap()
            mSubEventsMap?.forEach {
                if (count < 1) {
                    listSubEvents.add(SortedSubEvents("", it.value))
                    count++
                } else {
                    listSubEvents.add(SortedSubEvents(it.key, it.value))
                }
            }
            val mEvent = SortedEvents(
                mSubEventsMap?.keys?.elementAt(0) ?: "",
                event?.id.toString(),
                event?.name ?: "",
                event?.image?.uri ?: "",
                listSubEvents,
                false
            )
            listEvents.add(mEvent)
        }
        viewState.setContentNew(listEvents)
    }


}