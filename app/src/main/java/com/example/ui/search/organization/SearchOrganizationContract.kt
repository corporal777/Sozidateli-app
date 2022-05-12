package com.example.ui.search.organization

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract

interface SearchOrganizationContract {
    interface View : SearchContract.View<OrganizationNew/*Organization*/, SearchFilter.OrganizationNew/*Organization*/> {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: OrganizationNew/*Organization*/)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeSubscription(organization: OrganizationNew/*Organization*/)
    }

    interface Presenter : SearchContract.Presenter<OrganizationNew/*Organization*/> {
        fun onOrganizationClick(organization: OrganizationNew/*Organization*/)
        fun onOrganizationSubscriptionClick(organization: OrganizationNew/*Organization*/)
    }
}
