package com.example.ui.auth.authorization

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.ui.snAuth.SnType

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
        fun showSocialNetworkSetEmail(snType: SnType, email: String?, token: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showNeedConfirmEmailDialog(email: String?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onVkClick()
        fun onFbClick()
        fun onOkClick()
        fun onEmailClick()
        fun onLoginClick()

        fun onClickSetSocialNetworkEmail(snType: SnType, email: String, token: String)
    }
}
