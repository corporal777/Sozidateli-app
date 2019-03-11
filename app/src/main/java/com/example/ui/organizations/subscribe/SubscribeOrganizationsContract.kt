package com.example.ui.organizations.subscribe

import com.example.data.models.Organization
import com.example.ui.organizations.OrganizationsContract

interface SubscribeOrganizationsContract {
    interface View : OrganizationsContract.View

    interface Presenter : OrganizationsContract.Presenter {
        fun onSubscribeChangeClick(organization: Organization)
    }
}
