package com.example.ui.views.filters.event

import com.example.data.models.OrganizationNew
import com.example.ui.views.filters.BaseFiltersBottomSheetContract
import moxy.viewstate.strategy.alias.Skip

interface EventFiltersBottomSheetContract {
    interface View : BaseFiltersBottomSheetContract.View {

        @Skip
        fun initTextFilter()

        @Skip
        fun initDateStart()

        @Skip
        fun initDateEnd()

        @Skip
        fun initRegions()

        @Skip
        fun initTowns()

        @Skip
        fun initOrganizations(organizations: List<OrganizationNew>?)
    }

    interface Presenter : BaseFiltersBottomSheetContract.Presenter {

    }
}