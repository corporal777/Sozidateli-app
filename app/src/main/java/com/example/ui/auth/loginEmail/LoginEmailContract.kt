package com.example.ui.auth.loginEmail

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface LoginEmailContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWelcome()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegister()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableLoginBtn(isEnable: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickBack()
        fun onChangeEmailText(email: String)
        fun onChangePasswordText(password: String)
        fun onClickLogin(email: String, password: String)
        fun onClickRegister()
    }
}
