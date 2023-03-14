package com.example.ui.search.organization

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract

interface SearchOrganizationContract {
    interface View : SearchContract.View<OrganizationNew, SearchFilter.Organization> {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: OrganizationNew)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeSubscription(organization: OrganizationNew)
    }

    interface Presenter : SearchContract.Presenter<OrganizationNew> {
        fun onOrganizationClick(organization: OrganizationNew)
        fun onOrganizationSubscriptionClick(organization: OrganizationNew)
    }
}
