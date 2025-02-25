package com.example.ui.views.filters.chat

import com.example.data.models.NewEventFormat
import com.example.ui.views.filters.BaseFiltersBottomSheetContract
import moxy.viewstate.strategy.alias.Skip

interface ChatFiltersBottomSheetContract {
    interface View : BaseFiltersBottomSheetContract.View {
        @Skip
        fun initDaDataAddress()
    }

    interface Presenter : BaseFiltersBottomSheetContract.Presenter {

    }
}