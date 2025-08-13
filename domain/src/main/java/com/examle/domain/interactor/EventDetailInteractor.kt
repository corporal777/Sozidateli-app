package com.examle.domain.interactor

import com.examle.domain.model.event.EventModel
import com.examle.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventDetailInteractor(private val repository: EventRepository) :
    BaseEventInteractor(repository) {

    fun getEventDetail(id: String): Flow<EventModel> {
        return repository.getEventDetail(id, getCurrentRegistrationBinds())
    }

    fun addOrRemoveEventFavorite(event: EventModel): Flow<EventModel> {
        return if (event.userFavorite != null)
            repository.removeEventFromFavorites(event.userFavorite.id.toString())
                .map { event.copy(userFavorite = null) }
        else repository.addEventToFavorites(event.id.toString())
            .map { event.copy(userFavorite = it) }
    }

    private fun getCurrentRegistrationBinds(): String {
        return "current-user-registration," + "current-user-registration-state"
    }
}