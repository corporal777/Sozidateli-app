package com.examle.domain.repository

import com.examle.domain.model.Optional
import com.examle.domain.model.event.EventModel
import com.examle.domain.model.PaginationResponse
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    suspend fun getEventsList(map: Map<String, Any>): PaginationResponse<EventModel>

    suspend fun registerEvent(eventId : Int)
    suspend fun cancelRegisterEvent(eventId : Int)

    fun acceptEventAgreement(event : EventModel) : Flow<String>

    fun getEvent(eventId : String, binds : String) : Flow<EventModel>
}