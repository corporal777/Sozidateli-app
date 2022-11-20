package com.example.ui.organizations.redesign.data

import android.provider.ContactsContract
import com.example.data.models.EventNew
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNew

data class OrgLocalDataNew(
    val organization: OrganizationNew,
    val events: List<EventNew>?,
    val member: List<OrganizationMemberModel>?,
    val membersSize: Int = 0
)


