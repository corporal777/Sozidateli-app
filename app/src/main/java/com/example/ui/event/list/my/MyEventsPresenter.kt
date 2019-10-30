package com.example.ui.event.list.my

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import timber.log.Timber
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository
) : EventListPresenter<MyEventsContract.View>(), MyEventsContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getEventList(limit, offset, mapOf(Event.FILTER_REGISTRATION to Event.FILTER_REGISTRATION_ANY_REGISTERED))
    }

    override fun onEventClick(event: Event) {
        if (isCanSetDefault(event)) {
            compositeDisposable += eventRepository.setDefaultEvent(event.id)
                    .withCheckInternetConnectivity()
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        eventData.event = event
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
}
