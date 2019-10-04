package com.example.ui.event.list.my

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository
) : EventListPresenter<MyEventsContract.View>(), MyEventsContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset -> eventRepository.getEventRegisterList(limit, offset) }

    override fun onEventClick(event: Event) {
        if (isCanSetDefault(event)) {
            eventRepository.setDefaultEvent(event.id)
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        eventData.event = event
                        viewState.selectEvent(event)
                    }, {
                        it.printStackTrace()
                    }).call(compositeDisposable)
        } else {
            super.onEventClick(event)
        }
    }

    private fun isCanSetDefault(event: Event): Boolean {
        return event.status == Event.Status.APPROVED
    }
}
