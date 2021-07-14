package com.example.ui.event.list.my

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.EventNew
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
        val appData: AppData,
        eventData: UserEventData,
        private val eventRepository: EventRepository,
        userRepository: UserRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<MyEventsContract.View>(appData, eventData, eventRepository, userRepository, connectivity), MyEventsContract.Presenter {

    lateinit var filter: MyEventsFilter

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getEventsList(mapOf(EventNew.EVENT_LIMIT to limit, EventNew.EVENT_OFFSET to offset,
                EventNew.EVENT_BINDS to "rights", EventNew.EVENT_USER_ID to appData.getId(),
                EventNew.EVENT_USER_STATUS to when (filter) {
            MyEventsFilter.ACCEPTED, MyEventsFilter.APPROVED -> EventNew.FILTER_REGISTRATION_APPROVED
            MyEventsFilter.PENDING -> EventNew.FILTER_REGISTRATION_PENDING
            MyEventsFilter.DECLINED -> EventNew.FILTER_REGISTRATION_DECLINED
            MyEventsFilter.NONE -> EventNew.FILTER_REGISTRATION_ANY_REGISTERED
        }))
    }

    override fun onFirstViewAttach() {
        when (filter) {
            MyEventsFilter.ACCEPTED, MyEventsFilter.APPROVED -> viewState.setAcceptedHeader()
            MyEventsFilter.PENDING -> viewState.setPendingHeader()
            MyEventsFilter.DECLINED -> viewState.setDeclinedHeader()
            MyEventsFilter.NONE -> viewState.setNoFilterHeader()
        }
        super.onFirstViewAttach()
    }

    override fun onAcceptedClick() = viewState.showAccepted()
    override fun onPendingClick() = viewState.showPending()
    override fun onDeclinedClick() = viewState.showDeclined()

    private fun getFilterData(): Map<String, Any> {
        return mapOf(
                EventNew.EVENT_USER_STATUS to when (filter) {
                    MyEventsFilter.ACCEPTED, MyEventsFilter.APPROVED -> EventNew.FILTER_REGISTRATION_APPROVED
                    MyEventsFilter.PENDING -> EventNew.FILTER_REGISTRATION_PENDING
                    MyEventsFilter.DECLINED -> EventNew.FILTER_REGISTRATION_DECLINED
                    MyEventsFilter.NONE -> EventNew.FILTER_REGISTRATION_ANY_REGISTERED
                })
    }
}
