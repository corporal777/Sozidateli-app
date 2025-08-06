package com.example.ui.event.list

import android.util.Log
import call
import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.data.socket.SocketIOManager
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.views.dialogs.StateType
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.flow.PagingListFlow
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withDelay
import withEventLoading

abstract class EventListPresenter<V : EventListContract.View>(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
) : BasePresenter<V>(appData), EventListContract.Presenter {

    abstract val pagination: PagingListFlow<*>
    private var sentEvent : EventNew? = null


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.eventChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple { sentEvent = it }
    }

    override fun attachView(view: V) {
        super.attachView(view)
        if (sentEvent != null){
            viewState.updateEvent(sentEvent!!)
            sentEvent = null
        }
    }

    override fun onActionRegister(event: EventNew, withAccept: Boolean, position: Int) {
        if (isProfileLevelLow(event)) viewState.showStateErrorMessage(StateType.BASE, false, null)
        else eventRepository.acceptEventAgreement(event, withAccept)
            .flatMapMaybe { registerToEventRequest(event) }
            .performOnBackgroundOutOnMain()
            .withEventLoading(viewState, position)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (event.isFormEnabled()) viewState.showEventRequest(event.id.toString())
                    else {
                        viewState.updateEvent(event)
                        viewState.showEventRegistrationSuccessDialog()
                    }
                }
            ).call(compositeDisposable)
    }

    override fun onActionCancel(event: EventNew, position: Int) {
        val registrationId = event.binds?.currentUserRegistration?.id ?: 0
        if (isProfileLevelLow(event)) viewState.showStateErrorMessage(StateType.BASE, false, null)
        else eventRepository.cancelRegisterToEvent(registrationId)
            .andThen(eventRepository.getEvent(event.id.toString()))
            .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
            .performOnBackgroundOutOnMain()
            .withEventLoading(viewState, position)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.updateEvent(event) }
            ).call(compositeDisposable)
    }


    private fun registerToEventRequest(event: EventNew): Maybe<EventNew> {
        return Maybe.defer {
            if (event.isFormEnabled()) Maybe.just(event).withDelay(500)
            else eventRepository.registerToEvent(event.id ?: 0)
                //.andThen(socket.connectToUpdates())
                .andThen(eventRepository.getEvent(event.id.toString()))
                .doOnSuccess { event.setFieldsForActionButton(it) }.map { event }
        }
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)
    override fun onShowAuthorization(event: String?) {
        appData.savedEventId = event
        viewState.showAuthorization()
    }

    override fun onRefreshRequest() = pagination.invalidate()


    abstract fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew>>
}