package com.example.ui.auth.loginEmail

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface LoginEmailContract {
    interface View : BaseContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmailAndPassword(email: String, password: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailConfirmDialog(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegister()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableLoginBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRecoveryPassword(email: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickBack()
        fun onChangeEmailText(email: String)
        fun onChangePasswordText(password: String)
        fun onClickLogin()
        fun onClickRegister()
        fun onClickRecoverPassword()
    }
}
