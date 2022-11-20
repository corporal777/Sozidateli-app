package com.example.ui.organizations.events

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.EventNew
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

@InjectViewState
class OrganizationEventsPresenter
@Inject constructor(
    appData: AppData,
    eventData: UserEventData,
    private val eventRepository: EventRepository,
    userRepository: UserRepository,
    @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<OrganizationEventsContract.View>(
    appData,
    eventData,
    eventRepository,
    userRepository,
    connectivity
), OrganizationEventsContract.Presenter {

    lateinit var organizationId: String

    override fun getPaginationRequest(
        limit: Int,
        offset: Int
    ): Maybe<PaginationResponse<EventNew?>> {
        return eventRepository.getOrganizationEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to limit,
                EventNew.EVENT_OFFSET to offset,
                EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,userFavorite",
                // EventNew.EVENT_SORT_TYPE to "desc",
                EventNew.EVENT_ORGANIZATION to organizationId
            )
        )
    }
}