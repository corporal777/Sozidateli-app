package com.example.ui.organizations.list

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.OneExecutionByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface OrganizationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "header")
        fun setNoFilterHeader()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "header")
        fun setFavoritesHeader()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setOrganizations(organizations: List<Organization?>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showNoFilterEmptyListPlaceholder()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showFavoritesEmptyListPlaceholder()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showOrganization(organization: Organization)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFavorites()
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onOrganizationClick(organization: Organization)
        fun onRemoveFromFavoriteClick(organization: Organization)
        fun onRefreshRequest()
        fun onFavoritesClick()
    }
}
