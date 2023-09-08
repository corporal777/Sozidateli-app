package com.example.ui.views.suggestFieldView.settlement

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SearchRegion
import com.example.ui.base.BaseContract

class SearchSettlementBottomSheetContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSettlements(list: List<SearchRegion?>)

        @StateStrategyType(SkipStrategy::class)
        fun performOnItemSelected(item: SearchRegion?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSettlementChange(settlement: String)
        fun onSettlementSelected(settlement: String?)
    }
}