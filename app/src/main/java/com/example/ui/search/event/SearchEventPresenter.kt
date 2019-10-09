package com.example.ui.search.event

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.repository.EventRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import javax.inject.Inject

@InjectViewState
class SearchEventPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : SearchPresenter<SearchEventContract.View, Event, SearchFilter.Event>(), SearchEventContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        eventRepository.getEventList(limit, offset)
    }

    override fun onEventClick(event: Event) {
        viewState.showAboutEvent(event)
    }

    override fun onGoToEventClick(event: Event) {
        viewState.showEventRequest(event)
    }

    private fun buildFilter(): Map<String, Any> = mutableMapOf<String, Any>(
            FILTER_CONTENT to searchText
    ).apply {

    }

    override fun createFilter() = SearchFilter.Event()
    override fun copyFilter(filter: SearchFilter.Event) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
    }
}