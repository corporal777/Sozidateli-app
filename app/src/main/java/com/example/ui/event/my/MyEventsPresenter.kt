package com.example.ui.event.my

import android.util.Log
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.data.models.SearchFilter
import com.example.data.socket.SocketIOManager
import com.example.extensions.groupByNotNull
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.ui.search.event.SearchEventPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
    private val appData: AppData
) : EventListPresenter<MyEventsContract.View>(appData, eventRepository, socket),
    MyEventsContract.Presenter {

    private var eventStateFilter: MyEventsFilter = MyEventsFilter.NONE
    private var searchText = ""
    private var firstLaunch = true
    var searchFilter = SearchFilter.EventNew()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getUserCalendarEvents()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setShowScheduleEvents(!it.isNullOrEmpty())
            }
        getEventsData(true)
    }

    override fun attachView(view: MyEventsContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    private fun getEventsData(isFirst: Boolean) {
        viewState.setData(List(5) { null })
        compositeDisposable += Observable.create(pagination)
            .map { transformData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.showEmptyListPlaceholder(isFirst) },
                onNext = { eventList ->
                    if (eventList.isEmpty()) viewState.showEmptyListPlaceholder(isFirst)
                    else viewState.setData(eventList)
                })
    }


    override fun onSearchTextChange(text: String) {
        if (searchText == text) return
        searchText = text
        getEventsData(false)
    }

    override fun onSearchFiltersClick(filter: SearchFilter.EventNew) {
        searchFilter = filter
        getEventsData(false)
        viewState.setFiltersChosen(searchFilter.isHasFilter())
    }

    override fun onEventStateFiltersClick(isChecked: Boolean, filter: MyEventsFilter) {
        eventStateFilter = if (isChecked) filter else MyEventsFilter.NONE
        getEventsData(false)
    }

    override fun onShowFiltersClick() = viewState.showFilters()
    override fun onRefreshRequest() = pagination.invalidate()
    override fun onItemTake(position: Int) = pagination.onItemTake(position)

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getSortedEventsList(
            mutableMapOf<String, Any>().apply {
                put(EventNew.EVENT_LIMIT, limit)
                put(EventNew.EVENT_OFFSET, offset)
                put(EventNew.EVENT_BINDS, getBinds())
                put(EventNew.EVENT_USER_ID, appData.getId())
                put(
                    EventNew.EVENT_STATUS,
                    "cancelled,registration,registrationFinished,running,finished"
                )

                put(
                    EventNew.EVENT_USER_STATUS, when (eventStateFilter) {
                        MyEventsFilter.ACCEPTED, MyEventsFilter.APPROVED -> EventNew.FILTER_REGISTRATION_APPROVED
                        MyEventsFilter.PENDING -> EventNew.FILTER_REGISTRATION_PENDING
                        MyEventsFilter.DECLINED -> EventNew.FILTER_REGISTRATION_DECLINED
                        MyEventsFilter.NONE -> EventNew.FILTER_REGISTRATION_ANY_REGISTERED
                    }
                )

                if (!searchFilter.name.isNullOrEmpty())
                    put(EventNew.EVENT_NAME, "%" + searchFilter.name + "%")

                if (!searchText.isNullOrEmpty()) put(EventNew.EVENT_SEARCH, "%$searchText%")

                if (searchFilter.dateStart != null)
                    put(
                        EventNew.EVENT_START_DATE,
                        searchFilter.dateStart + "," + searchFilter.dateFinish
                    )

                if (searchFilter.format != null) put(EventNew.EVENT_FORMAT, searchFilter.format!!)

                if (!searchFilter.address.isNullOrEmpty() || searchFilter.fullAddress != null) {
                    if (searchFilter.fullAddress != null) {
                        if (searchFilter.fullAddress?.country != null) put(
                            EventNew.EVENT_ADDRESS_COUNTRY,
                            searchFilter.fullAddress?.country!!
                        )
                        if (searchFilter.fullAddress?.city != null) put(
                            EventNew.EVENT_ADDRESS_CITY,
                            searchFilter.fullAddress?.city!!
                        )
                        if (searchFilter.fullAddress?.region != null) put(
                            EventNew.EVENT_ADDRESS_REGION,
                            searchFilter.fullAddress?.region!!
                        )
                        if (searchFilter.fullAddress?.street != null) put(
                            EventNew.EVENT_ADDRESS_STREET,
                            searchFilter.fullAddress?.street!!
                        )

                    } else {

                    }
                }
                val category = searchFilter.spec ?: searchFilter.theme
                if (category != null) put(EventNew.EVENT_CATEGORY, category)
            }
        )
    }

    override fun getBinds(): String {
        return "activity,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState"
    }
}