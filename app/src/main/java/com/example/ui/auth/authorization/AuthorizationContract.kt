package com.example.ui.auth.authorization

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface AuthorizationContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLogin()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegistration(snUser: SnUser? = null)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onEmailClick()
        fun onLoginClick()
    }
}
