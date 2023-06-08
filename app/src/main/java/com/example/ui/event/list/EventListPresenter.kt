package com.example.ui.event.list

import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.EventNew
import com.example.data.models.UserProfileFields
import com.example.di.Connectivity
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.net.UnknownHostException

abstract class EventListPresenter<V : EventListContract.View>(
    private val appData: AppData,
    private val eventRepository: EventRepository,
) : BasePresenter<V>(appData), EventListContract.Presenter {

    protected val pagination: PaginationDataSourceFactory<EventNew?> =
        PaginationDataSourceFactory(::getPaginationRequest)
    lateinit var paginationList: PaginationList<EventNew?>


    override fun onActionRegister(event: String, url: String?) {
        if (url.isNullOrEmpty()) viewState.showEventRequest(event)
        else {
            compositeDisposable += eventRepository.checkRegistrationAgreement(event)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        if (it.isAccepted()) viewState.showEventRequest(event)
                        else viewState.showAgreementRegisterDialog(event, url)
                    }
                )
        }
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

    override fun onAcceptRegistrationAgreement(event: String) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) viewState.showEventRequest(event) }
            )
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    protected abstract fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>>
}
