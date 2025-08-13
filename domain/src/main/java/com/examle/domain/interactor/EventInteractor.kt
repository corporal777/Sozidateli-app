package com.examle.domain.interactor

import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventModel
import com.examle.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class EventInteractor(private val repository: EventRepository) : BaseEventInteractor(repository) {


    suspend fun getEventsList(map: Map<String, Any>): PaginationResponse<EventModel> {
        return repository.getEventsList(map)
    }

    fun getEvent(event: EventModel): Flow<EventModel> {
        return repository.getEvent(event.id.toString(), getCurrentRegistrationBinds())
            .map {
                event.copy(
                    actionStatus = it.actionStatus,
                    userRegistration = it.userRegistration,
                    userRegistrationState = it.userRegistrationState
                )
            }
    }

    private fun getCurrentRegistrationBinds(): String {
        return "current-user-registration," + "current-user-registration-state"
    }
}