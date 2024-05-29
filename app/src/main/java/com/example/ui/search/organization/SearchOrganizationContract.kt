package com.example.ui.search.organization

import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution

interface SearchOrganizationContract {
    interface View : SearchContract.View<OrganizationNew, SearchFilter.Organization> {
        @OneExecution
        fun showOrganization(organization: OrganizationNew)

        @OneExecution
        fun changeSubscription(organization: OrganizationNew)
    }

    interface Presenter : SearchContract.Presenter<OrganizationNew> {
        fun onOrganizationClick(organization: OrganizationNew)
        fun onOrganizationSubscriptionClick(org: OrganizationNew)
    }
}
