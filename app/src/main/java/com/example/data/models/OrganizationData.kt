package com.example.data.models

data class OrganizationData(
        val organization: Organization,
        val members: List<OrganizationMember>,
        val events: List<Event>
)