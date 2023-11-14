package com.example.ui.favoritesTab.organizations

import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface FavoriteOrganizationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setOrganizations(organizations: List<OrganizationNew/*Organization*/?>)

        @AddToEndSingle
        fun showFavoritesEmptyListPlaceholder()

        @OneExecution
        fun showOrganization(organization: OrganizationNew/*Organization*/)

        @OneExecution
        fun changeSubscription(organization: OrganizationNew/*Organization*/)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onOrganizationClick(organization: OrganizationNew/*Organization*/)
        fun onRemoveFromFavoriteClick(organization: OrganizationNew/*Organization*/)
        fun onRefreshRequest()
    }
}
