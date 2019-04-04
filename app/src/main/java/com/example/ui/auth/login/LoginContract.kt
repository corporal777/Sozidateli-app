package com.example.ui.auth.login

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract
import com.example.ui.snAuth.SnType

interface LoginContract {
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
        fun showSocialNetworkSetEmail(snType:SnType,email:String?,token:String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showNeedConfirmEmailDialog(email: String?)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickVk()
        fun onClickFb()
        fun onClickOk()
        fun onClickEmail()
        fun onClickSetSocialNetworkEmail(snType: SnType,email:String,token:String)
    }
}
