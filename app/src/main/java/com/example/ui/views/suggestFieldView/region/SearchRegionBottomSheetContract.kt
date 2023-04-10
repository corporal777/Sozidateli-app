package com.example.ui.views.suggestFieldView.region

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.NewEventFormat
import com.example.data.models.SearchRegion
import com.example.ui.base.BaseContract

class SearchRegionBottomSheetContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setRegions(list: List<SearchRegion>)

        @StateStrategyType(SkipStrategy::class)
        fun performOnItemSelected(item: SearchRegion?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onRegionChange(region: String)
        fun onRegionSelected(region: String?)
    }

}