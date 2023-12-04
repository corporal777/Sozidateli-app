package com.example.ui.views.filters

import com.example.data.models.InterestNew
import com.example.data.models.NewEventFormat
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface BaseFiltersBottomSheetContract {
    interface View : MvpView {

        @Skip
        fun initInterests(interests: Map<InterestNew, List<InterestNew>>)

        @Skip
        fun initFormats(formats: List<NewEventFormat>)
    }

    interface Presenter {

    }

}