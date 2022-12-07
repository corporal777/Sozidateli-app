package com.example.ui.event.my

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.*
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.extensions.groupByNotNull
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class MyEventsPresenterNew
@Inject constructor(
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository,
    private val appData: AppData,
    @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<MyEventsContractNew.View>(appData), MyEventsContractNew.Presenter {

    lateinit var mEventStateFilter: MyEventsFilter
    private var mSearchFilter = SearchFilter.EventNew()
    private var isFirstAttach = true
    private var isFirstLaunch = true
    private var mSearchText = ""

    private var isCommonDataLoaded = false

    private val pagination: PaginationDataSourceFactory<EventNew?> =
        PaginationDataSourceFactory(::getPaginationRequest)
    private lateinit var paginationList: PaginationList<EventNew?>

    override fun attachView(view: MyEventsContractNew.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else getEventsData(true, SHIMMER_LOADING)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventsData(true, SHIMMER_LOADING)
        getFiltersData()
        compositeDisposable += eventRepository.getUserCalendarEvents()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val list =
                    it?.filter { x -> x.binds?.activity?.any { z -> z.binds?.userCalendar != null } == true }
                viewState.setShowMyScheduleButton(!list.isNullOrEmpty())
            }
    }

    private fun getEventsData(isFirst : Boolean, loading : Int) {
        if (loading == 0) viewState.setData(List(5) { null })
        paginationList = pagination.applyErrorHandler {
            if (it.cause is UnknownHostException)
                hasNoConnectionError = true
        }
            .buildList(enablePlaceholders = false)

        compositeDisposable += Observable.create(paginationList)
            .performOnBackgroundOutOnMain()
            .let {
                if (loading == 1) it.withCustomProgressBarLoadingDialog(viewState)
                else it
            }
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.showEmptyListPlaceholder(isFirst)
                },
                onNext = { eventList ->
                    if (eventList.isEmpty()) {
                        viewState.showEmptyListPlaceholder(isFirst)
                    } else {
                        Log.e("EventList size: ", eventList.size.toString())
                        viewState.apply {
                            setData(eventList)
                        }
                    }

                })

        compositeDisposable += connectivity
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (hasNoConnectionError && it) {
                    hasNoConnectionError = false
                    paginationList.invalidate()
                }
            }
    }

    private fun getFiltersData() {
        val loadInterests = userRepository.getInterestsList(null)
            .map { interests ->
                interests.data.groupByNotNull { child -> interests.data.firstOrNull { it.id == child.parent } }
            }
        compositeDisposable += Maybe.zip(loadInterests,
            eventRepository.getEventFormatsList(
                mapOf(
                    EventNew.EVENT_LIMIT to 100,
                    EventNew.EVENT_OFFSET to 0
                )
            ),
            BiFunction<Map<InterestNew, List<InterestNew>>, List<NewEventFormat>, Unit> { interests, formats ->
                mSearchFilter.interests = interests
                mSearchFilter.formats = formats
            })
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    isCommonDataLoaded = true
                    onReceiveError(it)
                },
                onSuccess = {
                    isCommonDataLoaded = true
                })
    }

    fun getSearchFilters(): SearchFilter.EventNew {
        return mSearchFilter
    }

    override fun onSearchTextChange(text: String) {
        mSearchText = text
        getEventsData(false, SHIMMER_LOADING)
        //paginationList.invalidate()
    }

    override fun onSearchTextSubmit(text: String) {
        mSearchText = text
        getEventsData(false, SHIMMER_LOADING)
        //paginationList.invalidate()
    }

    override fun onRefreshRequest() {
        paginationList.invalidate()
    }

    override fun updateData() {
        getEventsData(false, PROGRESS_LOADING)
    }

    override fun setEventStateFilter(isChecked: Boolean, filter: MyEventsFilter) {
        if (isChecked) {
            this.mEventStateFilter = filter
        } else {
            this.mEventStateFilter = MyEventsFilter.NONE
        }
        getEventsData(false, PROGRESS_LOADING)
    }

    override fun onActionRegister(event: String) {
        viewState.showEventRequest(event)
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEventDetails(event))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                paginationList.invalidate()
            }
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    override fun onShowFiltersClick() {
        if (isCommonDataLoaded) {
            viewState.showFilters()
        }
    }

    private fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getSortedEventsList(
            mutableMapOf<String, Any>().apply {
                put(EventNew.EVENT_LIMIT, 30)
                put(EventNew.EVENT_OFFSET, offset)
                put(
                    EventNew.EVENT_BINDS,
                    //"rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState"
                    "activity,user-registration,user-form-result,current-user-registration,eventRegistrationState"
                )
                put(EventNew.EVENT_USER_ID, appData.getId())
                //put(EventNew.EVENT_SORT_TYPE, "desc")
                //put(EventNew.EVENT_SORT_FIELD, "id")
                put(
                    EventNew.EVENT_STATUS,
                    "cancelled,registration,registrationFinished,running,finished"
                )
                put(
                    EventNew.EVENT_USER_STATUS, when (mEventStateFilter) {
                        MyEventsFilter.ACCEPTED, MyEventsFilter.APPROVED -> EventNew.FILTER_REGISTRATION_APPROVED
                        MyEventsFilter.PENDING -> EventNew.FILTER_REGISTRATION_PENDING
                        MyEventsFilter.DECLINED -> EventNew.FILTER_REGISTRATION_DECLINED
                        MyEventsFilter.NONE -> EventNew.FILTER_REGISTRATION_ANY_REGISTERED
                    }
                )
                if (!mSearchText.isNullOrEmpty()) put(EventNew.EVENT_SEARCH, "%$mSearchText%")
                if (!mSearchFilter.name.isNullOrEmpty()) put(
                    EventNew.EVENT_NAME,
                    "%" + mSearchFilter.name + "%"
                )
                if (mSearchFilter.dateStart != null) put(
                    EventNew.EVENT_START_DATE, /*"%"+*/
                    mSearchFilter.dateStart + "," + mSearchFilter.dateFinish/*+"%"*/
                )
                if (mSearchFilter.format != null) put(EventNew.EVENT_FORMAT, mSearchFilter.format!!)
                if (!mSearchFilter.address.isNullOrEmpty() || mSearchFilter.fullAddress != null) {
                    if (mSearchFilter.fullAddress != null) {
                        if (mSearchFilter.fullAddress?.country != null) put(
                            EventNew.EVENT_ADDRESS_COUNTRY,
                            mSearchFilter.fullAddress?.country!!
                        )
                        if (mSearchFilter.fullAddress?.city != null) put(
                            EventNew.EVENT_ADDRESS_CITY,
                            mSearchFilter.fullAddress?.city!!
                        )
                        if (mSearchFilter.fullAddress?.region != null) put(
                            EventNew.EVENT_ADDRESS_REGION,
                            mSearchFilter.fullAddress?.region!!
                        )
                        if (mSearchFilter.fullAddress?.street != null) put(
                            EventNew.EVENT_ADDRESS_STREET,
                            mSearchFilter.fullAddress?.street!!
                        )

                    } else {

                    }
                }
                val category = mSearchFilter.spec ?: mSearchFilter.theme
                if (category != null) put(EventNew.EVENT_CATEGORY, category)
            }


        )

    }

    private fun <T> Observable<T>.setLoading(isFirst: Boolean): Observable<T> {
        return if (isFirst) withProgressBarLoadingDialog(viewState)
        else withCustomProgressBarLoadingDialog(viewState)
    }

    override fun onItemTake(position: Int) = paginationList.onItemTake(position)

    companion object {
        const val SHIMMER_LOADING = 0
        const val PROGRESS_LOADING = 1
    }
}