package com.example.ui.views.filters.user

import com.example.ui.views.filters.BaseFiltersBottomSheetContract
import moxy.viewstate.strategy.alias.Skip

interface UserFiltersBottomSheetContract {
    interface View : BaseFiltersBottomSheetContract.View {

        @Skip
        fun initAgeFrom()

        @Skip
        fun initAgeTo()

        @Skip
        fun initRegions()

        @Skip
        fun initTowns()
    }

    interface Presenter : BaseFiltersBottomSheetContract.Presenter {

    }
}