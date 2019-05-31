package com.example.ui.views.suggestFieldView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.DataDataItem
import com.example.data.models.DataDataResponse
import com.example.ui.base.BaseContract

class SuggestFieldViewContract {

    interface View : MvpView {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSuggested(list: List<DataDataItem>)
    }

    interface Presenter : BaseContract.Presenter{
        fun onQueryChange(query:String)
    }
}