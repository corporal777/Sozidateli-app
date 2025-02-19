package com.example.ui.event.my

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.models.EventNew.Companion.EVENT_BINDS
import com.example.data.models.EventNew.Companion.EVENT_LIMIT
import com.example.data.models.EventNew.Companion.EVENT_OFFSET
import com.example.data.models.EventNew.Companion.EVENT_STATUS
import com.example.data.models.EventNew.Companion.EVENT_USER_ID
import com.example.data.models.MyEventsFilter
import com.example.data.models.SearchFilter
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenterNew
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
    private val appData: AppData
) : EventListPresenterNew<MyEventsContract.View>(appData, eventRepository, socket),
    MyEventsContract.Presenter {

    private var eventStateFilter: MyEventsFilter = MyEventsFilter.NONE
    private var searchText = ""
    private var searchFilter = SearchFilter.EventNew()

    override val pagination = PagingDataSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 20, distance = 2)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += getPaginationRequest(1, 0)
            .performOnBackgroundOutOnMain()
            .subscribeSimple { viewState.setShowScheduleEvents(!it.isEmptyData()) }

        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) })
    }

    override fun onSearchTextChange(text: String) {
        if (searchText == text) return
        searchText = text
        pagination.invalidate()
    }

    override fun onApplyFiltersClick(filter: SearchFilter.EventNew) {
        searchFilter = filter
        viewState.setFiltersChosen(searchFilter.isHasFilter())
        pagination.invalidate()
    }

    override fun onEventStateClick(isChecked: Boolean, filter: MyEventsFilter) {
        eventStateFilter = if (isChecked) filter else MyEventsFilter.NONE
        pagination.invalidate()
    }

    override fun onShowFiltersClick() = viewState.showFilters(searchFilter)

    fun isHasSearchParam(): Boolean {
        return searchText.isNotEmpty() || searchFilter.isHasFilter() || eventStateFilter != MyEventsFilter.NONE
    }

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew>> {
        return eventRepository.getSortedEventsList(
            mutableMapOf<String, Any>().apply {
                put(EVENT_LIMIT, limit)
                put(EVENT_OFFSET, offset)
                put(
                    EVENT_BINDS,
                    "activity,current-user-registration,current-user-registration-state,eventRegistrationState"
                )
                put(EVENT_USER_ID, appData.getId())
                put(EVENT_STATUS, EventNew.EVENT_STATUS_ALL)

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
                }
                val category = searchFilter.spec ?: searchFilter.theme
                if (category != null) put(EventNew.EVENT_CATEGORY, category)
            }
        )
    }
}