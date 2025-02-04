package com.example.ui.search.organization

import androidx.paging.PagingData
import com.example.data.models.OrganizationNew
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface SearchOrganizationContract {
    interface View : SearchContract.View<SearchFilter.Organization> {
        @OneExecution
        fun setData(data: PagingData<OrganizationNew>)

        @OneExecution
        fun showOrganization(organization: OrganizationNew)

        @OneExecution
        fun updateOrganization(organization: OrganizationNew)
    }

    interface Presenter : SearchContract.Presenter<SearchFilter.Organization> {
        fun onOrganizationClick(organization: OrganizationNew)
        fun onOrganizationSubscriptionClick(org: OrganizationNew)
    }
}
