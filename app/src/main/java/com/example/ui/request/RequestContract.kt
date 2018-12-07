package com.example.ui.request

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface RequestContract {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableActionButton(enable: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onCloseClick()
    }
}
