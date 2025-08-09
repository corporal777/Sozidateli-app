package com.examle.domain.interactor

import com.examle.domain.model.EventModel
import com.examle.domain.model.PaginationResponse
import com.examle.domain.repository.EventRepository

class EventInteractor(private val repository: EventRepository) {


    suspend fun getEventsList(map : Map<String, Any>): PaginationResponse<EventModel> {
        return repository.getEventsList(map)
    }
}