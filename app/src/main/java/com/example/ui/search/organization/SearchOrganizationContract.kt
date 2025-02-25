package com.example.ui.search.organization

import androidx.paging.PagingData
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchOrganizationContract {
    interface View : SearchContract.View {
        @OneExecution
        fun setData(data: PagingData<OrganizationNew>)

        @OneExecution
        fun showOrganization(organization: OrganizationNew)

        @Skip
        fun updateOrganization(organization: OrganizationNew)

        @Skip
        fun showFilter(filter: SearchFilter.Organization)
    }

    interface Presenter : SearchContract.Presenter {
        fun onOrganizationClick(organization: OrganizationNew)
        fun onOrganizationSubscriptionClick(org: OrganizationNew)
        fun onFiltersApplyClick(filter: SearchFilter.Organization)
    }
}
