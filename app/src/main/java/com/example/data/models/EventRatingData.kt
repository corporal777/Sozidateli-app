package com.example.data.models

data class EventRatingData(
        val event: EventData,
        val fieldsData: List<EventRegisterFieldData<*>>
)