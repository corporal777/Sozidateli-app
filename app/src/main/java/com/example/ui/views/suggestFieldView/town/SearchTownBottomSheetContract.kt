package com.example.ui.views.suggestFieldView.town

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SearchTown
import com.example.ui.base.BaseContract

class SearchTownBottomSheetContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTowns(list: List<SearchTown?>)

        @StateStrategyType(SkipStrategy::class)
        fun performOnItemSelected(item: SearchTown?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onTownChange(town: String)
        fun onTownSelected(town: String?)
    }
}