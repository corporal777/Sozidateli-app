package com.examle.domain.repository

import com.examle.domain.model.EventModel
import com.examle.domain.model.PaginationResponse
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    suspend fun getEventsList(map: Map<String, Any>): PaginationResponse<EventModel>

    suspend fun registerEvent(eventId : Int)
    suspend fun cancelRegisterEvent(eventId : Int)

    suspend fun checkEventAgreement(event : EventModel, withAccept : Boolean) : Flow<EventModel>

}