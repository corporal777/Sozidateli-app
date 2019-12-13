package com.example.ui.search.organization

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.data.models.SearchFilter
import com.example.ui.search.SearchContract

interface SearchOrganizationContract {
    interface View : SearchContract.View<Organization, SearchFilter.Organization> {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: Organization)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeSubscription(organization: Organization)
    }

    interface Presenter : SearchContract.Presenter<Organization> {
        fun onOrganizationClick(organization: Organization)
        fun onOrganizationSubscriptionClick(organization: Organization)
    }
}
