package com.example.ui.organizations.events

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildFlow
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
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
class OrganizationEventsPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : EventListPresenter<OrganizationEventsContract.View>(appData, eventRepository, socket),
    OrganizationEventsContract.Presenter {

    lateinit var organizationId: String

    override val pagination = PagingDataSourceFactory { limit, offset ->
        getPaginationRequest(limit, offset)
    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 20, distance = 3)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it, isTemporaryUser()) })
    }


    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew>> {
        return eventRepository.getOrganizationEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to limit,
                EventNew.EVENT_OFFSET to offset,
                EventNew.EVENT_BINDS to "current-user-registration,current-user-registration-state",
                // EventNew.EVENT_SORT_TYPE to "desc",
                EventNew.EVENT_ORGANIZATION to organizationId
            )
        )
    }
}