package com.example.ui.event.list

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.flow.PagingListFlow
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class EventListPresenterNew<V : EventListContractNew.View>(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : BasePresenter<V>(appData), EventListContractNew.Presenter {

    abstract val pagination: PagingListFlow<*>

    override fun onActionRegister(event: EventNew, withAccept: Boolean) {
        compositeDisposable += Single.defer {
            if (withAccept) acceptRegistrationAgreementRequest(event)
            else Single.just(event)
        }
            .flatMapMaybe { registerToEventRequest(event) }
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
            .andThen(eventRepository.getEventDetails(event.id.toString()))
            .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
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


    private fun acceptRegistrationAgreementRequest(event: EventNew): Single<EventNew> {
        return eventRepository.acceptRegistrationAgreement(event.id.toString())
            .doOnSuccess { if (it.isAccepted()) event.state?.agreement?.setAccepted() }
            .map { event }
    }

    private fun registerToEventRequest(event: EventNew): Maybe<EventNew> {
        return Maybe.defer {
            if (event.isFormEnabled()) Maybe.just(event)
            else eventRepository.registerToEvent(event.id ?: 0)
                .andThen(socket.connectToUpdates())
                .andThen(eventRepository.getEventDetails(event.id.toString()))
                .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
        }
    }


    abstract fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew>>

}