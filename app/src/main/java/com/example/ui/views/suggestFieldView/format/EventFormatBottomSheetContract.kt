package com.example.ui.views.suggestFieldView.format

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.NewEventFormat
import com.example.ui.base.BaseContract

class EventFormatBottomSheetContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setFormats(list: List<NewEventFormat>)

        @StateStrategyType(SkipStrategy::class)
        fun performOnItemSelected(item: NewEventFormat?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onFormatChange(format: String)
        fun onFormatSelected(format: String?)
    }
}