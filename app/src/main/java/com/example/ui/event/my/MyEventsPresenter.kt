package com.example.ui.event.my

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.*
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val commonRepository: CommonRepository,
    private val appData: AppData
) : EventListPresenter<MyEventsContract.View>(appData, eventRepository),
    MyEventsContract.Presenter {

    lateinit var mEventStateFilter: MyEventsFilter
    private var mSearchFilter = SearchFilter.EventNew()
    private var isFirstAttach = true
    private var mSearchText = ""

    private var isHasSchedules = false
    private var isCommonDataLoaded = false

    override fun attachView(view: MyEventsContract.View?) {
        super.attachView(view)
//        if (isFirstAttach) isFirstAttach = false
//        else pagination.invalidate()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        compositeDisposable += Maybe.zip(
            eventRepository.getUserCalendarEvents()
                .map { it.filter { x -> x.binds?.activity?.any { z -> z.binds?.userCalendar != null } == true } },
            commonRepository.getInterests()
                .map { interests -> interests.groupByNotNull { child -> interests.firstOrNull { it.id == child.parent } } },
            eventRepository.getEventFormatsList(
                mapOf(EventNew.EVENT_LIMIT to 100, EventNew.EVENT_OFFSET to 0)
            )
        ) { events, interests, formats ->
            mSearchFilter.interests = interests
            mSearchFilter.formats = formats
            isHasSchedules = !events.isNullOrEmpty()
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    isCommonDataLoaded = true
                    viewState.setShowMyScheduleButton(isHasSchedules)
                },
                onSuccess = {
                    isCommonDataLoaded = true
                    viewState.setShowMyScheduleButton(isHasSchedules)
                })
        getEventsData(true, SHIMMER_LOADING)
    }

    private fun getEventsData(isFirst: Boolean, loading: Int) {
        if (loading == 0) viewState.setData(List(5) { null })
        compositeDisposable += Observable.create(pagination)
            .map { transformData(it) }
            .performOnBackgroundOutOnMain()
            .let {
                if (loading == 1) it.withProgressBarDialogLoading(viewState)
                else it
            }
            .subscribeSimple(
                onError = { viewState.showEmptyListPlaceholder(isFirst) },
                onNext = { eventList ->
                    if (eventList.isEmpty()) viewState.showEmptyListPlaceholder(isFirst)
                    else viewState.setData(eventList)
                })
    }


    override fun onSearchTextChange(text: String) {
        mSearchText = text
        getEventsData(false, SHIMMER_LOADING)
    }

    override fun onSearchTextSubmit(text: String) {
        mSearchText = text
        getEventsData(false, SHIMMER_LOADING)
    }

    fun getSearchFilters() = mSearchFilter
    override fun onRefreshRequest() = pagination.invalidate()
    override fun onItemTake(position: Int) = pagination.onItemTake(position)


    override fun updateData() {
        getEventsData(false, PROGRESS_LOADING)
        viewState.setFiltersChosen(mSearchFilter.isHasFilter())
    }

    override fun setEventStateFilter(isChecked: Boolean, filter: MyEventsFilter) {
        mEventStateFilter =
            if (isChecked) filter
            else MyEventsFilter.NONE
        getEventsData(false, PROGRESS_LOADING)
    }


    override fun onShowFiltersClick() {
        if (isCommonDataLoaded) viewState.showFilters()
    }

    override fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getSortedEventsList(
            mutableMapOf<String, Any>().apply {
                put(EventNew.EVENT_LIMIT, limit)
                put(EventNew.EVENT_OFFSET, offset)
                put(
                    EventNew.EVENT_BINDS,
                    "activity,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState"
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

                if (!mSearchFilter.name.isNullOrEmpty())
                    put(EventNew.EVENT_NAME, "%" + mSearchFilter.name + "%")

                if (!mSearchText.isNullOrEmpty()) put(EventNew.EVENT_SEARCH, "%$mSearchText%")

                if (mSearchFilter.dateStart != null)
                    put(EventNew.EVENT_START_DATE, mSearchFilter.dateStart + "," + mSearchFilter.dateFinish
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


    companion object {
        const val SHIMMER_LOADING = 0
        const val PROGRESS_LOADING = 1
    }
}