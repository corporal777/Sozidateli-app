package com.example.ui.organizations.favorites

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface FavoriteOrganizationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setOrganizations(organizations: List<Organization?>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: Organization)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onOrganizationClick(organization: Organization)
        fun onRemoveFromFavoriteClick(organization: Organization)
        fun onRefreshRequest()
    }
}
