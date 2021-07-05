package com.example.ui.state

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface UserStateContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setStatesUI(states: List<StateItemModel>)
    }
    interface Presenter : BaseContract.Presenter {
        fun onClickClose()
    }
}