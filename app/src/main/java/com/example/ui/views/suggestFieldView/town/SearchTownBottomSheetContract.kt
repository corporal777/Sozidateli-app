package com.example.ui.views.suggestFieldView.town

import com.example.data.models.SearchTown
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

class SearchTownBottomSheetContract {

    interface View : MvpView {
        @OneExecution
        fun setTowns(list: List<SearchTown?>)

        @Skip
        fun performOnItemSelected(item: SearchTown?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onTownChange(town: String)
        fun onTownSelected(town: String?)
    }
}