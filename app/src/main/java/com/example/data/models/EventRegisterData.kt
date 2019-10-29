package com.example.data.models

data class EventRegisterData(
        val event: EventRegistration,
        val groupField: EventRegisterField?,
        val selectedGroup: String?,
        val groups: List<EventGroup>,
        val fieldsData: List<EventRegisterFieldData<*>>
)