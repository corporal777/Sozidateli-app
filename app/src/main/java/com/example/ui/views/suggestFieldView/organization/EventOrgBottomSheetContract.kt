package com.example.ui.views.suggestFieldView.organization

import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

class EventOrgBottomSheetContract {

    interface View : MvpView {
        @OneExecution
        fun setOrganizations(list: List<OrganizationNew>)

        @Skip
        fun performOnItemSelected(item: OrganizationNew?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onOrganizationChange(name: String)
        fun onOrganizationSelected(name: String)
    }
}