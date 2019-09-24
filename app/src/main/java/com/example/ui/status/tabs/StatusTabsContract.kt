package com.example.ui.status.tabs

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface StatusTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectTab(position: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onAnonymousSelected()
        fun onProtectedSelected()
        fun onMaximumSelected()
        fun onCloseClick()
    }
}
