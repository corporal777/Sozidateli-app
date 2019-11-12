package com.example.ui.event.list.my

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<MyEventsContract.View>(connectivity), MyEventsContract.Presenter {

    override fun onEventClick(event: Event) {
        if (isCanSetDefault(event)) {
            compositeDisposable += eventRepository.setDefaultEvent(event.id)
                    .andThen(eventData.load(event.id))
                    .withCheckInternetConnectivity()
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        viewState.selectEvent(event)
                    }, {
                        it.printStackTrace()
                    })
        } else {
            super.onEventClick(event)
        }
    }

    private fun isCanSetDefault(event: Event): Boolean {
        return event.userRegistration == Event.RegistrationStatus.APPROVED
    }

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return eventRepository.getEventList(limit, offset, mapOf(Event.FILTER_REGISTRATION to Event.FILTER_REGISTRATION_ANY_REGISTERED))
    }
}
