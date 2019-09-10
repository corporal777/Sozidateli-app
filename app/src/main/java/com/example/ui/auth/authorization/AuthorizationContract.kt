package com.example.ui.auth.authorization

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.base.BaseContract

interface AuthorizationContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startVkAuthorization()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startFbAuthorization()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun startOkAuthorization()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLogin()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegistration(snUser: SnUser? = null)
    }

    interface Presenter : BaseContract.Presenter {
        fun onVkClick()
        fun onFbClick()
        fun onOkClick()
        fun onEmailClick()
        fun onLoginClick()
    }
}
