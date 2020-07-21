package com.example.ui.event.list.recommendations

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
        appData: AppData,
        eventData: UserEventData,
        private val eventRepository: EventRepository,
        userRepository: UserRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<RecommendationsContract.View>(appData, eventData, eventRepository, userRepository, connectivity), RecommendationsContract.Presenter {

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return eventRepository.getEventRecommendations(limit, offset)
    }

    override fun onSearchClick() = viewState.showSearch()

    override fun onOrganizationsClick() = viewState.showOrganizations()

    override fun onMyEventsClick() = viewState.showMyEvents()

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onMenuNotificationsClick() = viewState.showNotifications()
}