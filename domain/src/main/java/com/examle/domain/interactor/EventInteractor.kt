package com.examle.domain.interactor

import com.examle.domain.model.PaginationResponse
import com.examle.domain.model.event.EventModel
import com.examle.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class EventInteractor(private val repository: EventRepository) {


    suspend fun getEventsList(map: Map<String, Any>): PaginationResponse<EventModel> {
        return repository.getEventsList(map)
    }

    fun checkEventAgreement(event: EventModel): Flow<Boolean> {
        return if (event.userAgreement.isNullOrEmpty()) flowOf(false)
        else if (event.state?.agreementState == "accepted") flowOf(false)
        else flowOf(true)
    }

    fun acceptEventAgreement(event: EventModel): Flow<EventModel>{
        return repository.acceptEventAgreement(event)
            .onEach {
                if (it == "has already been taken") throw NullPointerException()
                else event.state?.agreementState = it
            }.map { event }
    }

    fun registerEvent(id: Int?): Flow<Unit> {
        return flow { emit(repository.registerEvent(id ?: 0)) }
    }

    fun cancelRegisterEvent(id: Int?): Flow<Unit> {
        return flow { emit(repository.cancelRegisterEvent(id ?: 0)) }
    }

    fun getEventById(id: Int): Flow<EventModel> {
        return repository.getEvent(id.toString(), getCurrentRegistrationBinds())
    }

    fun getEvent(event: EventModel): Flow<EventModel> {
        return repository.getEvent(event.id.toString(), getCurrentRegistrationBinds())
            .map {
                event.copy(
                    userRegistration = it.userRegistration,
                    userRegistrationState = it.userRegistrationState
                )
            }
    }

    private fun getCurrentRegistrationBinds(): String {
        return "current-user-registration," + "current-user-registration-state"
    }
}