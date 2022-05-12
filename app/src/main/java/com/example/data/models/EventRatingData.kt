package com.example.data.models

data class EventRatingData(
        val event: EventData,
        val fieldsData: List<EventRegisterFieldData<*>>,
        val created: String? = null,
        val ratingValue: Int? = 0
)