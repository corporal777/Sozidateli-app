package com.example.ui.views.filters.event

import com.example.data.models.InterestNew
import com.example.data.models.NewEventFormat
import com.example.ui.views.filters.BaseFiltersBottomSheetContract
import com.example.ui.views.filters.BaseFiltersBottomSheetPresenter
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventFiltersBottomSheetContract {
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