package com.example.ui.favoritesTab.organizations

import androidx.paging.PagingData
import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface FavoriteOrganizationsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(data: PagingData<OrganizationNew>)

        @Skip
        fun updateOrganization(organization: OrganizationNew)

        @OneExecution
        fun showOrganization(organization: OrganizationNew)
    }

    interface Presenter : BaseContract.Presenter {
        fun onOrganizationClick(organization: OrganizationNew)
        fun onRemoveFromFavoriteClick(organization: OrganizationNew)
        fun onRefreshRequest()
    }
}
