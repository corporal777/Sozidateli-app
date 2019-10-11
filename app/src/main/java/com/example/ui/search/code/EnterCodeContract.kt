package com.example.ui.search.code

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface EnterCodeContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvent(event: Event)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEventNotFoundError()
    }

    interface Presenter : BaseContract.Presenter {
        fun onSearchClick(code: String)
    }
}
