package com.example.ui.views.suggestFieldView.organization

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.NewEventFormat
import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract

class EventOrgBottomSheetContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setOrganizations(list: List<OrganizationNew>)

        @StateStrategyType(SkipStrategy::class)
        fun performOnItemSelected(item: OrganizationNew?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onOrganizationChange(name: String)
        fun onOrganizationSelected(name: String)
    }
}