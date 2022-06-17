package com.example.ui.event.my.schedule

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.EventNew
import com.example.data.models.EventScheduleCalendarDay
import com.example.data.models.MyEventsFilter
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.getDaysFromMondayToSunday
import com.example.util.getMonthName
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@InjectViewState
class MyScheduleEventsPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository,
    @Connectivity val connectivity: Observable<Boolean>
) : BasePresenter<MyScheduleEventsContract.View>(appData), MyScheduleEventsContract.Presenter {


    private var mDate = ""
    private var mFirstDate = ""

    private val pagination: PaginationDataSourceFactory<EventNew?> =
        PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsList()
    }


    private fun getEventsList() {
        viewState.setContent(List(20) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
            .buildList(enablePlaceholders = false)

        compositeDisposable += Observable.create(paginationList)
            .withProgressBarLoadingDialog(viewState)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty())
                else {
                    mDate = it[0]?.holdingDate?.from ?: ""
                    mFirstDate = it[0]?.binds?.activity?.get(0)?.holdingDate?.from ?: ""
                    initCalendarDays()
                    compositeDisposable += eventRepository.getEventFormatsList(
                        mapOf(
                            EventNew.EVENT_LIMIT to 100,
                            EventNew.EVENT_OFFSET to 0
                        )
                    )
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(
                            onError = { error ->
                                viewState.setContent(it)
                            },
                            onSuccess = { formats ->
                                it.forEach { event ->
                                    event?.format?.name =
                                        formats?.firstOrNull { f -> f.id == event?.format?.value }?.name
                                }
                                viewState.setContent(it.filterNotNull())
                            }
                        )
                }
            }

        compositeDisposable += connectivity
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (hasNoConnectionError && it) {
                    hasNoConnectionError = false
                    paginationList.invalidate()
                }
            }
    }

    private fun getDays(): List<String> {
        //return getDaysFromMondayToSunday(mDate, "2022-07-07")
        return getDaysFromMondayToSunday("2022-07-07", "2022-09-05")
    }


    private fun initCalendarDays() {
        val mEventDate = defaultServerDateFormatter.parse(mDate).time
        val mMonth =
            getMonthName(mEventDate.calendar().get(Calendar.MONTH))
        val mDays = userEventData.createCalendarDays(getDays().map {
            defaultServerDateFormatter.parse(it).time
        })
        val mFirstEventDate = getFirstEventDate(defaultServerDateFormatter.parse(mFirstDate).time)
        viewState.apply {
            setHeaderAndCalendar(mMonth, mDays)
            scrollToDay(mFirstEventDate)
            setSearchBlock()
        }

    }


    override fun onSearchTextChange(text: String) {
    }

    override fun onSearchTextSubmit(text: String) {
    }

    override fun onItemTake(position: Int) = paginationList.onItemTake(position)

    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        Log.e("EventsList", "limit: $limit ,offset: $offset")
        return eventRepository.getEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to limit,
                EventNew.EVENT_OFFSET to offset,
                EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState",
                EventNew.EVENT_HIDDEN to false,
                EventNew.EVENT_STATUS to "registration,running,registrationFinished,approved"
            )
        )
    }

    private fun getFirstEventDate(date: Long): EventScheduleCalendarDay {
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

}