package com.example.data.models

import com.example.data.models.EventNew
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNew

data class AboutOrganizationData(
    val organization: OrganizationNew,
    val events: List<EventNew>?,
    val member: List<OrganizationMemberModel>?,
    val membersSize: Int? = 0
)


