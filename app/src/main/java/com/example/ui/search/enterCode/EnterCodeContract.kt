package com.example.ui.search.enterCode

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface EnterCodeContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent(event: Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSearchClick(code: String)
    }
}
