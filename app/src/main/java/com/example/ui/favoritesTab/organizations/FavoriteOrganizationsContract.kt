package com.example.ui.favoritesTab.organizations

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface FavoriteOrganizationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setOrganizations(organizations: List<OrganizationNew/*Organization*/?>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showFavoritesEmptyListPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: OrganizationNew/*Organization*/)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun changeSubscription(organization: OrganizationNew/*Organization*/)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onOrganizationClick(organization: OrganizationNew/*Organization*/)
        fun onRemoveFromFavoriteClick(organization: OrganizationNew/*Organization*/)
        fun onRefreshRequest()
    }
}
