package com.example.ui.views.suggestFieldView.region

import com.example.data.models.SearchRegion
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

class SearchRegionBottomSheetContract {

    interface View : MvpView {
        @OneExecution
        fun setRegions(list: List<SearchRegion>)

        @Skip
        fun performOnItemSelected(item: SearchRegion?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onRegionChange(region: String)
        fun onRegionSelected(region: String?)
    }

}