package com.example.ui.views.suggestFieldView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.DaDataItem
import com.example.data.models.NewUserAddress
import com.example.ui.base.BaseContract

class DaDataAutoCompleteTextViewContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSuggested(list: List<NewUserAddress>)

        @StateStrategyType(SkipStrategy::class)
        fun performOnItemSelected(item: NewUserAddress)
    }

    interface Presenter : BaseContract.Presenter {
        fun onQueryChange(query: String)
        fun onItemSelected(position: Int)
    }
}