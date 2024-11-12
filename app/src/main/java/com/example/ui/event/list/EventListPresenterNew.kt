package com.example.ui.event.list

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.PAGE_PLACEHOLDER
import com.example.util.PAGE_SIZE
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.flow.PagingDataSourceFactory
import com.example.util.pagination.flow.PagingList
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class EventListPresenterNew<V : EventListContractNew.View>(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : BasePresenter<V>(appData), EventListContractNew.Presenter {

    private var isFirstAttach = true
    abstract val pagination: PagingList<*>

    override fun attachView(view: V) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else viewState.invalidatePagingData()
    }


    override fun onActionRegister(event: EventNew, withRegister: Boolean) {
        if (withRegister) registerToEvent(event)
        else if (event.userAgreement?.uri.isNullOrEmpty()) registerToEvent(event)
        else if (event.state?.isAgreementAccepted() == true) registerToEvent(event)
        else viewState.showAgreementRegisterDialog(event)
    }

    private fun registerToEvent(event: EventNew) {
        compositeDisposable += Maybe.defer {
            if (event.isFormEnabled()) Maybe.just(event)
            else eventRepository.registerToEvent(event.id ?: 0)
                .andThen(socket.connectToUpdates())
                .andThen(getEventDetailRequest(event))
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateEvent(event)
                },
                onSuccess = {
                    viewState.updateEvent(event)
                    if (event.isFormEnabled()) viewState.showEventRequest(event.id.toString())
                    else viewState.showEventRegistrationSuccessDialog()
                }
            )
    }

    override fun onActionCancel(event: EventNew) {
        val registrationId = event.binds?.currentUserRegistration?.id ?: 0
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(getEventDetailRequest(event))
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateEvent(event)
                },
                onSuccess = { viewState.updateEvent(event) }
            )
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onShowAuthorization(event: String?) {
        appData.savedEventId = event
        viewState.showAuthorization()
    }

    override fun onRefreshRequest() = pagination.invalidate()

    private fun getEventDetailRequest(event: EventNew): Maybe<EventNew> {
        return eventRepository.getEventDetails(event.id.toString())
            .doOnSuccess {
                event.binds?.currentUserRegistration = it.binds?.currentUserRegistration
                event.binds?.currentUserRegistrationState = it.binds?.currentUserRegistrationState
            }
    }


}