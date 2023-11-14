package com.example.ui.views.suggestFieldView.address

import com.example.data.models.NewUserAddress
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

class DaDataAutoCompleteTextViewContract {

    interface View : MvpView {
        @AddToEndSingle
        fun setSuggested(list: List<NewUserAddress>)

        @Skip
        fun performOnItemSelected(item: NewUserAddress)
    }

    interface Presenter : BaseContract.Presenter {
        fun onQueryChange(query: String)
        fun onItemSelected(position: Int)
    }
}