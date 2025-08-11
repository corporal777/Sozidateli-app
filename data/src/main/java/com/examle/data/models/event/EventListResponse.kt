package com.examle.data.models.event

data class EventListResponse(
    val totalCount: Int,
    val data: List<EventResponse>
)