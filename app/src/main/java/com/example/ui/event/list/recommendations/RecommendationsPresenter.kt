package com.example.ui.event.list.recommendations

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        @Connectivity connectivity: Observable<Boolean>
) : EventListPresenter<RecommendationsContract.View>(connectivity), RecommendationsContract.Presenter {

    override fun getPaginationRequest(limit: Int, offset: Int): Maybe<PaginationResponse<Event?>> {
        return eventRepository.getEventRecommendations(limit, offset)
    }
}