package com.example.ui.state

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface UserStateContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setStatesUI(states: List<StateItemModel>)
    }
    interface Presenter : BaseContract.Presenter {
        fun onClickClose()
    }
}