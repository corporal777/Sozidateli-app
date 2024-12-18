package com.example.ui.event.list

import call
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.PAGE_PLACEHOLDER
import com.example.util.PAGE_SIZE
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading

abstract class EventListPresenter<V : EventListContract.View>(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : BasePresenter<V>(appData), EventListContract.Presenter {

    protected val pagination = PaginationDataSourceFactory(::getPaginationRequest)
        .applyErrorHandler {
            it.printStackTrace()
            viewState.showRequestErrorMessage()
        }
        .buildList(enablePlaceholders = PAGE_PLACEHOLDER, initialSize = PAGE_SIZE)

    override fun onActionRegister(event: String, url: String?, formEnabled: Boolean) {
        if (url.isNullOrEmpty()) registerToEvent(event, formEnabled)
        else eventRepository.checkRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (it.isAccepted()) registerToEvent(event, formEnabled)
                    else viewState.showAgreementRegisterDialog(event, url, formEnabled)
                }
            ).call(compositeDisposable)
    }

    override fun onAcceptRegistrationAgreement(event: String, formEnabled: Boolean) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) registerToEvent(event, formEnabled) }
            )
    }

    private fun registerToEvent(event: String, formEnabled: Boolean) {
        if (formEnabled) viewState.showEventRequest(event)
        else eventRepository.registerToEvent(event.toInt())
            .andThen(socket.connectToUpdates())
            .andThen(eventRepository.getEvent(event, getBinds()))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        updateEvent(it)
                        showEventRegistrationSuccessDialog()
                    }
                }
            ).call(compositeDisposable)
    }

    override fun onActionCancel(event: String, registrationId: String?) {
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEvent(event, getBinds()))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.updateEvent(it) }
            )
    }


    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onShowAuthorization(event: String) {
        appData.savedEventId = event
        viewState.showAuthorization()
    }


    protected abstract fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>>

    protected abstract fun getBinds(): String
}