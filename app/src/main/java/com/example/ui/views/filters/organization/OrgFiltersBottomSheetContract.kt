package com.example.ui.views.filters.organization

import com.example.ui.views.filters.BaseFiltersBottomSheetContract
import moxy.viewstate.strategy.alias.Skip

interface OrgFiltersBottomSheetContract {
    interface View : BaseFiltersBottomSheetContract.View {

        @Skip
        fun initOrganizationName()

        @Skip
        fun initOrganizationInn()

        @Skip
        fun initRegions()

        @Skip
        fun initTowns()
    }

    interface Presenter : BaseFiltersBottomSheetContract.Presenter {

    }
}