package com.example.data.models

data class OrganizationData(
        val organization: Organization,
        val events: List<Event>,
        val totalEvents: Int
)