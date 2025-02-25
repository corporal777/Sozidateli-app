package com.example.ui.views.filters.event.my

import com.example.ui.views.filters.BaseFiltersBottomSheetContract
import moxy.viewstate.strategy.alias.Skip

interface MyEventsFiltersBottomSheetContract {
    interface View : BaseFiltersBottomSheetContract.View {

        @Skip
        fun initTextFilter()

        @Skip
        fun initDateFilter()

        @Skip
        fun initAddressFilter()
    }

    interface Presenter : BaseFiltersBottomSheetContract.Presenter {

    }
}