package com.example.ui.event.list.recommendations

import com.arellomobile.mvp.InjectViewState
import com.example.repository.EventRepository
import com.example.ui.event.list.EventListPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import javax.inject.Inject

@InjectViewState
class RecommendationsPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : EventListPresenter<RecommendationsContract.View>(), RecommendationsContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset -> eventRepository.getEventList(limit, offset) }
}
