package com.example.data.models

data class EventUserRegistration(
        val id: String,
        val group: EventGroup?,
        val status: Event.RegistrationStatus?
)