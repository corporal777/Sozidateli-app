package com.example.ui.event.list.favorite

import com.arellomobile.mvp.InjectViewState
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
class FavoriteEventsPresenter
@Inject constructor(
        eventData: UserEventData,
        private val eventRepository: EventRepository,
        userRepository: UserRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<FavoriteEventsContract.View>(eventData, eventRepository, userRepository, connectivity), FavoriteEventsContract.Presenter {

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return eventRepository.getFavoriteEvents(limit, offset)
    }
}