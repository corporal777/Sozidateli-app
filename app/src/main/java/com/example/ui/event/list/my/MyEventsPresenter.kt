package com.example.ui.event.list.my

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.MyEventsFilter
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
        appData: AppData,
        eventData: UserEventData,
        private val eventRepository: EventRepository,
        userRepository: UserRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<MyEventsContract.View>(appData, eventData, eventRepository, userRepository, connectivity), MyEventsContract.Presenter {

    lateinit var filter: MyEventsFilter

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return eventRepository.getEventList(limit, offset, getFilterData())
    }

    override fun onFirstViewAttach() {
        when (filter) {
            MyEventsFilter.ACCEPTED -> viewState.setAcceptedHeader()
            MyEventsFilter.PENDING -> viewState.setPendingHeader()
            MyEventsFilter.DECLINED -> viewState.setDeclinedHeader()
            MyEventsFilter.NONE -> viewState.setNoFilterHeader()
        }
        super.onFirstViewAttach()
    }

    override fun onAcceptedClick() = viewState.showAccepted()
    override fun onPendingClick() = viewState.showPending()
    override fun onDeclinedClick() = viewState.showDeclined()

    private fun getFilterData(): Map<String, String> {
        return mapOf(Event.FILTER_REGISTRATION to when (filter) {
            MyEventsFilter.ACCEPTED -> Event.FILTER_REGISTRATION_APPROVED
            MyEventsFilter.PENDING -> Event.FILTER_REGISTRATION_PENDING
            MyEventsFilter.DECLINED -> Event.FILTER_REGISTRATION_DECLINED
            MyEventsFilter.NONE -> Event.FILTER_REGISTRATION_ANY_REGISTERED
        })
    }
}
