package com.example.ui.event.list

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import java.net.UnknownHostException

abstract class EventListPresenter<V : EventListContract.View>(
    private val appData: AppData,
    private val eventRepository: EventRepository,
) : BasePresenter<V>(appData), EventListContract.Presenter {

    protected val pagination = PaginationDataSourceFactory(::getPaginationRequest)
        .applyErrorHandler { if (it.cause is UnknownHostException) hasNoConnectionError = true }
        .buildList(enablePlaceholders = false, initialSize = 30)

    var eventsList = mutableListOf<EventNew?>()

    override fun onActionRegister(event: String, url: String?) {
        if (url.isNullOrEmpty()) viewState.showEventRequest(event)
        else {
            compositeDisposable += eventRepository.checkRegistrationAgreement(event)
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
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
        val binds =
            "userFavorite,user-registration,current-user-registration,current-user-registration-state,eventRegistrationState,format"
        compositeDisposable += eventRepository.cancelRegisterToEvent(registrationId?.toInt() ?: 0)
            .andThen(eventRepository.getEvent(event, binds))
            .doOnSuccess {
                val item = eventsList.find { x -> x?.id == it.id }
                if (item != null) eventsList[eventsList.indexOf(item)] = it
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.updateEvent(it)
                //paginationList.invalidate()
            }
    }

    override fun onAcceptRegistrationAgreement(event: String) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(event)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { if (it.isAccepted()) viewState.showEventRequest(event) }
            )
    }

    override fun onShowEventClick(event: String) = viewState.showAboutEvent(event)

    protected fun transformData(list: List<EventNew?>): MutableList<EventNew?> {
        eventsList = list.toMutableList()
        return eventsList
    }


    protected abstract fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>>
}
