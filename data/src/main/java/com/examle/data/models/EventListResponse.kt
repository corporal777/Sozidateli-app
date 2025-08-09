package com.examle.data.models

data class EventListResponse(
    val totalCount: Int,
    val data: List<EventResponse>
)