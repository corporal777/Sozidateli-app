package com.example.ui.organizations.events

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class OrganizationEventsPresenter
@Inject constructor(
    val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : EventListPresenter<OrganizationEventsContract.View>(appData, eventRepository, socket),
    OrganizationEventsContract.Presenter {

    lateinit var organizationId: String
    private var isFirstAttach = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(10) { null })

        compositeDisposable += Observable.create(pagination)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.isEmpty()) viewState.showEmptyListPlaceholder()
                else viewState.setData(it)
            }
    }

    override fun attachView(view: OrganizationEventsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else pagination.invalidate()
    }


    override fun onItemTake(position: Int) = pagination.onItemTake(position)
    override fun onRefreshRequest() = pagination.invalidate()

    override fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getOrganizationEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to limit,
                EventNew.EVENT_OFFSET to offset,
                EventNew.EVENT_BINDS to getBinds(),
                // EventNew.EVENT_SORT_TYPE to "desc",
                EventNew.EVENT_ORGANIZATION to organizationId
            )
        )
    }

    override fun getBinds(): String {
        return "current-user-registration,current-user-registration-state,eventRegistrationState"
    }

}